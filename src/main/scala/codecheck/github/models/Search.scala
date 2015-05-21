package codecheck.github.models

import org.json4s.JValue
import codecheck.github.models.SortDirection

sealed abstract class SearchSort(val name: String) {
  override def toString = name
}
object SearchSort {
 case object stars extends SearchSort("stars")
 case object forks extends SearchSort("forks")
 case object updated extends SearchSort("updated")
}

case class SearchInput (
 q: String,
 sort: Option[SearchSort] = None,
 order: SortDirection = SortDirection.desc
) extends AbstractInput

case class SearchRepositoryResult(value: JValue) extends AbstractJson(value) {
 def total_count: Long = get("total_count").toLong
 def incomplete_results: Boolean = boolean("incomplete_results")
 lazy val items = Repository(value \ "items")
}
