package codecheck.github.operations

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import org.json4s.JArray

import codecheck.github.api.GitHubAPI
import codecheck.github.exceptions.NotFoundException
import codecheck.github.utils.ToDo
import codecheck.github.models.Repository
import codecheck.github.models.RepositoryInput
import codecheck.github.models.RepositoryListOption

trait RepositoryOp {
  self: GitHubAPI =>

  private def doList(path: String, option: RepositoryListOption): Future[List[Repository]] = {
    exec("GET", path + s"?type=${option.listType}&sort=${option.sort}&direction=${option.direction}").map { res =>
      res.body match {
        case JArray(arr) => arr.map(v => Repository(v))
        case _ => throw new IllegalStateException()
      }
    }
  }
  def listOwnRepositories(option: RepositoryListOption = RepositoryListOption()): Future[List[Repository]] = 
    doList("/user/repos", option)

  def listUserRepositories(user: String, option: RepositoryListOption = RepositoryListOption()): Future[List[Repository]] = 
    doList(s"/users/$user/repos", option)

  def listOrgRepositories(org: String, option: RepositoryListOption = RepositoryListOption()): Future[List[Repository]] =
    doList(s"/orgs/$org/repos", option)

  def listAllPublicRepositories(since: Long = 0): Future[List[Repository]] = {
    val q = if (since == 0) "" else "?since=" + since
    exec("GET", "/repositories" + q).map { res =>
      res.body match {
        case JArray(arr) => arr.map(v => Repository(v))
        case _ => throw new IllegalStateException()
      }
    }
  }


  def getRepository(owner: String, repo: String): Future[Option[Repository]] = {
    exec("GET", s"/repos/$owner/$repo", fail404=false).map { res =>
      res.statusCode match {
        case 404 => None
        case 200 => Some(Repository(res.body))
      }
    }
  }

  def createRepository(input: RepositoryInput): Future[Repository] = ToDo[Future[Repository]]
  def updateRepository(input: RepositoryInput): Future[Repository] = ToDo[Future[Repository]]

/*
List contributors
List languages
List Teams
List Tags
List Branches
Get Branch
Delete a Repository
*/

}
