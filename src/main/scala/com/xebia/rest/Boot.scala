package com.xebia.rest

import akka.actor.{ActorSystem, Props}
import spray.can.server.SprayCanHttpServerApp
import com.typesafe.config.Config
import spray.util.ConfigUtils

object Boot extends App with SprayCanHttpServerApp {

  private val config = system.settings.config
  val configuredRecordStore = config.getString("rest.store.backend") match {
    case "filestore" => new FileSystemStore(system)
    case "memory" => new MemoryHashStore(system)
    case "mongodb" => new MongoDBStore(system)
  }
  val service = system.actorOf(Props(new RestServiceActor(configuredRecordStore)))
  private val c: Config = ConfigUtils.prepareSubConfig(config, "spray.can.server")
  private val interface = c getString "interface"
  private val port      = c getInt    "port"
  newHttpServer(service) ! Bind(interface, port)
}
