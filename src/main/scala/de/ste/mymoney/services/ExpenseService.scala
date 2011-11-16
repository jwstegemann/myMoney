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
			
				analyze(request)			
				_.complete(HttpResponse(OK))
			}
		}
	}
		
	def analyze(request : AnalyzeRequest) = {
		for (date <- request.startDate until request.endDate) {
			println(date)
		}
	}
	
	def saveSingletonExpense(expense : Expense, ctx : RequestContext) = {
		expensesCollection += expense
		ctx.complete(HttpResponse(OK))
	}
	
	def saveRecurrentExpense(expense : Expense, ctx : RequestContext) = {
		println("WEEKLY detected")
		ctx.complete(HttpResponse(OK))
	}

}