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

  val expenseHttpService = actorOf(new HttpService(expenseModule.expenseService))
  val balanceHttpService = actorOf(new HttpService(balanceModule.balanceService))
  val rootService = actorOf(new RootService(expenseHttpService, balanceHttpService))

  Supervisor(
    SupervisorConfig(
      OneForOneStrategy(List(classOf[Exception]), 3, 100),
      List(
        Supervise(expenseHttpService, Permanent),
        Supervise(balanceHttpService, Permanent),
        Supervise(rootService, Permanent)
      )
    )
  )
}