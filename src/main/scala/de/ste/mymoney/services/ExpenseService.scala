package de.ste.mymoney.services

import cc.spray._
import cc.spray.json._
import typeconversion.SprayJsonSupport
import cc.spray.directives._
import cc.spray.http._
import cc.spray.http.StatusCodes._

import de.ste.mymoney.domain._
import de.ste.mymoney.protocol.MyMoneyJsonProtocol._

import com.mongodb.casbah.Imports._
import com.mongodb.casbah.commons.conversions.scala._

import org.scala_tools.time.Imports._
import de.ste.mymoney.util.MyLocalDate
import de.ste.mymoney.util.MyLocalDate._
import scala.collection.mutable.ListBuffer



trait ExpenseService extends Directives with SprayJsonSupport {

	RegisterJodaTimeConversionHelpers()

	val mongoCon = MongoConnection("localhost")
	val expensesCollection : MongoCollection = mongoCon("test")("expenses")
	
	val expenseService = {
		pathPrefix("expense") {
			get {
				path(Remaining) { id =>
					_.complete(load(id))
				}
			} ~
			(put & path("")) {
				content(as[Expense]) { expense =>
					create(expense)
					_.complete(HttpResponse(OK))
				}
			} ~
			(post) {
				path(Remaining) { id =>
					content(as[Expense]) { expense =>
						update(id, expense)
						_.complete(HttpResponse(OK))
					}
				}
			} ~
			(delete) {
				path(Remaining) { id =>
					delete(id)
					_.complete(HttpResponse(OK))
				}
			}
		} ~
		path("expenses") {
			get {
				_.complete(find());
			}
		} ~
		(path("analyze") & post) { 
			content(as[AnalyzeRequest]) { request =>
				_.complete(analyze(request))
			}
		}
	}
	
	
	/*
	 * CRUD - methods
	 */ 
	
	def create(expense : Expense) = {
		expense.recurrence match {
			case Expense.SINGLETON => saveSingletonExpense(expense)
			case recurrence : Int => saveRecurrentExpense(expense)
		}
	}
	
	def load(id : String) : Expense = {
		val query : DBObject = MongoDBObject("_id" -> new ObjectId(id))

		expensesCollection.findOne(query) match {
			case Some(expenseDbo : BasicDBObject) => expenseDbo
			case None => throw new Exception("expense with id " + id + " does not exist.")
		}
	}
	
	def find() = {
		val cursor = expensesCollection.find();
		for { expenseDbo <- cursor.toSeq } yield (expenseDbo : Expense)
	}
	
	def delete(id : String) = {
		//TODO: check if das Element selbst ein ref hat!
		val query : DBObject = $or(("_id" -> new ObjectId(id)), ("ref" -> id))
		val writeResult  = expensesCollection.remove(query)
		if (writeResult.getN == 0) throw new Exception("requested entity could not be deleted")
	}
	
	def update(id : String, expense : Expense) = {
		//TODO: check if das Element selbst ein ref hat!
		
		//delete all refs
		val queryDeleteAll : DBObject = MongoDBObject("ref" -> id)
		val writeResultDeleteRefs = expensesCollection.remove(queryDeleteAll)
		
		println("deleted refs: " + writeResultDeleteRefs.getN)
		
		//update entity
		val queryUpdate : DBObject = MongoDBObject("_id" -> new ObjectId(id))
		val writeResultUpdate = expensesCollection.update(queryUpdate,expense)

		println("updated " + writeResultUpdate.getN)

		//create new refs
		if (writeResultUpdate.getN == 0)
		{
			throw new Exception("requested entity could not be updated")
		}
		else if (expense.recurrence != Expense.SINGLETON)
		{
			createRecurrentInstances(id, expense)
		}
	}
		
		
	/*
	 * application-specific methods
	 */
		
	def analyze(request : AnalyzeRequest) = {
		//FIXME: test if perios is narrow enough (<100 days?)
	
		val query : DBObject = "from" $gte (request.startDate : org.joda.time.DateTime) $lte (request.endDate : org.joda.time.DateTime)
		
		val mongoResult = expensesCollection.mapReduce(AnalyzeMapReduce.map, AnalyzeMapReduce.reduce, MapReduceInlineOutput, Some(query))
		
		var resultItem : Option[AnalyzeResult] =   if (mongoResult.hasNext) Some(mongoResult.next) else None
		var saldo : Double = request.startSaldo
		
		val analyzeResults = new ListBuffer[AnalyzeResult]()
		
		for (date <- request.startDate until request.endDate) {
			if ((resultItem isEmpty) || (resultItem.get.date != date))
			{
				analyzeResults += AnalyzeResult(date,saldo)
			}
			else
			{
				saldo += resultItem.get.saldo
				//TODO: hier spaeter die Buchungen hinzufuegen
				analyzeResults += AnalyzeResult(date,saldo)
				resultItem = if (mongoResult.hasNext) Some(mongoResult.next) else None
			}
		}

		//FIXME: Das muesste doch auch ohne Kopieren gehen, oder?
		analyzeResults.toArray
	}
	
	def saveSingletonExpense(expenseDbo : DBObject) : ObjectId = {
		val writeResult = expensesCollection += expenseDbo
		if (writeResult.getError != null) throw new Exception("Error writing recurrent instances")

		expenseDbo.as[ObjectId]("_id")
	}
	
	def saveRecurrentExpense(expense : Expense) = {
		//FIXME: automatische Fortschreibung von Eintragungen ohne Enddatum
			
		val refObjectId = saveSingletonExpense(expense)
		createRecurrentInstances(refObjectId.toString(), expense)
	}
	
	def createRecurrentInstances(refId : String, expense : Expense) = {
		val endDate = expense.to match {
			case Some(date) => date
			case None => new LocalDate() + Period.years(5)
		}
	
		for (date <- expense.from until(endDate, expense.recurrence, start=1)) {
			val clonedExpense = expense.copy(to = None, recurrence = 0, from = date, ref = Some(refId))
			
			//TODO: write all instances in one row?
			val writeResult = expensesCollection += clonedExpense
			
			if (writeResult.getError != null) throw new Exception("Error writing recurrent instances")
		}
	}

}