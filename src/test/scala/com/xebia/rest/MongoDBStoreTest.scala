package com.xebia.rest

import org.specs.Specification

class MongoDBStoreTest extends Specification {

  "The MongoDBStore when put" should {
    "store and retrieve the record correctly" in {
      val r = Record(3l, "abc", "abc abc", 45, true)
      MongoDBStore.put(r.id, r)
      val f = MongoDBStore.get(r.id)
      f.get mustNotBe (None)
      f.get.get.id mustEqual 3l
      f.get.get.shortStringAttribute mustEqual "abc"
    }
  }

}