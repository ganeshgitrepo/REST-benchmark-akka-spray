package com.xebia.rest

import akka.actor.Actor
import akka.util.{Duration, Timeout}
import spray.http._
import spray.routing._

class RestServiceActor(store: RecordStore) extends Actor with RestService {
  val recordStore = store
  def actorRefFactory = context
  def receive = runRoute(route)
  implicit val storeTimeout: Timeout = Duration(actorSystem.settings.config.getString("rest.store.store-timeout"))
}

trait RestService extends HttpService {

  val recordStore: RecordStore
  implicit val storeTimeout: Timeout

  val route = {
    import spray.httpx.SprayJsonSupport._
    import RecordJsonProtocol._

    // Debugging: /ping -> pong
    path("ping") {
      get {
        complete("pong")
      }
    } ~
    // Service implementation.
    pathPrefix("rest") {
      path("get" / LongNumber) { id =>
        get { ctx =>
          recordStore.get(id) onComplete {
            case Right(result) => result match {
              case Some(record) => ctx.complete(record)
              case None => ctx.complete(StatusCodes.NotFound,"Record with id=" + id + " is not in database.")
            }
          }
        }
      } ~
      path("put" / LongNumber) { id =>
        post {
          entity(as[Record]) { record =>
            if (record.id == id) {
              recordStore.put(id, record)
              complete("")
            } else {
              complete(StatusCodes.Conflict, "The resource ID and ID of the POSTed record do not match.")
            }
          }
        }
      }
    }
  }
}
