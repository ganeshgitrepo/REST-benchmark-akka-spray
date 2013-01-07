package com.xebia.rest

import akka.dispatch.Future
import akka.util.Timeout

trait RecordStore {
  def get(key: Long)(implicit timeout: Timeout): Future[Option[Record]]
  def put(key: Long, value: Record)(implicit timeout: Timeout): Future[Unit]
}

// Convenience set of messages for actor implementations
object RecordStoreMessages {
  sealed trait RecordStoreMessage
  case class Get(id: Long) extends RecordStoreMessage
  case class Put(id: Long, record: Record) extends RecordStoreMessage
}
