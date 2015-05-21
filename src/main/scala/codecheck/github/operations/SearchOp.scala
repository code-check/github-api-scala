package codecheck.github.operations

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import codecheck.github.api.GitHubAPI
import codecheck.github.models.SearchInput
import codecheck.github.models.SearchRepositoryResult

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
}
