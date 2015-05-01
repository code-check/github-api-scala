package codecheck.github.app

import codecheck.github.api.GitHubAPI
import scala.concurrent.Future

trait Command {
  val api: GitHubAPI

  def parseRepo(str: String, origin: Option[Repo] = None): Repo = {
    val split = str.split("/")
    if (split.length == 2) {
      Repo(split(0), split(1))
    } else {
      Repo(origin.map(_.owner).getOrElse(api.user.login), str)
    }
  }
  def run(setting: CommandSetting, args: List[String]): Future[CommandSetting]
}