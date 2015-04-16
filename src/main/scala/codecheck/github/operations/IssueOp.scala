package codecheck.github.operations

import java.net.URLEncoder
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import org.json4s.JArray
import org.json4s.JString
import org.json4s.JNothing

import codecheck.github.api.GitHubAPI
import codecheck.github.models.IssueEditParams
import codecheck.github.models.Issue

trait IssueOp {
  self: GitHubAPI =>

  def editIssue(owner: String, repo: String, number: Long, params: IssueEditParams): Future[Issue] = {
    val path = s"/repos/$owner/$repo/issues/$number"
    val body = params.toJson
    exec("PATCH", path, body).map { result =>
      new Issue(result.body)
    }
  }

  def assign(owner: String, repo: String, number: Long, assignee: String): Future[Issue] = {
    editIssue(owner, repo, number, IssueEditParams(assignee=Some(assignee)))
  }

  def unassign(owner: String, repo: String, number: Long): Future[Issue] = {
    editIssue(owner, repo, number, IssueEditParams(assignee=Some("none")))
  }

}
