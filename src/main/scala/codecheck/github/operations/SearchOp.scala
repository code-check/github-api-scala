package codecheck.github.operations

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import codecheck.github.api.GitHubAPI
import codecheck.github.models.SearchInput
import codecheck.github.models.SearchRepositoryResult
//import codecheck.github.models.SortDirection
import codecheck.github.models.SearchCodeResult
import codecheck.github.models.SearchIssueResult
import codecheck.github.models.SearchUserResult

trait SearchOp {
 self: GitHubAPI =>

  def searchRepositories(input: SearchInput): Future[Option[SearchRepositoryResult]] = {
    val path = s"/search/repositories?q=${input.q}&sort=${input.sort}&order=${input.order}"
    exec("GET", path ).map { res =>
      res.statusCode match {
        case 200 => Some(SearchRepositoryResult(res.body))
        case 404 => None
     }
   }
  }

  def searchCode(input: SearchInput): Future[Option[SearchCodeResult]] = {
    val path = s"/search/code?q=${input.q}&sort=${input.sort}&order=${input.order}"
    exec("GET", path ).map { res =>
      res.statusCode match {
        case 200 => Some(SearchCodeResult(res.body))
        case 404 => None
     }
    }
  }

  def searchIssues(input: SearchInput): Future[Option[SearchIssueResult]] = {
    val path = s"/search/issues?q=${input.q}&sort=${input.sort}&order=${input.order}"
    exec("GET", path ).map { res =>
      res.statusCode match {
        case 200 => Some(SearchIssueResult(res.body))
        case 404 => None
     }
    }
  }

  def searchUser(input: SearchInput): Future[Option[SearchUserResult]] = {
    val path = s"/search/users?q=${input.q}&sort=${input.sort}&order=${input.order}"
    exec("GET", path ).map { res =>
      res.statusCode match {
        case 200 => Some(SearchUserResult(res.body))
        case 404 => None
     }
    }
  }
}
