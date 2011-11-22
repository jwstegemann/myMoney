package de.ste.mymoney.services

import cc.spray._
import cc.spray.json._
import typeconversion.SprayJsonSupport
import cc.spray.directives._
import cc.spray.http._
import cc.spray.http.StatusCodes._

import de.ste.mymoney.domain._
import de.ste.mymoney.protocol.MyMoneyJsonProtocol._

import com.mongodb.casbah.Imports._
import com.mongodb.casbah.commons.conversions.scala._

import org.scala_tools.time.Imports._
import de.ste.mymoney.util.MyLocalDate
import de.ste.mymoney.util.MyLocalDate._
import scala.collection.mutable.ListBuffer



trait AnalyzeService extends Directives {

	RegisterJodaTimeConversionHelpers()

	val mongoCon = MongoConnection("localhost")
	val expensesCollection : MongoCollection = mongoCon("myMoney")("transactions")
	val balancesCollection : MongoCollection = mongoCon("myMoney")("balances")
	
	val analyzeService = {
		(path("rest/analyze") & get) { 
			_.complete(analyze())
		}
	}
	
	def analyze() = {
		val latestBalanceCursor = balancesCollection.find().sort(MongoDBObject("date" -> -1))
		if (!latestBalanceCursor.hasNext) throw new Exception("no balance defined yet.")
		val latestBalance = latestBalanceCursor.next
			
		val startDate = new org.joda.time.LocalDate(latestBalance.as[String]("date"))
		val endDate = startDate.plusYears(5)
		val startSaldo = latestBalance.as[Double]("value")

		println("startDate:" + startDate)
		println("endDate:" + endDate)
		println("startSaldo:" + startSaldo)
		
		val query : DBObject = "from" $gte (startDate : org.joda.time.DateTime) $lte (endDate : org.joda.time.DateTime)
		
		val mongoResult = expensesCollection.mapReduce(AnalyzeMapReduce.map, AnalyzeMapReduce.reduce, MapReduceInlineOutput, Some(query))
		
		var resultItem : Option[AnalyzeResult] =   if (mongoResult.hasNext) Some(mongoResult.next) else None
		var saldo : Double = startSaldo
		
		val analyzeResults = new ListBuffer[AnalyzeResult]()
		
		val result = new StringBuilder()
		result append "date" append "," append "saldo" append "\n"
		
		for (date <- startDate until endDate) {
			if ((resultItem isEmpty) || (resultItem.get.date != date))
			{
				result append date append "," append saldo append "\n"
			}
			else
			{
				saldo += resultItem.get.saldo
				result append date append "," append saldo append "\n"
				resultItem = if (mongoResult.hasNext) Some(mongoResult.next) else None
			}
		}

		//TODO: is a stream possible
		result.toString
	}
	
}