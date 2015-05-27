package codecheck.github.operations

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import codecheck.github.api.GitHubAPI
import codecheck.github.models.SearchInput
import codecheck.github.models.SearchRepositoryResult
import codecheck.github.models.SearchCodeResult
import codecheck.github.models.SearchIssueResult
import codecheck.github.models.SearchUserResult

trait SearchOp {
 self: GitHubAPI =>

  def searchRepositories(input: SearchInput): Future[SearchRepositoryResult] = {
    val path = s"/search/repositories?q=${input.q}&sort=${input.sort}&order=${input.order}"
    exec("GET", path ).map { res =>
      SearchRepositoryResult(res.body)
   }
  }

  def searchCode(input: SearchInput): Future[SearchCodeResult] = {
    val path = s"/search/code?q=${input.q}&sort=${input.sort}&order=${input.order}"
    exec("GET", path ).map { res =>
      SearchCodeResult(res.body)
    }
  }

  def searchIssues(input: SearchInput): Future[SearchIssueResult] = {
    val path = s"/search/issues?q=${input.q}&sort=${input.sort}&order=${input.order}"
    exec("GET", path ).map { res =>
      SearchIssueResult(res.body)
    }
  }

  def searchUser(input: SearchInput): Future[SearchUserResult] = {
    val path = s"/search/users?q=${input.q}&sort=${input.sort}&order=${input.order}"
    exec("GET", path ).map { res =>
      SearchUserResult(res.body)
    }
  }
}
