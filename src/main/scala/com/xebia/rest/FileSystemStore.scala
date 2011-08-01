package com.xebia.rest

import akka.actor.Actor._
import cc.spray.json._
import RecordJsonProtocol._
import io.Source
import java.io.{FileNotFoundException, FileOutputStream, File}
import com.xebia.rest.RecordStoreMessages._
import akka.actor.{PoisonPill, Actor}

object FileSystemStore extends RecordStore {
  def get(key: Long) = {
    // Spawn an actor just for this task.
    val newActor = actorOf[FileSystemStore].start()
    val futureResult = newActor !!! Get(key)
    newActor ! PoisonPill
    futureResult
  }

  def put(key: Long, value: Record) = {
    // Spawn an actor just for this task.
    val newActor = actorOf[FileSystemStore].start()
    newActor ! Put(key, value)
    newActor ! PoisonPill
  }
}

class FileSystemStore extends Actor {
  val encoding = "UTF-8"
  val fsRoot = new File("/tmp/spray")
  def receive = {
    case Get(id) => {
      val recordLocation = new File(fsRoot, id.toString)
      try {
        val rawRecord = Source.fromFile(recordLocation, encoding).getLines().mkString
        self.channel ! Some(JsonParser(rawRecord).fromJson[Record])
      } catch {
        case e:FileNotFoundException => self.channel ! None
      }
    }
    case Put(id, value) => {
      val pendingRecordLocation = new File(fsRoot, id + '.' + self.uuid.toString)
      val recordOutputStream = new FileOutputStream(pendingRecordLocation)
      try {
        recordOutputStream.write(value.toJson.toString.getBytes(encoding))
      } finally {
        recordOutputStream.close()
      }
      val finalRecordLocation = new File(fsRoot, id.toString)
      pendingRecordLocation.renameTo(finalRecordLocation)
    }
  }
}
