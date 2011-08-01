package com.xebia.rest

import akka.actor.Actor._
import cc.spray.json._
import RecordJsonProtocol._
import io.Source
import util.control.Exception._
import java.io.{FileNotFoundException, FileOutputStream, File, OutputStream}
import com.xebia.rest.RecordStoreMessages._
import akka.actor.{PoisonPill, Actor}
import akka.config.Config._

object FileSystemStore extends RecordStore {
  def get(key: Long) = {
    // Spawn an actor just for this task.
    val newActor = actorOf[ShardingFileSystemStore].start()
    val futureResult = newActor !!! Get(key)
    newActor ! PoisonPill
    futureResult
  }

  def put(key: Long, value: Record) = {
    // Spawn an actor just for this task.
    val newActor = actorOf[ShardingFileSystemStore].start()
    newActor ! Put(key, value)
    newActor ! PoisonPill
  }
}

class FileSystemStore extends Actor {
  val encoding = "UTF-8"
  lazy val fsRoot = new File(config.getString("store.root").orNull)

  def locationInStore(id: Long): String = id.toString
  def location(id: Long): File = new File(fsRoot, locationInStore(id))

  def receive = {
    case Get(id) => {
      val recordLocation = location(id)
      try {
        val rawRecord = Source.fromFile(recordLocation, encoding).getLines().mkString
        self.channel ! Some(JsonParser(rawRecord).fromJson[Record])
      } catch {
        case e:FileNotFoundException => self.channel ! None
      }
    }
    case Put(id, value) => {
      val recordLocation = location(id)
      val pendingRecordLocation = new File(recordLocation.getParentFile,
                                           recordLocation.getName + '.' + self.uuid)
      val recordOutputStream = openOutputStream(pendingRecordLocation)
      ultimately(recordOutputStream.close) {
        recordOutputStream.write(value.toJson.toString.getBytes(encoding))
      }
      pendingRecordLocation.renameTo(recordLocation)
    }
  }

  def openOutputStream(name: File): OutputStream = {
    // Return an opened stream for the given file, trying (once) to create all
    // parent directories if we get a FileNotFoundException.
    try {
      new FileOutputStream(name)
    } catch {
      case e:FileNotFoundException => {
        name.getParentFile.mkdirs
        new FileOutputStream(name)
      }
    }
  }
}

class ShardingFileSystemStore extends FileSystemStore {
  override def locationInStore(id: Long): String = {
    // Hash the id, and use the least significant 12-bits.
    val hash = id.hashCode
    val bucketComponents = (hash & 0xff) :: (hash >> 8 & 0x0f) :: Nil map (x => x.toHexString)
    val pathComponents = bucketComponents ::: id.toString :: Nil
    pathComponents.mkString(File.separator)
  }
}
