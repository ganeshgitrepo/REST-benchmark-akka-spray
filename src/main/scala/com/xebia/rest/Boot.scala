package com.xebia.rest

import akka.actor.{ActorSystem, Props}
import spray._
import servlet.WebBoot

class Boot extends WebBoot {

  override implicit val system = ActorSystem("default")

  val configuredRecordStore = system.settings.config.getString("rest.store.backend") match {
    case "filestore" => new FileSystemStore
    case "memory" => new MemoryHashStore
    case "mongodb" => new MongoDBStore
  }

  override val serviceActor = system.actorOf(Props(new RestServiceActor(configuredRecordStore)))
}
