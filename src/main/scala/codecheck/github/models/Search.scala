package codecheck.github.models

import org.json4s.JValue
import org.json4s.JArray

sealed abstract class SearchSort(val name: String) {
 override def toString = name
}
object SearchSort {
  //for serachRepositories
  case object stars extends SearchSort("stars")
  case object forks extends SearchSort("forks")
  case object updated extends SearchSort("updated")

  //for searchCode
  case object indexed extends SearchSort("indexed")

  //for searchIssues
  case object comments extends SearchSort("comments")
  case object created extends SearchSort("created")
  //case object updated extends SearchSort("updated")

  //for searchUser
  case object followers extends SearchSort("followers")
  case object repositories extends SearchSort("repositories")
  case object joined extends SearchSort("joined")
}

case class SearchInput (
 q: String,
 sort: Option[SearchSort] = None,
 order: SortDirection = SortDirection.desc
) extends AbstractInput

case class SearchRepositoryResult(value: JValue) extends AbstractJson(value) {
 def total_count: Long = get("total_count").toLong
 def incomplete_results: Boolean = boolean("incomplete_results")
 lazy val items = (value \ "items") match {
    case JArray(arr) => arr.map(new Repository(_))
    case _ => Nil
  }
}

case class searchCodeItems (value: JValue) extends AbstractJson(value){
  def name: String = get("name")
  lazy val Repo = new Repository(value \ "repository")
}

case class SearchCodeResult(value: JValue) extends AbstractJson(value) {
 def total_count: Long = get("total_count").toLong
 def incomplete_results: Boolean = boolean("incomplete_results")
 lazy val items = (value \ "items") match {
    case JArray(arr) => arr.map(new searchCodeItems(_))
    case _ => Nil
  }
}

case class SearchIssueResult(value: JValue) extends AbstractJson(value) {
 def total_count: Long = get("total_count").toLong
 def incomplete_results: Boolean = boolean("incomplete_results")
 lazy val items = (value \ "items") match {
    case JArray(arr) => arr.map(new Issue(_))
    case _ => Nil
  }
}

case class SearchUserResult(value: JValue) extends AbstractJson(value) {
 def total_count: Long = get("total_count").toLong
 def incomplete_results: Boolean = boolean("incomplete_results")
 lazy val items = (value \ "items") match {
    case JArray(arr) => arr.map(new User(_))
    case _ => Nil
  }
}
