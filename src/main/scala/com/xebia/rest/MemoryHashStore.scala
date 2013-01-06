package com.xebia.rest

import akka.actor.{Props, ActorSystem, OldActor}
import com.xebia.rest.RecordStoreMessages._
import akka.migration._

class MemoryHashStore(implicit system: ActorSystem) extends RecordStore {

  val storeActor = system.actorOf(Props(new MemoryHashStoreActor))

  override def get(key: Long) = (storeActor ? Get(key)).mapTo[Option[Record]]

  override def put(key: Long, value: Record) = storeActor ? Put(key, value)

  class MemoryHashStoreActor extends OldActor {
    val records = new collection.mutable.HashMap[Long,Record]()

    protected def receive = {
      case Get(id) => self.channel ! records.get(id)
      case Put(id, record) =>  records += ((id, record))
    }
  }
}
