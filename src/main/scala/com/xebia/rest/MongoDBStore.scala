package com.xebia.rest

import akka.actor.ActorSystem
import akka.util.Timeout
import scala.concurrent._
import com.mongodb.casbah.MongoConnection
import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.casbah.Imports._
import com.novus.salat._
import com.novus.salat.global._

class MongoDBStore(implicit system: ActorSystem) extends RecordStore {

  lazy val collection = {
    val config = system.settings.config.getConfig("rest.mongodb")
    val mongoConn = MongoConnection(config.getString("host"), config.getInt("port"))
    mongoConn(config.getString("database"))(config.getString("collection"))
  }

  import system.dispatcher

  override def get(key: Long)(implicit timeout: Timeout) = {
    Future {
      blocking {
        val result = collection.findOne(MongoDBObject("_id" -> key))
        result.foreach(remap(_, "_id" -> "id"))
        result.map(toRecord(_))
      }
    }
  }

  override def put(key: Long, value: Record)(implicit timeout: Timeout) = {
    Future {
      blocking {
        val dbObject = toDBObject(value)
        remap(dbObject, "id" -> "_id")
        collection.insert(dbObject)
      }
    }
  }

  private def remap(dbObject: DBObject, fromTo: (String, String)) = dbObject += (fromTo._2 -> dbObject.removeField(fromTo._1))
  private def toDBObject(record: Record): DBObject = grater[Record].asDBObject(record)
  private def toRecord(dbObject: DBObject): Record = grater[Record].asObject(dbObject)
}
