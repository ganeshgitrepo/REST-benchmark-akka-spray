package com.xebia.rest

import cc.spray._

trait RestService extends Directives {

  val recordStore: RecordStore

  val restService = {
    import directives.LongNumber
    import http._
    import typeconversion.SprayJsonSupport._
    import RecordJsonProtocol._

    // Debugging: /ping -> pong
    path("ping") {
      get { _.complete("pong") }
    } ~
    // Service implementation.
    pathPrefix("rest") {
      path("get" / LongNumber) { id =>
        get { ctx =>
          recordStore.get(id).onComplete(f => {
	        f.result.get match {
            case Some(record) => ctx.complete(record)
            case None => ctx.fail(StatusCodes.NotFound,"Record with id=" + id + " is not in database.")
          }})
        }
      } ~
      path("put" / LongNumber) { id =>
        post {
          content(as[Record]) { record =>
            if (record.id == id) {
              recordStore.put(id, record)
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
