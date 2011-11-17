package de.ste.mymoney.util

import org.scala_tools.time.Imports._
import scala.collection.mutable.ListBuffer

class MyLocalDate(val underlying : LocalDate)  {
	def until(endDate : LocalDate, step : Period = MyLocalDate.oneDay) = {
	
		var dates = new ListBuffer[LocalDate]()
		
		var date : LocalDate = underlying
		
		while (!date.isAfter(endDate))
		{
			dates += date
			date += step
		}

		dates
	}
}

object MyLocalDate {	
	val oneDay : Period = Period.days(1)
	val oneWeek : Period = Period.weeks(1)
	val oneMonth : Period = Period.months(1)
	val oneQuarter : Period = Period.months(3)
	val oneYear : Period = Period.years(1)
	
	implicit def fromLocalDate(date : LocalDate) = new MyLocalDate(date)
	
	implicit def localDate2DateTime(date : LocalDate) = date.toDateTimeAtStartOfDay(DateTimeZone.UTC)
	implicit def dateTime2LocalDate(date : DateTime) = date.toLocalDate

	implicit def optionDateTime2OptionLocalDate(dateOption : Option[DateTime]) = dateOption match {
		case Some(date) => Some(date.toLocalDate)
		case None => None
	}
	
	implicit def optionLocalDate2OptionDateTime(dateOption : Option[LocalDate]) = dateOption match {
		case Some(date) => Some(date.toDateTimeAtStartOfDay(DateTimeZone.UTC))
		case None => None
	}
}