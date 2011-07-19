package com.xebia.rest

import akka.config.Supervision._
import akka.actor.Supervisor
import akka.actor.Actor._
import cc.spray._
import utils.ActorHelpers._

class Boot {

  val memoryHashStore = MemoryHashStore

  val mainModule = new RestService {
    val recordStore = memoryHashStore
  }

  // Start all actors that need supervision, including the root service actor.
   Supervisor(
    SupervisorConfig(
      OneForOneStrategy(List(classOf[Exception]), 3, 100),
      List(
        Supervise(actorOf[RootService], Permanent),
        Supervise(memoryHashStore.storeActor, Permanent)
      )
    )
  )

  // attach an HttpService (which is also an actor)
  // the root service automatically starts the HttpService if it is unstarted
  actor[RootService] ! Attach(HttpService(mainModule.restService))
}
