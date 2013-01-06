package com.xebia.rest

import org.specs2.mutable.Specification
import akka.dispatch.Await
import akka.util.{Timeout, Duration}
import java.util.concurrent.TimeUnit

class MongoDBStoreTest extends Specification {

  "The MongoDBStore when put" should {
    "store and retrieve the record correctly" in new AkkaTestkitContext {
      implicit val storeTimeout = Timeout(2, TimeUnit.SECONDS)
      val r = Record(3l, "abc", "abc abc", 45, true)
      val mongoDbStore = new MongoDBStore
      mongoDbStore.put(r.id, r)
      val f = mongoDbStore.get(r.id)
      val result = Await.result(f, Duration(3, TimeUnit.SECONDS)).asInstanceOf[Option[Record]]
      result mustNotEqual None
      val record = result.get
      record.id mustEqual 3l
      record.shortStringAttribute mustEqual "abc"
    }
  }

}