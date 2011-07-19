package com.xebia.rest

import akka.actor.Actor
import akka.actor.Actor._
import com.xebia.rest.MemoryHashStore._

object MemoryHashStore extends RecordStore {
  sealed trait MemoryHashStoreMessage
  case class Get(id: Long) extends MemoryHashStoreMessage
  case class Put(id: Long, record: Record) extends MemoryHashStoreMessage

  val storeActor = actorOf[MemoryHashStore]

  def get(key: Long) = {
    storeActor !!! Get(key)
  }

  def put(key: Long, value: Record) = {
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
