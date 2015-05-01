package codecheck.github.operations

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import codecheck.github.api.GitHubAPI
import codecheck.github.models.User

trait UserOp {
  self: GitHubAPI =>

  def getAuthenticatedUser: Future[User] = exec("GET", "/user").map { res =>
    User(res.body)
  }
}
