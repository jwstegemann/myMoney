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
import org.joda.time.format.ISODateTimeFormat
import org.joda.time.DateTime



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
	
	val dtf = ISODateTimeFormat.date();
	
	def analyze() = {
		val latestBalanceCursor = balancesCollection.find().sort(MongoDBObject("date" -> -1))
		if (!latestBalanceCursor.hasNext) throw new Exception("no balance defined yet.")
		val latestBalance = latestBalanceCursor.next
			
		val startDate = new org.joda.time.LocalDate(latestBalance.as[String]("date"))
		val endDate = startDate.plusYears(5)
		var saldo = latestBalance.getAs[Double]("value").getOrElse(0.0)

		val query : DBObject = "from" $gte (startDate : org.joda.time.DateTime) $lte (endDate : org.joda.time.DateTime)
		
		val mongoResult = expensesCollection.mapReduce(AnalyzeMapReduce.map, AnalyzeMapReduce.reduce, MapReduceInlineOutput, Some(query))
		
		val result = new StringBuffer("date,saldo\n")
		
		for (resultItem <- mongoResult) {
			dtf.printTo(result, resultItem.as[DateTime]("_id"))
			saldo += resultItem.as[DBObject]("value").as[Double]("saldo")
			result append "," append saldo append "\n"
		}

		//TODO: is a stream possible
		result.toString
	}
	
}