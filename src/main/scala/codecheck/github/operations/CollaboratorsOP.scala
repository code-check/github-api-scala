package codecheck.github.operations

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import org.json4s.JArray

import codecheck.github.api.GitHubAPI
import codecheck.github.exceptions.NotFoundException
import codecheck.github.models.User

trait CollaboratorsOp {
  self: GitHubAPI =>

  def listCollaborators(owner: String, repo: String): Future[List[User]] = {
	val path = s"/repos/$owner/$repo/collaborators"
	exec("GET", path).map( 
      _.body match {
	      case JArray(arr) => arr.map(v => User(v))
      	case _ => throw new IllegalStateException()
      }
    )
  }
}