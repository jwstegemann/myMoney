package de.ste.mymoney

import akka.config.Supervision._
import akka.actor.Supervisor
import akka.actor.Actor._
import cc.spray._
import de.ste.mymoney.services._
import com.mongodb.casbah.Imports._


class Boot {
 
  val expenseHttpService = actorOf(new HttpService(AppContext.expenseService))
  val balanceHttpService = actorOf(new HttpService(AppContext.balanceService))
  val analyzeHttpService = actorOf(new HttpService(AppContext.analyzeService))
  
  val rootService = actorOf(new RootService(expenseHttpService, balanceHttpService, analyzeHttpService))

  Supervisor(
    SupervisorConfig(
      OneForOneStrategy(List(classOf[Exception]), 3, 100),
      List(
        Supervise(expenseHttpService, Permanent),
        Supervise(balanceHttpService, Permanent),
        Supervise(analyzeHttpService, Permanent),
        Supervise(rootService, Permanent)
      )
    )
  )
}