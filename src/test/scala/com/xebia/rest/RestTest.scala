package com.xebia.rest

import org.specs2.mutable.Specification
import org.specs2.specification._
import spray.testkit.Specs2RouteTest
import spray.http._
import spray.json._
import akka.migration._
import com.xebia.SprayJsonSupport._
import RecordJsonProtocol._
import akka.AkkaException

class RestTest extends Specification with Specs2RouteTest with RestService {
  def actorRefFactory = system

  val recordStore = new MemoryHashStore

  "The ReST Service when PINGed" should {
    "return a PONG response" in {
      Get("/ping") ~> route ~> check {
        status === StatusCodes.OK
        mediaType mustEqual MediaTypes.`text/plain`
        entityAs[String] mustEqual "pong"
      }
    }
  }

  "The ReST Service when GET" should {
    "return a 404 if the record is not found" in {
      Get("/rest/get/666") ~> route ~> check {
        status === StatusCodes.NotFound
      }
    }

    "return a 200 status code if the record is found" in {
      val id = 201
      val record = JsObject(
        "id" -> JsNumber(id),
        "shortStringAttribute" -> JsString("bla4"),
        "longStringAttribute" -> JsString("bla blah blah4"),
        "intNumber" -> JsNumber(1004),
        "trueOrFalse" -> JsBoolean(false)
      )
      Post("/rest/put/" + id, record) ~> route ~> check {
        handled must beTrue
      }
      Get("/rest/get/" + id) ~> route ~> check {
        status === StatusCodes.OK
        mediaType mustEqual MediaTypes.`application/json`
        entityAs[JsObject] mustEqual record
      }
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
      Post("/rest/put/666", record) ~> route ~> check {
        status === StatusCodes.Conflict
      }
    }

    "return a 200 if the post succeeds" in {
      val record = JsObject(
        "id" -> JsNumber(200),
        "shortStringAttribute" -> JsString("bla4"),
        "longStringAttribute" -> JsString("bla bla bla 4"),
        "intNumber" -> JsNumber(1004),
        "trueOrFalse" -> JsBoolean(false)
      )
      Post("/rest/put/200", record) ~> route ~> check {
        status === StatusCodes.OK
      }
    }
  }
}
