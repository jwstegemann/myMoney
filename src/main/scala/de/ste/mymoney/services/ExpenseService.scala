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
					val query : DBObject = MongoDBObject("_id" -> new ObjectId(id))

					expensesCollection.findOne(query) match {
						case Some(expenseDbo : BasicDBObject) => _.complete(expenseDbo : Expense)
						case None => _.complete(HttpResponse(NotFound,"expense with id " + id + " does not exist."))
					}
				}
			} ~
			(put & path("")) {
				content(as[Expense]) { expense =>
				
					expense.recurrence match {
						case Expense.SINGLETON => saveSingletonExpense(expense,_)
						case Expense.WEEKLY => saveRecurrentExpense(expense,_)
						case missing : Int => (_ : RequestContext).complete(HttpResponse(BadRequest,"recurrence " + missing + " ist not available"))					}
				}
			} ~
			(post & path("")) {
				content(as[Expense]) { expense =>
					println("AAAAAAAAAAAAAAAA");
					_.complete(HttpResponse(OK))
				}
			} ~
			(delete & path("")) {
				content(as[Expense]) { expense =>
					println("AAAAAAAAAAAAAAAA");
					_.complete(HttpResponse(OK))
				}
			}
		} ~
		path("expenses") {
			get { ctx =>
				val cursor = expensesCollection.find();
				val list = for { expenseDbo <- cursor.toSeq } yield (expenseDbo : Expense)
				ctx.complete(list);
			}
		} ~
		(path("analyze") & post) { 
			content(as[AnalyzeRequest]) { request =>
				_.complete(analyze(request))
			}
		}
	}
		
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
	
	def saveSingletonExpense(expense : Expense, ctx : RequestContext) = {
		expensesCollection += expense
		ctx.complete(HttpResponse(OK))
	}
	
	def saveRecurrentExpense(expense : Expense, ctx : RequestContext) = {
		
	
		//for (date <- expense.from) {
		//}
		
		ctx.complete(HttpResponse(OK))
	}

}