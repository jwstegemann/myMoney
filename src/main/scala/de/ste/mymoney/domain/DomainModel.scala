package de.ste.mymoney.domain

import com.mongodb.casbah.Imports._

case class Expense(
	name : String,
	value : Double
)



case class Result(
	status : Int,
	msg : String
)