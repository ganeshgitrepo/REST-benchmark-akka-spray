package com.xebia.rest

import cc.spray.json.DefaultJsonProtocol

case class Record(id: Long,
                  shortStringAttribute: String,
                  longStringAttribute: String,
                  intNumber: Int,
                  trueOrFalse: Boolean)

object RecordJsonProtocol extends DefaultJsonProtocol {
  implicit val recordFormat =
    jsonFormat(Record, "id", "shortStringAttribute", "longStringAttribute", "intNumber", "trueOrFalse")
}
