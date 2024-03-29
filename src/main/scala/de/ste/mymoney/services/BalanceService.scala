package de.ste.mymoney.services

import cc.spray._
import cc.spray.json._
import typeconversion.SprayJsonSupport
import cc.spray.directives._
import cc.spray.http._
import cc.spray.http.StatusCodes._

import de.ste.mymoney.MongoComponent
import de.ste.mymoney.domain._
import de.ste.mymoney.protocol.MyMoneyJsonProtocol._

import com.mongodb.casbah.Imports._
import com.mongodb.casbah.commons.conversions.scala._

import org.scala_tools.time.Imports._
import de.ste.mymoney.util.MyLocalDate
import de.ste.mymoney.util.MyLocalDate._
import scala.collection.mutable.ListBuffer



trait BalanceService extends Directives with SprayJsonSupport { this: MongoComponent =>

	RegisterJodaTimeConversionHelpers()
	
	val balanceService = {
		pathPrefix("rest") {
			pathPrefix("balance") {
				get {
					path(Remaining) { id =>
						_.complete(load(id))
					}
				} ~
				(put & path("")) {
					content(as[Balance]) { balance =>
						create(balance)
						_.complete(HttpResponse(OK))
					}
				} ~
//				(post) {
//					path(Remaining) { id =>
//						content(as[Balance]) { balance =>
//							update(id, balance)
//							_.complete(HttpResponse(OK))
//						}
//					}
//				} ~
				(delete) {
					path(Remaining) { id =>
						delete(id)
						_.complete(HttpResponse(OK))
					}
				}
			} ~
			path("balances") {
				get {
					_.complete(find());
				}
			}
		}
	}
	
	
	/*
	 * CRUD - methods
	 */ 
	
	private def create(balance : Balance) = {
		// delete old instances here
		// schreibe die regelmaessigen Zahlungen bis datum+5Jahre fort
	
		val writeResult = balancesCollection += balance
		if (writeResult.getError != null) throw new Exception("error writing balance")
	}
	
	private def load(id : String) : Balance = {
		val query : DBObject = MongoDBObject("_id" -> new ObjectId(id))

		balancesCollection.findOne(query) match {
			case Some(balanceDbo : BasicDBObject) => balanceDbo
			case None => throw new Exception("balance with id " + id + " does not exist.")
		}
	}
	
	private def find() = {
		val cursor = balancesCollection.find()
		for { balanceDbo <- cursor.toSeq } yield (balanceDbo : Balance)
	}
	
	private def delete(id : String) = {
		val query : DBObject = MongoDBObject("_id" -> new ObjectId(id))
		val writeResult  = balancesCollection.remove(query)
		if (writeResult.getN == 0) throw new Exception("requested entity could not be deleted")
	}
	
	private def update(id : String, balance : Balance) = {
		//update entity
		val queryUpdate : DBObject = MongoDBObject("_id" -> new ObjectId(id))
		val writeResultUpdate = balancesCollection.update(queryUpdate,balance)
	}
}