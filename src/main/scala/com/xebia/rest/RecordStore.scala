package com.xebia.rest

trait RecordStore {
  def get(key: Long): Option[Record]
  def put(key: Long, value: Record)
}

object HashMapRecordStore extends RecordStore {
  val records = new collection.mutable.HashMap[Long,Record]()

  def get(key: Long) = {
    records.get(key)
  }

  def put(key: Long, value: Record) = {
    records.put(key, value)
  }
}
