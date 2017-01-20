package codecheck.github.models

import org.json4s.JValue
import org.json4s.JArray

sealed trait SearchSort {
  def name: String
  override def toString = name
}

sealed abstract class SearchRepositorySort(val name: String) extends SearchSort

object SearchRepositorySort {
  case object stars   extends SearchRepositorySort("stars")
  case object forks   extends SearchRepositorySort("forks")
  case object updated extends SearchRepositorySort("updated")

  val values = Array(stars, forks, updated)

  def fromString(str: String) = values.filter(_.name == str).head
}

sealed abstract class SearchCodeSort(val name: String) extends SearchSort

object SearchCodeSort {
  case object indexed extends SearchCodeSort("indexed")

  val values = Array(indexed)

  def fromString(str: String) = values.filter(_.name == str).head
}

sealed abstract class SearchIssueSort(val name: String) extends SearchSort

object SearchIssueSort {
  case object created  extends SearchIssueSort("created")
  case object updated  extends SearchIssueSort("updated")
  case object comments extends SearchIssueSort("comments")

  val values = Array(created, updated, comments)

  def fromString(str: String) = values.filter(_.name == str).head
}

sealed abstract class SearchUserSort(val name: String) extends SearchSort

object SearchUserSort {
  case object followers    extends SearchUserSort("followers")
  case object repositories extends SearchUserSort("repositories")
  case object joined       extends SearchUserSort("joined")

  val values = Array(followers, repositories, joined)

  def fromString(str: String) = values.filter(_.name == str).head
}

sealed trait SearchInput extends AbstractInput {
 def q: String
 def sort: Option[SearchSort]
 def order: SortDirection
 def query = s"?q=$q" + sort.map(sortBy => s"&sort=$sortBy&order=$order").getOrElse("")
}

case class SearchRepositoryInput (
 val q: String,
 val sort: Option[SearchRepositorySort] = None,
 val order: SortDirection = SortDirection.desc
) extends SearchInput

case class SearchRepositoryResult(value: JValue) extends AbstractJson(value) {
 def total_count: Long = get("total_count").toLong
 def incomplete_results: Boolean = boolean("incomplete_results")
 lazy val items = (value \ "items") match {
    case JArray(arr) => arr.map(Repository(_))
    case _ => Nil
  }
}

case class SearchCodeInput (
 q: String,
 sort: Option[SearchCodeSort] = None,
 order: SortDirection = SortDirection.desc
) extends SearchInput

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

case class SearchIssueInput (
 q: String,
 sort: Option[SearchIssueSort] = None,
 order: SortDirection = SortDirection.desc
) extends SearchInput

case class SearchIssueResult(value: JValue) extends AbstractJson(value) {
 def total_count: Long = get("total_count").toLong
 def incomplete_results: Boolean = boolean("incomplete_results")
 lazy val items = (value \ "items") match {
    case JArray(arr) => arr.map(new Issue(_))
    case _ => Nil
  }
}

case class SearchUserInput (
 q: String,
 sort: Option[SearchUserSort] = None,
 order: SortDirection = SortDirection.desc
) extends SearchInput

case class SearchUserResult(value: JValue) extends AbstractJson(value) {
 def total_count: Long = get("total_count").toLong
 def incomplete_results: Boolean = boolean("incomplete_results")
 lazy val items = (value \ "items") match {
    case JArray(arr) => arr.map(new User(_))
    case _ => Nil
  }
}
