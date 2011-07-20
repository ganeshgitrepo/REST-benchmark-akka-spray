package com.xebia.load

import com.mongodb.util.JSON
import com.mongodb.casbah.{MongoCollection, MongoConnection}
import com.mongodb.BasicDBObject
import org.omg.PortableInterceptor.NON_EXISTENT
import java.io.{InputStreamReader, BufferedReader, FileInputStream}

object MongoDBLoader {


  def doWithSnippet(coll:MongoCollection)(dbObj:BasicDBObject) = {
    val intN = dbObj.get("intNumber").asInstanceOf[Long]
    dbObj.append("intNumber", intN.toInt)
    val id = dbObj.get("id").asInstanceOf[Long]
    dbObj.remove("id")
    dbObj.append("_id", id)
    coll.insert(dbObj)
  }

  def processJsonSnippet(reader: BufferedReader, json: String): Option[String] = {
    val l = reader.readLine()
    if (l == null) {
      return if (json == null)  None else Some(json)
    } else {
      val newJson = json + l
      if (l.trim.endsWith("}")) {
        return Some(newJson)
      } else {
        processJsonSnippet(reader, newJson)
      }
    }
  }

  def loadJsonData(collection:MongoCollection) = {
    val jsonSnippethandler = doWithSnippet(collection)(_)
    var counter = 0L
    val in = new BufferedReader(new InputStreamReader(new FileInputStream("C:\\development\\github\\perfbenchmark\\data.json")))
    var l = in.readLine()
    while (l != null) {
      val json = processJsonSnippet(in, l)
      if (json != None) {
      val dbObj = JSON.parse(json.get).asInstanceOf[BasicDBObject]
        counter += 1
        jsonSnippethandler(dbObj)
        if (counter % 10000 == 0) {
          println("loaded " + counter)
        }
      }
      l = in.readLine()
    }
    in.close();
  }

  def main(args: Array[String]) {

    val mongoConn = MongoConnection("localhost", 27017)
    val collection = mongoConn("perfcontest")("perftest")
    loadJsonData(collection)


    /*
  val input = Stream.continually(in readLine)
      for (l <- input) {
      println(l)
      }


    */
  }


}

/*

public class Main {

    /**
     * This method reads contents of a file and print it out
     */
    public void readFromFile(String filename) {

        BufferedInputStream bufferedInput = null;
        byte[] buffer = new byte[1024];

        try {

            //Construct the BufferedInputStream object
            bufferedInput = new BufferedInputStream(new FileInputStream(filename));

            int bytesRead = 0;

            //Keep reading from the file while there is any content
            //when the end of the stream has been reached, -1 is returned
            while ((bytesRead = bufferedInput.read(buffer)) != -1) {

                //Process the chunk of bytes read
                //in this case we just construct a String and print it out
                String chunk = new String(buffer, 0, bytesRead);
                System.out.print(chunk);
            }

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            //Close the BufferedInputStream
            try {
                if (bufferedInput != null)
                    bufferedInput.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new Main().readFromFile("myFile.txt");
    }
}
*/