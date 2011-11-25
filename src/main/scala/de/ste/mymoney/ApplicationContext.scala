package de.ste.mymoney

import akka.config.Supervision._
import akka.actor.Supervisor
import akka.actor.Actor._
import cc.spray._
import de.ste.mymoney.services._
import com.mongodb.casbah.Imports._


trait MongoComponent {
 
  val mongoConnection = MongoConnection("localhost")
  
  val expensesCollection : MongoCollection = mongoConnection("myMoney")("transactions")  
  val balancesCollection : MongoCollection = mongoConnection("myMoney")("balances")
  
}

object AppContext extends   
	MongoComponent with   
	ExpenseService with
	BalanceService with
	AnalyzeService
{  
}   
