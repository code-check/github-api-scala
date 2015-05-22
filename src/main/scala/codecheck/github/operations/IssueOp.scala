package codecheck.github.operations

import java.net.URLEncoder
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import org.json4s.JArray
import org.json4s.JString
import org.json4s.JNothing

import codecheck.github.api.GitHubAPI
import codecheck.github.exceptions.NotFoundException
import codecheck.github.models.IssueInput
import codecheck.github.models.Issue
import codecheck.github.models.IssueListOption
import codecheck.github.models.IssueListOption4Repository

trait IssueOp {
  self: GitHubAPI =>

  private def doList(path: String): Future[List[Issue]] = {
    exec("GET", path).map(
      _.body match {
        case JArray(arr) => arr.map(v => Issue(v))
        case _ => throw new IllegalStateException()
      }
    )
  }

  def listAllIssues(option: IssueListOption = IssueListOption()): Future[List[Issue]] =
    doList("/issues" + option.q)

  def listUserIssues(option: IssueListOption = IssueListOption()): Future[List[Issue]] =
    doList("/user/issues" + option.q)

  def listOrgIssues(org: String, option: IssueListOption = IssueListOption()): Future[List[Issue]] =
    doList(s"/orgs/$org/issues" + option.q)

  def listRepositoryIssues(owner: String, repo: String, option: IssueListOption4Repository = IssueListOption4Repository()): Future[List[Issue]] =
    doList(s"/repos/$owner/$repo/issues" + option.q)

  def getIssue(owner: String, repo: String, number: Long): Future[Option[Issue]] =
    exec("GET", s"/repos/$owner/$repo/issues/$number", fail404=false).map(res =>
      res.statusCode match {
        case 404 => None
        case 200 => Some(Issue(res.body))
      }
    )

  def createIssue(owner: String, repo: String, input: IssueInput): Future[Issue] = {
    val path = s"/repos/$owner/$repo/issues"
    exec("POST", path, input.value).map { result =>
      new Issue(result.body)
    }
  }

  def editIssue(owner: String, repo: String, number: Long, input: IssueInput): Future[Issue] = {
    val path = s"/repos/$owner/$repo/issues/$number"
    exec("PATCH", path, input.value).map { result =>
      new Issue(result.body)
    }
  }

  def assign(owner: String, repo: String, number: Long, assignee: String): Future[Issue] = {
    editIssue(owner, repo, number, IssueInput(assignee=Some(assignee)))
  }

  def unassign(owner: String, repo: String, number: Long): Future[Issue] = {
    editIssue(owner, repo, number, IssueInput(assignee=Some("")))
  }

}
