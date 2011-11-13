package de.ste.mymoney.domain

case class Expense(
	name : String,
	value : Double
)

case class Result(
	status : Int,
	msg : String
)