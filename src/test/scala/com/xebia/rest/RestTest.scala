package com.xebia.rest

import cc.spray.test.SprayTest
import cc.spray.http.HttpMethods._
import org.specs2.mutable.Specification
import org.specs2.specification._
import cc.spray.http._
import cc.spray.json._

class RestTest extends Specification with SprayTest with RestService {

  val recordStore = MemoryHashStore

  implicit val context = new Scope with After {
    val storeActor = recordStore.storeActor
    storeActor.start()

    def after = {
      storeActor.stop()
    }
  }

  "The ReST Service when GET" should {
    "return a 404 if the record is not found" in {
      val response = testService(HttpRequest(GET, "/rest/get/666")) {
        restService
      }.response.status mustEqual StatusCodes.NotFound
    }

    "return a 200 status code if the record is found" in {
      val record = JsObject(
        "id" -> JsNumber(201),
        "shortStringAttribute" -> JsString("bla4"),
        "longStringAttribute" -> JsString("bla blah blah4"),
        "intNumber" -> JsNumber(1004),
        "trueOrFalse" -> JsBoolean(false)
      )
      testService(HttpRequest(POST, "/rest/put/201",
        content = Some(HttpContent(ContentType(MediaTypes.`application/json`), record.compactPrint)))) {
        restService
      }
      val response = testService(HttpRequest(GET, "/rest/get/201")) {
        restService
      }.response

      response.status mustEqual StatusCodes.OK
      response.content.map(_.contentType.mediaType) mustEqual Some(MediaTypes.`application/json`)
      val responseRecord = response.content.map(content => JsonParser(content.buffer.map(_.toChar)))
      responseRecord mustEqual Some(record)
    }
  }

  "The ReST Service when POST" should {
    "return a 409 if the id's don't match" in {
      val record = JsObject(
        "id" -> JsNumber(4),
        "shortStringAttribute" -> JsString("bla4"),
        "longStringAttribute" -> JsString("bla bla bla 4"),
        "intNumber" -> JsNumber(1004),
        "trueOrFalse" -> JsBoolean(false)
      )
      val response = testService(HttpRequest(POST, "/rest/put/666",
        content = Some(HttpContent(ContentType(MediaTypes.`application/json`),
          record.compactPrint)))) {
        restService
      }.response
      response.status mustEqual StatusCodes.Conflict
    }

    "return a 200 if the post succeeds" in {
      val record = JsObject(
        "id" -> JsNumber(200),
        "shortStringAttribute" -> JsString("bla4"),
        "longStringAttribute" -> JsString("bla bla bla 4"),
        "intNumber" -> JsNumber(1004),
        "trueOrFalse" -> JsBoolean(false)
      )
      val response = testService(HttpRequest(POST, "/rest/put/200",
        content = Some(HttpContent(ContentType(MediaTypes.`application/json`),
          record.compactPrint)))) {
        restService
      }.response
      response.status mustEqual StatusCodes.OK
    }
  }
}
