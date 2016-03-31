package model

import spray.json.DefaultJsonProtocol

/**
  * Created by mmachiraju on 30/03/2016.
  */

case class Issue(id: Long, name: String, assignedTo: Option[Person], status: String)

case class Person(id: Long, name: String)

trait IssueJsonFormats extends DefaultJsonProtocol {
  implicit val personJsonFormatter = jsonFormat2(Person.apply)
  implicit val issueFormat = jsonFormat4(Issue.apply)

}

