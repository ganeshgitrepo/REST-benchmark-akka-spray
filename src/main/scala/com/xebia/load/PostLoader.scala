package com.xebia.load

import java.net.{HttpURLConnection,URL}

import util.control.Exception._
import cc.spray.http._
import cc.spray.json._
import com.xebia.rest.{Record,RecordJsonProtocol}
import RecordJsonProtocol._

object PostLoader extends App {
  val urlRoot = new URL(args.head)
  val jsonFileNames = args.tail
  val jsonEncoding = "UTF-8"
  val progressInterval = 10000
  var totalCount = 0
  var uploadCount = 0
  jsonFileNames.foreach(jsonFileName => {
    val jsonFile = io.Source.fromFile(jsonFileName, jsonEncoding)
    ultimately(jsonFile.close) {
      // Each line is a separate JSON bit.
      jsonFile.getLines.foreach(line => {
         val record = JsonParser(line).fromJson[Record]
         totalCount += 1
         val recordId = record.id
         val url = new URL(urlRoot, "/rest/put/" + recordId)
         val request = url.openConnection
         request match {
           case httpRequest: HttpURLConnection => {
             httpRequest.setRequestMethod(HttpMethods.POST.toString)
             httpRequest.setRequestProperty("Content-Type", "application/json")
           }
         }
         request.setDoOutput(true)
         request.setDoInput(true)
         request.connect
         val postStream = request.getOutputStream
         ultimately(postStream.close) {
           postStream.write(line.getBytes(jsonEncoding))
         }
         var success = true
         request match {
           case httpRequest: HttpURLConnection => {
             val responseCode = httpRequest.getResponseCode
             if (responseCode != HttpURLConnection.HTTP_OK) {
               success = false
               Console.println("Error uploading record " + recordId + ": " + httpRequest.getResponseMessage)
             }
           }
         }
         if (success) {
           uploadCount += 1
         }
         if (totalCount > 0 && 0 == totalCount % progressInterval) {
           Console.println("Processed " + totalCount + " records…");
         }
      })
    }
  })
  Console.println("Uploaded " + uploadCount + " of " + totalCount + " records.")
}