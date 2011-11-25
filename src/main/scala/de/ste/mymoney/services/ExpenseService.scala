package de.ste.mymoney.services

import cc.spray._
import cc.spray.json._
import typeconversion.SprayJsonSupport
import cc.spray.directives._
import cc.spray.http._
import cc.spray.http.StatusCodes._

import de.ste.mymoney.MongoComponent
import de.ste.mymoney.domain._
import de.ste.mymoney.protocol.MyMoneyJsonProtocol._

import com.mongodb.casbah.Imports._
import com.mongodb.casbah.commons.conversions.scala._

import org.scala_tools.time.Imports._
import de.ste.mymoney.util.MyLocalDate
import de.ste.mymoney.util.MyLocalDate._
import scala.collection.mutable.ListBuffer



trait ExpenseService extends Directives with SprayJsonSupport { this: MongoComponent =>

	RegisterJodaTimeConversionHelpers()

	val expenseService = {
		pathPrefix("rest") {
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
			pathPrefix("expenses") {
				path(Remaining) { query =>
					get {
						_.complete(find(query));
					}
				}
			}
		}
	}
	
	
	/*
	 * CRUD - methods
	 */ 
	
	private def create(expense : Expense) = {
		expense.recurrence match {
			case Expense.SINGLETON => saveSingletonExpense(expense)
			case recurrence : Int => saveRecurrentExpense(expense)
		}
	}
	
	private def load(id : String) : Expense = {
		val query : DBObject = MongoDBObject("_id" -> new ObjectId(id))

		expensesCollection.findOne(query) match {
			case Some(expenseDbo : BasicDBObject) => expenseDbo
			case None => throw new Exception("expense with id " + id + " does not exist.")
		}
	}
	
	private def find(queryString : String) = {
		val queryExists : DBObject = "ref" $exists false
//		println("query: " + queryString)
		val query = queryString match {
			case "singleton" => MongoDBObject("recurrence" -> 0) ++ queryExists
			case "weekly" => MongoDBObject("recurrence" -> 52) ++ queryExists
			case "monthly" => MongoDBObject("recurrence" -> 12) ++ queryExists
			case "quarterly" => MongoDBObject("recurrence" -> 4) ++ queryExists
			case "yearly" => MongoDBObject("recurrence" -> 1) ++ queryExists
			case _ => queryExists
		}
		
		val cursor = expensesCollection.find(query)
		for { expenseDbo <- cursor.toSeq } yield (expenseDbo : Expense)
	}
	
	private def delete(id : String) = {
		//TODO: check if das Element selbst ein ref hat!
		val query : DBObject = $or(("_id" -> new ObjectId(id)), ("ref" -> id))
		val writeResult  = expensesCollection.remove(query)
		if (writeResult.getN == 0) throw new Exception("requested entity could not be deleted")
	}
	
	private def update(id : String, expense : Expense) = {
		//TODO: check if das Element selbst ein ref hat!
		
		//delete all refs
		val queryDeleteAll : DBObject = MongoDBObject("ref" -> id)
		val writeResultDeleteRefs = expensesCollection.remove(queryDeleteAll)
		
//		println("deleted refs: " + writeResultDeleteRefs.getN)
		
		//update entity
		val queryUpdate : DBObject = MongoDBObject("_id" -> new ObjectId(id))
		val writeResultUpdate = expensesCollection.update(queryUpdate,expense)

//		println("updated " + writeResultUpdate.getN)

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
	
	private def saveSingletonExpense(expenseDbo : DBObject) : ObjectId = {
		val writeResult = expensesCollection += expenseDbo
		if (writeResult.getError != null) throw new Exception("Error writing recurrent instances")

		expenseDbo.as[ObjectId]("_id")
	}
	
	private def saveRecurrentExpense(expense : Expense) = {
		//FIXME: automatische Fortschreibung von Eintragungen ohne Enddatum
			
		val refObjectId = saveSingletonExpense(expense)
		createRecurrentInstances(refObjectId.toString(), expense)
	}
	
	private def createRecurrentInstances(refId : String, expense : Expense) = {
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