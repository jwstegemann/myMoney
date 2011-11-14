package de.ste.mymoney.protocol

import cc.spray.json._
import de.ste.mymoney.domain._

object MyMoneyJsonProtocol extends DefaultJsonProtocol {
  implicit val expenseFormat = jsonFormat(Expense.apply, "id", "name", "value")     
  implicit val resultFormat = jsonFormat(Result.apply, "status", "msg")     
}
