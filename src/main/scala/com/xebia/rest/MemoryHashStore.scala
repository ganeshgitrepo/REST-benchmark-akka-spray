package com.xebia.rest

import akka.actor.{Props, ActorSystem, Actor}
import akka.pattern.ask
import akka.util.Timeout

import RecordStoreMessages._

class MemoryHashStore(implicit system: ActorSystem) extends RecordStore {

  val storeActor = system.actorOf(Props(new MemoryHashStoreActor))

  override def get(key: Long)(implicit timeout: Timeout) = (storeActor ? Get(key)).mapTo[Option[Record]]

  override def put(key: Long, value: Record)(implicit timeout: Timeout) = (storeActor ? Put(key, value)).mapTo[Unit]

  class MemoryHashStoreActor extends Actor {
    val records = new collection.mutable.HashMap[Long,Record]()

    protected def receive = {
      case Get(id) => sender ! records.get(id)
      case Put(id, record) => {
        records += ((id, record))
        sender ! ()
      }
    }
  }
}
