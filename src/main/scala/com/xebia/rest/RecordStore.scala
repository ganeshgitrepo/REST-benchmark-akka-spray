package com.xebia.rest

import akka.dispatch.Future

trait RecordStore {
  def get(key: Long): Future[Option[Record]]
  def put(key: Long, value: Record)
}
