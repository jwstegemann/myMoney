package de.ste.mymoney.util

import org.scala_tools.time.Imports._
import scala.collection.mutable.ListBuffer

class MyLocalDate(val underlying : LocalDate)  {
	def until(endDate : LocalDate) = {
	
		var dates = new ListBuffer[LocalDate]()
		
		var date : LocalDate = underlying
		
		while (!date.isAfter(endDate))
		{
			dates += date
			date += MyLocalDate.oneDay
		}

		dates
	}
}

object MyLocalDate {	
	val oneDay : Period = Period.days(1)
	
	implicit def fromLocalDate(date : LocalDate) = new MyLocalDate(date)
}