package com.xebia.rest

import akka.actor.Actor
import akka.util.Timeout
import spray.http._
import spray.routing._

import scala.concurrent.duration.Duration

class RestServiceActor(store: RecordStore) extends Actor with RestService {
  val recordStore = store
  def actorRefFactory = context
  def receive = runRoute(route)
  implicit val storeTimeout: Timeout = Timeout(actorSystem.settings.config.getMilliseconds("rest.store.store-timeout"))
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
          recordStore.get(id) onSuccess {
            case Some(record) => ctx.complete(record)
            case None => ctx.complete(StatusCodes.NotFound,"Record with id=" + id + " is not in database.")
          }
        }
      } ~
      path("put" / LongNumber) { id =>
        post {
          entity(as[Record]) { record => ctx =>
            if (record.id == id) {
              recordStore.put(id, record) onSuccess {
                case _ => ctx.complete("")
              }
            } else {
              ctx.complete(StatusCodes.Conflict, "The resource ID and ID of the POSTed record do not match.")
            }
          }
        }
      }
    }
  }
}
