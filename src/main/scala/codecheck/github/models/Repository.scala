package codecheck.github.models

import org.json4s.JValue
import codecheck.github.utils.ToDo

sealed abstract class RepositoryListType(name: String) {
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
}

sealed abstract class RepositorySort(name: String) {
  override def toString = name
}

object RepositorySort {
  case object created extends RepositorySort("created")
  case object updated extends RepositorySort("updated")
  case object pushed extends RepositorySort("pushed")
  case object full_name extends RepositorySort("full_name")
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

  lazy val owner = new User(value \ "owner")
}

