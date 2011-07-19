package com.xebia.rest

import akka.dispatch.Future

trait RecordStore {
  def get(key: Long): Future[Option[Record]]
  def put(key: Long, value: Record)
}

// Convenience set of messages for actor implementations
object RecordStoreMessages {
  sealed trait RecordStoreMessage
  case class Get(id: Long) extends RecordStoreMessage
  case class Put(id: Long, record: Record) extends RecordStoreMessage
}
