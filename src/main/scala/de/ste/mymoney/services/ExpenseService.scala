package de.ste.mymoney.services

import cc.spray._
import cc.spray.json._
import typeconversion.SprayJsonSupport
import cc.spray.directives._

import de.ste.mymoney.domain._
import de.ste.mymoney.protocol.MyMoneyJsonProtocol._

import com.mongodb.casbah.Imports._

trait ExpenseService extends Directives with SprayJsonSupport {

  val mongoCon = MongoConnection("localhost")
  val expensesCollection : MongoCollection = mongoCon("test")("expenses")
  
  val expenseService = {
	path("expense" / Remaining) { id =>
		get {
				val query : DBObject = MongoDBObject("_id" -> id)

				// FIXME: test for empty value of the option here
				//val expenseDbo : MongoDBObject = expensesCollection.findOne(query).getOrElse().asInstanceOf[MongoDBObject]
				
				val expenseDbo : MongoDBObject = expensesCollection.findOne(query).getOrElse().asInstanceOf[MongoDBObject]
				
				// TODO: we need to externalize and maybe even generalize this
				_.complete(Expense(expenseDbo.getAs[String]("name").getOrElse(""),expenseDbo.getAs[Double]("value").getOrElse(0.0)))
				
				//_.complete(Expense(id,0.0))
		}
	} ~
	path("expense") {
		put {
			content(as[Expense]) { expense =>
				
				// TODO: we need to externalize and maybe even generalize this
				val dbo = MongoDBObject.newBuilder
					dbo += "name" -> expense.name
					dbo += "value" -> expense.value	
				
				expensesCollection += dbo.result

				_.complete(Result(0,expense.name))
			}
		} ~
		post {
			_.complete(Result(0,""))
		}
	}
  }
  
}