package de.ste.mymoney

import akka.config.Supervision._
import akka.actor.Supervisor
import akka.actor.Actor._
import cc.spray._
import de.ste.mymoney.services._


class Boot {
 
  val mainModule = new HelloService {
	println("Bin da!")
    // bake your module cake here
  }

  val httpService = actorOf(new HttpService(mainModule.helloService))
  val rootService = actorOf(new RootService(httpService))

  Supervisor(
    SupervisorConfig(
      OneForOneStrategy(List(classOf[Exception]), 3, 100),
      List(
        Supervise(httpService, Permanent),
        Supervise(rootService, Permanent)
      )
    )
  )
}