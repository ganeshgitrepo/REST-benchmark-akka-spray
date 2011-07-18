package com.xebia.rest

import cc.spray._
import directives.IntNumber
import http._
import MediaTypes._
import marshalling.SprayJsonMarshalling
import json._
import RecordJsonProtocol._

trait RestService extends Directives with SprayJsonMarshalling {

  val records = new collection.mutable.HashMap[Int,Record]()

  val restService = {
    // Debugging: /ping -> pong
    path("ping") {
      get { _.complete("pong") }
    } ~
    // Service implementation.
    pathPrefix("rest") {
      path("get" / IntNumber) { id =>
        get { ctx =>
          records.get(id) match {
            case Some(record) => ctx.complete(record)
            case None => ctx.fail(StatusCodes.NotFound,"Record with id=" + id + " is not in database.")
          }
        }
      } ~
      path("put" / IntNumber) { id =>
        post {
          content(as[Record]) { record =>
            if (record.id == id) {
              records += ((id, record))
              _.complete("")
            } else {
              _.fail(StatusCodes.Conflict, "The resource ID and ID of the POSTed record do not match.")
            }
          }
        }
      }
    }
  }
}
