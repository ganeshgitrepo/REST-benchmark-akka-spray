package com.xebia.rest

import akka.actor.Actor
import akka.actor.Actor._
import cc.spray.json._
import RecordJsonProtocol._
import akka.dispatch.Future
import io.Source
import java.io.{FileNotFoundException, FileOutputStream, File}

object FileSystemStore extends RecordStore {
  val fsRoot = new File("/tmp/spray")

  def get(key: Long) = {
    Future[Option[Record]] {
      val recordLocation = new File(fsRoot, key + "")
      try {
        val rawRecord = Source.fromFile(recordLocation, "UTF-8").getLines().mkString
        Some(JsonParser(rawRecord).fromJson[Record])
      } catch {
        case e:FileNotFoundException => None
      }
    }
  }

  def put(key: Long, value: Record) = {
    spawn {
      val pendingRecordLocation = new File(fsRoot, key + ".tmp")
      val recordOutputStream = new FileOutputStream(pendingRecordLocation)
      try {
        recordOutputStream.write(value.toJson.toString.getBytes("UTF-8"))
      } finally {
        recordOutputStream.close()
      }
      val finalRecordLocation = new File(fsRoot, key + "")
      pendingRecordLocation.renameTo(finalRecordLocation)
    }
  }
}
