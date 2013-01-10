package com.xebia.rest

import org.specs2.mutable.Specification
import akka.util.Timeout
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import java.util.concurrent.TimeUnit

class MongoDBStoreTest extends Specification {

  "The MongoDBStore when put" should {
    "store and retrieve the record correctly" in new AkkaTestkitContext {
      implicit val storeTimeout = Timeout(2, TimeUnit.SECONDS)
      val r = Record(3l, "abc", "abc abc", 45, true)
      val mongoDbStore = new MongoDBStore
      val fp = mongoDbStore.put(r.id, r)
      Await.ready(fp, Duration(3, TimeUnit.SECONDS))
      val fg = mongoDbStore.get(r.id)
      val result = Await.result(fg, Duration(3, TimeUnit.SECONDS)).asInstanceOf[Option[Record]]
      result mustNotEqual None
      val record = result.get
      record.id mustEqual 3l
      record.shortStringAttribute mustEqual "abc"
    }
  }

  "The MongoDBStore when get" should {
    "return None if the record isn't in the database" in new AkkaTestkitContext {
      implicit val storeTimeout = Timeout(2, TimeUnit.SECONDS)
      val mongoDbStore = new MongoDBStore
      val fg = mongoDbStore.get(213)
      val result = Await.result(fg, Duration(3, TimeUnit.SECONDS)).asInstanceOf[Option[Record]]
      result must beNone
    }
  }
}
