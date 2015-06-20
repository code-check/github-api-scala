package codecheck.github.models
import org.json4s.JValue

case class BranchListItem(value: JValue) extends AbstractJson(value) {
  def name = get("name")
  lazy val commit = CommitInfo(value \ "commit")
}

case class CommitInfo(value: JValue) extends AbstractJson(value) {
  def sha = get("sha")
  def url = get("url")
}

case class Branch(value: JValue) extends AbstractJson(value) {
  def name = get("name")
}
