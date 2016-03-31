package model

import spray.json.DefaultJsonProtocol

/**
  * Created by mmachiraju on 30/03/2016.
  */
object JsonParsing extends App {

  case class Color(name: String, red: Int, green: Int, blue: Int,country:Country)

  case class Country(id: Int ,name: String)

  object MyJsonProtocol extends DefaultJsonProtocol {
    implicit val colorFormat1 = jsonFormat2(Country)
    implicit val colorFormat = jsonFormat5(Color)

  }

  import MyJsonProtocol._
  import spray.json._


  val countryObject  = Country(1,"CadetBlue")
  val json = Color("CadetBlue", 95, 158, 160,countryObject).toJson
  val color = json.convertTo[Color]
  print(json.prettyPrint)


  case class Issue(id: Long, name: String, assignedTo: Person, status: String)

  case class Person(id: Long, name: String)

  object Protocols extends DefaultJsonProtocol {
    implicit val personJsonFormatter = jsonFormat2(Person.apply)
    implicit val issueFormat = jsonFormat4(Issue.apply)
  }

  import Protocols._
//  val person = Person(1, "Manogna")
//  val json1 = Issue(1, "NPS", person, "In Progress").toJson
//  val issue = json1.convertTo[Issue].copy(name="UA")
//  print(json1)
//  print(issue)

          val extractedBody :String =   "{\"id\":1,\"name\":\"NPS\",\"assignedTo\":{\"id\":1,\"name\":\"Manogna\"},\"status\":\"In Progress\"}"
  val extractedIssue: Issue = extractedBody.parseJson.convertTo[Issue]

  print(extractedIssue)
}
