package de.ste.mymoney.protocol

import cc.spray.json._
import de.ste.mymoney.domain._

import org.scala_tools.time.Imports._
import org.joda.time.format.DateTimeFormatter

object MyMoneyJsonProtocol extends DefaultJsonProtocol {

  val dtf : DateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd")
  /*
   * use joda DateTime
   */
  implicit object DateTimeJsonFormat extends JsonFormat[LocalDate] {
    def write(date : LocalDate) = {
      JsString(date.toString())
    }
    def read(value: JsValue) = value match {
      case JsString(date) => {
        new LocalDate(date)
      }
      case _ => throw new DeserializationException("Date expected")
    }
  }

  implicit val expenseFormat = jsonFormat(Expense.apply, "id", "name", "value", "recurrence", "description", "from", "to", "ref")
  implicit val balanceFormat = jsonFormat(Balance.apply, "id", "date", "value", "description")
  implicit val analyzeRequestFormat = jsonFormat(AnalyzeRequest.apply, "startSaldo", "startDate", "endDate")
  implicit val analyzeResultFormat = jsonFormat(AnalyzeResult.apply, "date", "saldo")
}
