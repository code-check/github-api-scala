package codecheck.github.app

import codecheck.github.api.GitHubAPI
import codecheck.github.api.RepositoryAPI
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import codecheck.github.utils.Json4s

trait Command {
  implicit val formats = Json4s.formats

  val api: GitHubAPI

  def parseRepo(str: String, origin: Option[Repo] = None): Repo = {
    val split = str.split("/")
    if (split.length == 2) {
      Repo(split(0), split(1))
    } else {
      Repo(origin.map(_.owner).getOrElse(api.user.login), str)
    }
  }

  def withRepo(repo: Option[Repo])(f: RepositoryAPI => Future[Any]): Future[Any] = {
    repo.map { v =>
      val rapi = api.repositoryAPI(v.owner, v.name)
      f(rapi)
    }.getOrElse {
      println("Repository not specified")
      Future(false)
    }
  }

  def run(setting: CommandSetting, args: List[String]): Future[CommandSetting]
}