package codecheck.github.operations

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import codecheck.github.api.GitHubAPI
import codecheck.github.models.User
import codecheck.github.models.UserInput
import codecheck.github.utils.ToDo

trait UserOp {
  self: GitHubAPI =>

  def getAuthenticatedUser: Future[User] = exec("GET", "/user").map { res =>
    User(res.body)
  }

  def getUser(username: String): Future[Option[User]] = {
    exec("GET", s"/users/${username}", fail404=false).map { res =>
      res.statusCode match {
        case 404 => None
        case 200 => Some(new User(res.body))
      }
    }
  }
  def updateAuthenticatedUser(input: UserInput): Future[User] = ToDo[Future[User]]
  def getAllUsers(sinse: Long = 0): Future[List[User]] = ToDo[Future[List[User]]]
}
