package codecheck.github.app.commands

import scala.concurrent.ExecutionContext.Implicits.global
import codecheck.github.api.GitHubAPI
import scala.concurrent.Future
import codecheck.github.app.Repo
import codecheck.github.app.Command
import codecheck.github.app.CommandSetting

class ChangeRepositoryCommand(val api: GitHubAPI) extends Command {
  def run(setting: CommandSetting, args: List[String]): Future[CommandSetting] = {
    val repo = args match {
      case str :: Nil =>
        var split = str.split("/")
        val repo = if (split.length == 2) {
          Repo(split(0), split(1))
        } else {
          Repo(setting.repositoryOwner.getOrElse(api.user.login), str)
        }
        Some(repo)
      case owner :: repo :: Nil =>
        Some(Repo(owner, repo))
      case _ =>
        println("cr [OWNER] [REPO]")
        setting.repo
    }
    Future(setting.copy(repo=repo))
  }
}
