package de.ste.mymoney.services

import cc.spray._

trait HelloService extends Directives {
  
  val helloService = {
    path("test") {
      get { _.complete("Say hello to Spray!") }
    }
  }
  
}