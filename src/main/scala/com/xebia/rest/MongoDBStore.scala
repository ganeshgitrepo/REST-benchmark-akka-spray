package com.xebia.rest

import akka.actor.ActorSystem
import akka.dispatch.Future
import akka.util.Timeout
import com.mongodb.casbah.MongoConnection
import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.DBObject

class MongoDBStore(implicit system: ActorSystem) extends RecordStore {

  lazy val collection = {
    val config = system.settings.config.getConfig("rest.mongodb")
    val mongoConn = MongoConnection(config.getString("host"), config.getInt("port"))
    mongoConn(config.getString("database"))(config.getString("collection"))
  }

  override def get(key: Long)(implicit timeout: Timeout) = {
    Future {
      val q = MongoDBObject("_id" -> key)
      val record = collection.findOne(q)
      unmarshal(record.get)
    }
  }

  override def put(key: Long, value: Record)(implicit timeout: Timeout) = {
    Future {
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
