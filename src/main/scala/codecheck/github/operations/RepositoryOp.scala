package codecheck.github.operations

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import org.json4s.{JArray, JString}

import codecheck.github.api.GitHubAPI
import codecheck.github.exceptions.NotFoundException
import codecheck.github.utils.ToDo
import codecheck.github.models.Repository
import codecheck.github.models.RepositoryInput
import codecheck.github.models.RepositoryListOption
import codecheck.github.models.LanguageList

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
    def handleRedirect(url: String): Future[Option[Repository]] = {
      val regex = "https://api.github.com/repositories/(\\d+)".r
      url match {
        case regex(id) => getRepositoryById(id.toLong)
        case _ => Future.successful(None)
      }
    }
    exec("GET", s"/repos/$owner/$repo", fail404=false).flatMap { res =>
      res.statusCode match {
        case 200 => Future.successful(Some(Repository(res.body)))
        case 301 =>
          res.body \ "url" match {
            case JString(url) => handleRedirect(url)
            case _ => Future.successful(None)
          }
        case 404 => Future.successful(None)
      }
    }
  }

  def getRepositoryById(id: Long): Future[Option[Repository]] = {
    exec("GET", s"/repositories/$id", fail404=false).map { res =>
      res.statusCode match {
        case 200 => Some(Repository(res.body))
        case 404 => None
      }
    }
  }

  def createUserRepository(input: RepositoryInput): Future[Repository] = {
    exec("POST", s"/user/repos", input.value).map { res =>
      new Repository(res.body)
    }
  }
  def updateRepository(input: RepositoryInput): Future[Repository] = ToDo[Future[Repository]]

  def removeRepository(owner: String, repo: String): Future[Boolean] = {
    val path = s"/repos/$owner/$repo"
    exec("DELETE", path).map { v =>
      v.statusCode == 204
    }
  }

  def listLanguages(owner: String, repo: String): Future[LanguageList] = {
    val path = s"/repos/$owner/$repo/languages"
    exec("GET", path).map { res =>
      LanguageList(res.body)
    }
  }

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
