package service

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

import model.Issue

import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, _}

/**
  * Created by mmachiraju on 30/03/2016.
  */
object IssueDBService {
  private val idGenerator = IdGenerator()

  private val issueDatabase = new ConcurrentHashMap[Long, Issue]().asScala

  def nextIssueId(): Long = idGenerator.next

  def insert(u: Issue): Future[Issue] = Future {
    val IssueWithId = u.copy(id = nextIssueId())
    issueDatabase += (IssueWithId.id -> IssueWithId)
    IssueWithId
  }


  def update(id : Long,updatedIssue: Issue): Future[Option[Issue]] = Future {
    if (issueDatabase.contains(id)) {
      issueDatabase.replace(id, updatedIssue)
      Some(updatedIssue)
    } else {
      None
    }
  }

  def search(searchStatus: String, searchName: Option[String]): Future[List[Issue]] = {
    Future {
      //      issueDatabase.filter { case (id, issue) => searchStatus.equals(issue.status) }
      //        .filter { case (id, issue) => searchName.map(issue.name.contains(_)).getOrElse (true) }
      //        .values.toList
      issueDatabase
        .filter(currentIssue => searchStatus.equals
        (currentIssue._2.status))
        .filter(currentIssue => searchName.equals(currentIssue._2.name)).values.toList;
    }
  }

  def delete(id: Long): Future[Option[Issue]] = Future {
    issueDatabase.remove(id)
  }

  def select(id: Long): Future[Option[Issue]] = Future {
    issueDatabase.get(id)
  }

  def all: Future[List[Issue]] = Future {
    issueDatabase.values.toList
  }

  case class IdGenerator() {
    private val id = new AtomicLong

    def next: Long = id.incrementAndGet
  }

}
