import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer

/**
  * Created by mmachiraju on 23/03/2016.
  */
object IssueManagementBasic extends App {
  // used to run the actors
  implicit val system = ActorSystem("IssueManagement")
  // materialises underlying flow definition into a set of actors
  implicit val materializer = ActorMaterializer()


  //    val config = ConfigFactory.load()
  //    val logger = Logging(system, getClass)

  val routes =
    path("issue") {
      post {
        complete {
          "Anonymous Issue Created"
        }
      }
    } ~
      path("issues") {
        get {
          complete {
            "All the issues"
          }
        }
      } ~ path("issue" / IntNumber) {
      issueNumber => {
        entity(as[String]) {
          extractedBody => {
            get {
              complete {
                s"Existing Issue with Id $issueNumber and with details $extractedBody retrieved"
              }
            } ~ put {
              complete {
                s"Existing Issue with Id $issueNumber and with details $extractedBody updated"
              }
            }
          }
        }


      }
    }

//  val headerName = path("issue") {
//    (headerValueByName("Whatever")) =>
//    {
//      headerValue => {
//        complete(s"header is $headerValue")
//      }
//    }
//  }
  // start the server
  val bindingFuture = Http().bindAndHandle(routes, "localhost", 8080)

  // wait for the user to stop the server
  println("Press <enter> to exit.")
  Console.in.read.toChar

  // gracefully shutdown the server
  import system.dispatcher

  bindingFuture
    .flatMap(_.unbind())
    .onComplete(_ => system.shutdown())

}
