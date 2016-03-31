import akka.actor.ActorSystem
import spray.json.pimpString
import scala.concurrent.Future
import scala.util.{Failure, Success}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import model.{IssueJsonFormats, Issue}
import service.IssueDBService

//import IssueJsonFormats
import akka.http.scaladsl.unmarshalling.FromRequestUnmarshaller
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Try

/**
  * Created by mmachiraju on 23/03/2016.
  */
object IssueManagementWithProperties extends App with IssueJsonFormats {
  // used to run the actors
  implicit val system = ActorSystem("IssueManagement")
  //  implicit val issueUnmarshaller: FromRequestUnmarshaller[Issue] = ???
  // materialises underlying flow definition into a set of actors
  implicit val materializer = ActorMaterializer()
  val routes = path("issue") {
    entity(as[String]) {
      extractedBody => {
        post {
          complete {
            val extractedIssue: Issue = extractedBody.parseJson.convertTo[Issue]
            var response = "Default Statement"
            IssueDBService.insert(extractedIssue)
              .onComplete {
                case Success(result) => response = s"Created an issue with $result.id and the given details extractedIssue"
                case Failure(e) => response = e.printStackTrace.toString
              }
            response
          }
        }
      }
    }
  } ~
    path("issues") {
      get {
        complete {
          IssueDBService.all.map(_.foldLeft("")((z, b) => z + b.toString + "\n"))
        }
      }
    } ~ path("issue" / IntNumber) {
    issueNumber => {
      entity(as[String]) {
        extractedBody => {
          get {
            complete {
              IssueDBService.select(issueNumber).onComplete {
                case Success(result) => s"Existing Issue with Id $issueNumber and with details $result retrieved"
                case Failure(e) => e.printStackTrace
              }
              "Reached here should not have Did nothing Bing"
            }
          } ~ put {
            complete {
              val extractedIssue: Issue = extractedBody.parseJson.convertTo[Issue]
              IssueDBService.update(issueNumber, extractedIssue)
                .onComplete {
                  case Success(result) => s"Updated an issue with $result.id and the given details $extractedIssue"
                  case Failure(e) => e.printStackTrace
                }
              "Reached here should not have Did nothing Booong"
            }
          }
        }
      }


    }
  }


  //  val routesQueryParams = path("issue") {
  //        post {
  //          parameters('name, 'person ? "Manogna", 'status ? "new").as(Issue){
  //            (name,person,status)=>{
  //
  //            }
  //          }
  //          complete {
  //            "Reached here should not have Did nothing Ding"
  //          }
  //        }
  //  } ~
  //    path("issues") {
  //      get {
  //        complete {
  //          IssueDBService.all.map(_.foldLeft("")((z, b) => z + b.toString + "\n"))
  //        }
  //      }
  //    } ~ path("issue" / IntNumber) {
  //    issueNumber => {
  //      entity(as[Issue]) {
  //        extractedBody => {
  //          get {
  //            complete {
  //              IssueDBService.select(issueNumber) .onComplete {
  //                case Success(result) => s"Existing Issue with Id $issueNumber and with details $result retrieved"
  //                case Failure(e) => e.printStackTrace
  //              }
  //              "Reached here should not have Did nothing Bing"
  //            }
  //          } ~ put {
  //            complete {
  //              IssueDBService.update(issueNumber,extractedBody)
  //                .onComplete {
  //                  case Success(result) => s"Updated an issue with $result.id and the given details $extractedBody"
  //                  case Failure(e) => e.printStackTrace
  //                }
  //              "Reached here should not have Did nothing Booong"
  //            }
  //          }
  //        }
  //      }
  //
  //
  //    }
  //  }


  // start the server
  val bindingFuture: Future[Http.ServerBinding] = Http().bindAndHandle(routes, "localhost", 8080)

  // wait for the user to stop the server
  println("Press <enter> to exit.")
  Console.in.read.toChar

  // gracefully shutdown the server
  //  import system.dispatcher

  bindingFuture
    .flatMap(_.unbind())
    .onComplete(_ => system.shut)

}
