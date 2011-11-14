package de.ste.mymoney.domain

import com.mongodb.casbah.Imports._
import com.mongodb.BasicDBObject

object Expense {
	implicit def mongo2Expense(expenseDbo : DBObject) : Expense = {
		Expense(
			Some(expenseDbo.as[ObjectId]("_id").toString()),
			expenseDbo.getAs[String]("name").getOrElse(""),
			expenseDbo.getAs[Double]("value").getOrElse(0.0)
		)
	}
}

case class Expense (
	id : Option[String],
	name : String,
	value : Double
)

case class Result (
	status : Int,
	msg : String
)