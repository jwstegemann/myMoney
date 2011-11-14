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
import java.util.Date

trait ExpenseService extends Directives with SprayJsonSupport {

	val mongoCon = MongoConnection("localhost")
	val expensesCollection : MongoCollection = mongoCon("test")("expenses")
	
	val expenseService = {
		pathPrefix("expense") {
			get {
				path(Remaining) { id =>
					val query : DBObject = MongoDBObject("_id" -> new ObjectId(id))

					expensesCollection.findOne(query) match {
						// TODO: we need to externalize and maybe even generalize this
						case Some(expenseDbo : BasicDBObject) => _.complete(expenseDbo : Expense)
						case None => _.complete(HttpResponse(NotFound,"expense with id " + id + " does not exist."))
					}
				}
			} ~
			(put & path("")) {
				content(as[Expense]) { expense =>
					// TODO: we need to externalize and maybe even generalize this
					val dbo = MongoDBObject.newBuilder
						dbo += "name" -> expense.name
						dbo += "value" -> expense.value	
				
					expensesCollection += dbo.result

					_.complete(HttpResponse(OK))
				}
			} ~
			(post & path("")) {
				content(as[Expense]) { expense =>
					println("AAAAAAAAAAAAAAAA");
					_.complete(Result(0,"update " + expense.name))
				}
			} ~
			(delete & path("")) {
				content(as[Expense]) { expense =>
					println("AAAAAAAAAAAAAAAA");
					_.complete(Result(0,"delete " + expense.name))
				}
			}
		} ~
		path("expenses") {
			get { ctx =>
				val cursor = expensesCollection.find();
				
				val list = for { expenseDbo <- cursor.toSeq } yield (expenseDbo : Expense)
				
				ctx.complete(list);
			}
		}
	}
}