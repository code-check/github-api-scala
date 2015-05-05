package codecheck.github.app.commands

import scala.concurrent.ExecutionContext.Implicits.global
import codecheck.github.api.GitHubAPI
import scala.concurrent.Future
import codecheck.github.app.Repo
import codecheck.github.app.Command
import codecheck.github.app.CommandSetting

class ChangeRepositoryCommand(val api: GitHubAPI) extends Command {

  def check(repo: Repo): Future[Some[Repo]] = {
    api.getRepository(repo.owner, repo.name).map{ret =>
      ret.map { v =>
        val p = v.permissions
        print("Your permissions: ")
        if (p.admin) print("admin ")
        if (p.push) print("push ")
        if (p.pull) print("pull ")
        println
        v
      }.orElse {
        println(s"Repository ${repo.owner}/${repo.name} is not found.")
        None
      }
    }.transform(
      (_ => Some(repo)),
      (_ => new Exception(s"Repository ${repo.owner}/${repo.name} is not found."))
    )
  }

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
        None
    }
    repo.map(check(_).map(v => setting.copy(repo=v))).getOrElse(Future(setting))
  }
}
