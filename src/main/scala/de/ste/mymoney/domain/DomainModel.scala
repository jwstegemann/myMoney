package de.ste.mymoney.domain

import com.mongodb.casbah.Imports._
import com.mongodb.BasicDBObject

import org.scala_tools.time.Imports._

import de.ste.mymoney.util.MyLocalDate
import de.ste.mymoney.util.MyLocalDate._

object Expense {
	implicit def fromMongo(expenseDbo : DBObject) : Expense = {
		Expense(
			Some(expenseDbo.as[ObjectId]("_id").toString()),
			expenseDbo.getAs[String]("name").getOrElse(""),
			expenseDbo.getAs[Double]("value").getOrElse(0.0),
			expenseDbo.getAs[Int]("recurrence").getOrElse(0),
			expenseDbo.getAs[String]("description").getOrElse(""),
			expenseDbo.as[DateTime]("from"),
			expenseDbo.getAs[DateTime]("to"),
			expenseDbo.getAs[String]("ref")
		)
	}
	
	implicit def toMongo(expense : Expense) : DBObject = {
		val dbo = MongoDBObject.newBuilder
			dbo += "name" -> expense.name
			dbo += "value" -> expense.value
			dbo += "recurrence" -> expense.recurrence
			dbo += "description" -> expense.description
			dbo += "from" -> (expense.from : DateTime)
			if (! expense.to.isEmpty) dbo += "to" -> (expense.to.get : DateTime)
			if (! expense.ref.isEmpty) dbo += "ref" -> expense.ref.get
		dbo.result
	}

	val SINGLETON = 0;
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
	to : Option[LocalDate],
	ref : Option[String]
)

/*
 * Balance
 */
object Balance {
	implicit def fromMongo(balanceDbo : DBObject) : Balance = {
		Balance(
			Some(balanceDbo.as[ObjectId]("_id").toString()),
			balanceDbo.as[DateTime]("date"),
			balanceDbo.getAs[Double]("value").getOrElse(0.0),
			balanceDbo.getAs[String]("description").getOrElse("")
		)
	}
	
	implicit def toMongo(balance : Balance) : DBObject = {
		val dbo = MongoDBObject.newBuilder
			dbo += "date" -> (balance.date : DateTime)
			dbo += "value" -> balance.value
			dbo += "description" -> balance.description
		dbo.result
	}
}

case class Balance (
	id : Option[String],
	date : LocalDate,
	value : Double,
	description : String
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
