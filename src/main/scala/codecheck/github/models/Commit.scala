package codecheck.github.models

import org.json4s.JValue
import org.json4s.jackson.JsonMethods
import org.json4s.JsonDSL._

case class Commit(value: JValue) extends AbstractJson(value) {
  def sha = get("sha")
  def url = get("url")
  def message = get("message")

  lazy val author = CommitUser(value \ "author")
  lazy val committer = CommitUser(value \ "committer")
  lazy val tree = Tree(value \ "tree")
  lazy val parents = seqOpt[Tree]("parents")
}

case class CommitUser(value: JValue) extends AbstractJson(value) {
//  def date = date("date")
  def name = get("name")
  def email = get("email")
}

case class Tree(value: JValue) extends AbstractJson(value) {
  def url = get("url")
  def sha = get("sha")
}
