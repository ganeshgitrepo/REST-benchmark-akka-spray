package com.xebia.rest

import akka.actor.{Props, ActorSystem, PoisonPill, Actor}
import akka.pattern.ask
import akka.util.Timeout
import spray.json._
import io.Source
import util.control.Exception._

import java.io.{FileNotFoundException, FileOutputStream, File, OutputStream}
import java.util.UUID

import RecordJsonProtocol._
import RecordStoreMessages._

class FileSystemStore(implicit system: ActorSystem) extends RecordStore {
  override def get(key: Long)(implicit timeout: Timeout) = {
    // Spawn an actor just for this task.
    val newActor = system.actorOf(Props(new ShardingFileSystemStoreActor))
    val futureResult = (newActor ? Get(key)).mapTo[Option[Record]]
    newActor ! PoisonPill
    futureResult
  }

  override def put(key: Long, value: Record)(implicit timeout: Timeout) = {
    // Spawn an actor just for this task.
    val newActor = system.actorOf(Props(new ShardingFileSystemStoreActor))
    val futureResult = (newActor ? Put(key, value)).mapTo[Unit]
    newActor ! PoisonPill
    futureResult
  }

  class FileSystemStoreActor extends Actor {
    val encoding = "UTF-8"
    lazy val fsRoot = new File(system.settings.config.getString("rest.filestore.root"))
    val uuid = UUID.randomUUID

    def locationInStore(id: Long): String = id.toString
    def location(id: Long): File = new File(fsRoot, locationInStore(id))

    def receive = {
      case Get(id) => {
        val recordLocation = location(id)
        try {
          val rawRecord = Source.fromFile(recordLocation, encoding).getLines().mkString
          sender ! Some(JsonParser(rawRecord).convertTo[Record])
        } catch {
          case e:FileNotFoundException => sender ! None
        }
      }
      case Put(id, value) => {
        val recordLocation = location(id)
        val pendingRecordLocation = new File(recordLocation.getParentFile,
          recordLocation.getName + '.' + uuid)
        val recordOutputStream = openOutputStream(pendingRecordLocation)
        ultimately(recordOutputStream.close) {
          recordOutputStream.write(value.toJson.toString.getBytes(encoding))
        }
        pendingRecordLocation.renameTo(recordLocation)
        sender ! ()
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

  class ShardingFileSystemStoreActor extends FileSystemStoreActor {
    override def locationInStore(id: Long): String = {
      // Hash the id, and use the least significant 12-bits.
      val hash = id.hashCode
      val bucketComponents = (hash & 0xff) :: (hash >> 8 & 0x0f) :: Nil map (x => x.toHexString)
      val pathComponents = bucketComponents ::: id.toString :: Nil
      pathComponents.mkString(File.separator)
    }
  }
}
