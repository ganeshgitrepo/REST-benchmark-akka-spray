package com.xebia.rest

import cc.spray.test.{DontDetach, SprayTest}
import cc.spray.http.HttpMethods._
import org.specs.Specification
import cc.spray.http._
import cc.spray.marshalling.SprayJsonMarshalling

class RestTest extends Specification with SprayTest with RestService with DontDetach {

	"The ReST Service when GET" should {
		"return a 404 if the record is not found" in {
			val response = testService(HttpRequest(GET, "/rest/get/666")) {
				restService
			}.response
			response.status mustEqual StatusCodes.NotFound
		}

		"return a 200 status code if the record is found" in {
			val niceRecord: String = "{\n  \"id\": 201,\n  \"shortStringAttribute\": \"bla4\",\n  \"longStringAttribute\": \"bla bla bla4\",\n  \"intNumber\": 1004,\n  \"trueOrFalse\": false\n}"
			val record: String = "{\"id\": 201, \"shortStringAttribute\": \"bla4\", \"longStringAttribute\": \"bla bla bla4\", \"intNumber\": 1004, \"trueOrFalse\": false}"
			testService(HttpRequest(POST, "/rest/put/201",
									content = Some(HttpContent(ContentType(MediaTypes.`application/json`), record)))) {
				restService
			}
			val response = testService(HttpRequest(GET, "/rest/get/201")) {
				restService
			}.response

			response.status mustEqual StatusCodes.OK
			response.content.as[String] mustEqual Right(niceRecord)
		}
	}

	"The ReST Service when POST" should {
		"return a 409 if the id's don't match" in {
			val response = testService(HttpRequest(POST, "/rest/put/666",
									content = Some(HttpContent(ContentType(MediaTypes.`application/json`), "{\"id\": 4, \"shortStringAttribute\": \"bla4\", \"longStringAttribute\": \"bla bla bla4\", \"intNumber\": 1004, \"trueOrFalse\": false}")))) {
				restService
			}.response
			response.status mustEqual StatusCodes.Conflict
		}

		"return a 200 if the post succeeds" in {
			val response = testService(HttpRequest(POST, "/rest/put/200",
									content = Some(HttpContent(ContentType(MediaTypes.`application/json`), "{\"id\": 200, \"shortStringAttribute\": \"bla4\", \"longStringAttribute\": \"bla bla bla4\", \"intNumber\": 1004, \"trueOrFalse\": false}")))) {
				restService
			}.response
			response.status mustEqual StatusCodes.OK
		}
	}

}