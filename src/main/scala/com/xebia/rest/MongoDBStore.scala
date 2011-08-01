package com.xebia.rest

import com.mongodb.casbah.MongoConnection
import com.mongodb.casbah.commons.MongoDBObject
import akka.dispatch.Future
import com.mongodb.DBObject
import akka.actor.Actor._
import akka.config.Config._

object MongoDBStore extends RecordStore {

  lazy val mongoConn = MongoConnection(config.getString("mongodb.host", "localhost"),
                                       config.getInt("mongodb.port", 27017))
  lazy val collection = mongoConn(config.getString("mongodb.database").orNull)(config.getString("mongodb.collection").orNull)

  def get(key: Long) = {
    Future {
      val q = MongoDBObject("_id" -> key)
      val record = collection.findOne(q)
      unmarshal(record.get)
    }
  }

  def put(key: Long, value: Record) = {
    spawn {
      val dbObj = marshall(key, value)
      collection.insert(dbObj)
    }
  }

  def unmarshal(obj: DBObject) = {
    val id = obj.get("_id").asInstanceOf[Long]
    val intNumber = obj.get("intNumber").asInstanceOf[Int]
    val trueOrFalse = obj.get("trueOrFalse").asInstanceOf[Boolean]
    val longStringAttribute = obj.get("longStringAttribute").asInstanceOf[String]
    val shortStringAttribute = obj.get("shortStringAttribute").asInstanceOf[String]
    Some(Record(id, shortStringAttribute, longStringAttribute, intNumber, trueOrFalse))
  }

  def marshall(key: Long, rec: Record) = {
    MongoDBObject("_id" -> key, "intNumber" -> rec.intNumber, "longStringAttribute" -> rec.longStringAttribute, "shortStringAttribute" -> rec.shortStringAttribute, "trueOrFalse" -> rec.trueOrFalse)
  }


}