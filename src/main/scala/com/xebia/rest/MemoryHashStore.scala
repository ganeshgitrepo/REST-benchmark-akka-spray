package com.xebia.rest

import akka.actor.Actor
import akka.actor.Actor._
import com.xebia.rest.RecordStoreMessages._

object MemoryHashStore extends RecordStore {

  val storeActor = actorOf[MemoryHashStore]

  def get(key: Long) = {
    storeActor !!! Get(key)
  }

  def put(key: Long, value: Record) {
    storeActor !!! Put(key, value)
  }
}

class MemoryHashStore extends Actor {
  val records = new collection.mutable.HashMap[Long,Record]()

  protected def receive = {
    case Get(id) => self.channel ! records.get(id)
    case Put(id, record) => records += ((id, record))
  }
}
