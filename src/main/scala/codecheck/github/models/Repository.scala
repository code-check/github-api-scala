package codecheck.github.models

import org.json4s.JValue
import codecheck.github.utils.ToDo

sealed abstract class RepositoryListType(val name: String) {
  override def toString = name
}

object RepositoryListType {
  case object all extends RepositoryListType("all")
  case object owner extends RepositoryListType("owner")
  case object public extends RepositoryListType("public")
  case object private_ extends RepositoryListType("private")
  case object member extends RepositoryListType("member")
  case object forks extends RepositoryListType("forks")
  case object sources extends RepositoryListType("sources")

  val values = Array(all, owner, public, private_, member, forks, sources)

  def fromString(str: String) = if (str == "private") {
    private_
  } else {
    values.filter(_.name == str).head
  }
}

sealed abstract class RepositorySort(val name: String) {
  override def toString = name
}

object RepositorySort {
  case object created extends RepositorySort("created")
  case object updated extends RepositorySort("updated")
  case object pushed extends RepositorySort("pushed")
  case object full_name extends RepositorySort("full_name")

  val values = Array(created, updated, pushed, full_name)

  def fromString(str: String) = values.filter(_.name == str).head
}

case class RepositoryListOption(
  listType: RepositoryListType = RepositoryListType.all,
  sort: RepositorySort = RepositorySort.full_name,
  direction: SortDirection = SortDirection.asc
)

/*case*/ class RepositoryInput extends ToDo

case class Repository(value: JValue) extends AbstractJson(value) {
  def id = get("id").toLong
  def name = get("name")
  def full_name = get("full_name")
  def url = get("url")
  def language = get("language")
  def stargazers_count = get("stargazers_count")

  def description = opt("description")
  def open_issues_count = get("open_issues_count").toInt

  lazy val permissions = Permissions(value \ "permissions")
  lazy val owner = User(value \ "owner")
}

case class Permissions(value: JValue) extends AbstractJson(value) {
  def admin = boolean("admin")
  def push = boolean("push")
  def pull = boolean("pull")
}
