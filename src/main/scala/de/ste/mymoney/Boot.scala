package de.ste.mymoney

import akka.config.Supervision._
import akka.actor.Supervisor
import akka.actor.Actor._
import cc.spray._
import de.ste.mymoney.services._


class Boot {
 
  val expenseModule = new ExpenseService {
    // bake your module cake here
  }

  val balanceModule = new BalanceService {
    // bake your module cake here
  }

  val analyzeModule = new AnalyzeService {
    // bake your module cake here
  }

  val expenseHttpService = actorOf(new HttpService(expenseModule.expenseService))
  val balanceHttpService = actorOf(new HttpService(balanceModule.balanceService))
  val analyzeHttpService = actorOf(new HttpService(analyzeModule.analyzeService))
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