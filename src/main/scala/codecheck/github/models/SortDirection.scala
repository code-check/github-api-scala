package codecheck.github.models

sealed abstract class SortDirection(val name: String) {
  override def toString = name
}

object SortDirection {
  case object asc  extends SortDirection("asc")
  case object desc extends SortDirection("desc")

  val values = Array(asc, desc)

  def fromString(str: String) = values.filter(_.name == str).head
}
