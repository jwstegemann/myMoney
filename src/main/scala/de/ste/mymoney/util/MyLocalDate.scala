package de.ste.mymoney.util

import org.scala_tools.time.Imports._

import org.joda.time.Days
import org.joda.time.Weeks
import org.joda.time.Months
import org.joda.time.Years

import scala.collection.mutable.ListBuffer

class MyLocalDate(val underlying : LocalDate)  {

	val DAILY = 365;
	val WEEKLY = 52;
	val MONTHLY = 12;
	val QUARTERLY = 4;
	val YEARLY = 1;
	
	//FIXME: add value to message
	private val BAD_RECURRENCE_MSG = "recurrence value is not available"

	def until(endDate : LocalDate, step : Int = DAILY, start : Int = 0) = {
	
		var dates = new ListBuffer[LocalDate]()
		
		var amount = step match {
			case DAILY => Days.daysBetween(underlying, endDate).getDays()
			case WEEKLY => Weeks.weeksBetween(underlying, endDate).getWeeks()
			case MONTHLY => Months.monthsBetween(underlying, endDate).getMonths()
			case QUARTERLY => Months.monthsBetween(underlying, endDate).getMonths() / 3
			case YEARLY => Years.yearsBetween(underlying, endDate).getYears()
			case value : Int => throw new Exception(BAD_RECURRENCE_MSG)
		}
		
		for (i <- start to amount) {
			var period = step match {
				//TODO: this might be better with Period instead of Days, Weeks, etc.
				case DAILY => Days.days(i)
				case WEEKLY => Weeks.weeks(i)
				case MONTHLY => Months.months(i)
				case QUARTERLY => Months.months(i * 3)
				case YEARLY => Years.years(i)
				case value : Int => throw new Exception(BAD_RECURRENCE_MSG)
			}

			println("i: " + i)
			
			dates += underlying + period
		}

		
		dates
	}
	
	def recurrencePeriod(recurrence : Int) = {
		recurrence match {
			case DAILY => Days.ONE
			case WEEKLY => Weeks.ONE
			case MONTHLY => Months.ONE
			case QUARTERLY => Months.THREE
			case YEARLY => Years.ONE
			case value : Int => throw new Exception(BAD_RECURRENCE_MSG)
		}
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