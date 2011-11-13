package de.ste.mymoney.services

import cc.spray._
import de.ste.mymoney.domain._
import cc.spray.json._
import typeconversion.SprayJsonSupport
import de.ste.mymoney.protocol.MyMoneyJsonProtocol._


trait ExpenseService extends Directives with SprayJsonSupport {
  
  val expenseService = {
	path("expense") {
		get {
			val te = Expense("testname",47.11)
			_.complete(te)
		} ~
		put {
			content(as[Expense]) { expense =>
				_.complete(Result(0,expense.name))
			}
		} ~
		post {
			_.complete(Result(0,""))
		}
	}
  }
  
}