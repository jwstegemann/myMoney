package de.ste.mymoney.domain

import com.mongodb.casbah.Imports._
import com.mongodb.BasicDBObject

import org.scala_tools.time.Imports._

object Expense {
	implicit def fromMongo(expenseDbo : DBObject) : Expense = {
		Expense(
			Some(expenseDbo.as[ObjectId]("_id").toString()),
			expenseDbo.getAs[String]("name").getOrElse(""),
			expenseDbo.getAs[Double]("value").getOrElse(0.0),
			expenseDbo.getAs[Int]("recurrence").getOrElse(0),
			expenseDbo.getAs[String]("description").getOrElse(""),
			expenseDbo.as[DateTime]("from").toLocalDate(),
			expenseDbo.as[DateTime]("to").toLocalDate()
		)
	}
	
	implicit def toMongo(expense : Expense) : DBObject = {
		val dbo = MongoDBObject.newBuilder
			dbo += "name" -> expense.name
			dbo += "value" -> expense.value
			dbo += "recurrence" -> expense.recurrence
			dbo += "description" -> expense.description
			dbo += "from" -> expense.from.toDateTimeAtStartOfDay(DateTimeZone.UTC)
			dbo += "to" -> expense.from.toDateTimeAtStartOfDay(DateTimeZone.UTC)
		dbo.result
	}

	val SINGLETON = 0;
	val WEEKLY = 52;
	val MONTHLY = 12;
	val QUARTERLY = 4;
	val YEARLY = 1;
}

case class Expense (
	id : Option[String],
	name : String,
	value : Double,
	recurrence : Int,
	//TODO: make option from this one
	description : String,
	//TODO: add tags
	from : LocalDate,
	to : LocalDate
)

/*
 * AnalyzeRequest and Result
 */
case class AnalyzeRequest (
	startSaldo : Double,
	startDate : LocalDate,
	endDate : LocalDate
)

object AnalyzeResult {
	implicit def fromMongo(analyzeDbo : DBObject) : AnalyzeResult = {
		val value = analyzeDbo.as[DBObject]("value")
	
		AnalyzeResult(
			analyzeDbo.as[DateTime]("_id").toLocalDate(),
			value.getAs[Double]("saldo").getOrElse(32.0)
		)
	}
}

case class AnalyzeResult (
	date : LocalDate,
	saldo : Double
)
