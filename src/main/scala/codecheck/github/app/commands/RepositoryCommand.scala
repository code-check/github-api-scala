package codecheck.github.app.commands

import java.io.File
import codecheck.github.api.GitHubAPI
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import codecheck.github.app.Command
import codecheck.github.app.CommandSetting
import codecheck.github.app.Repo
import codecheck.github.models.Repository
import codecheck.github.models.RepositoryListOption
import codecheck.github.models.RepositoryListType
import codecheck.github.models.RepositorySort
import codecheck.github.models.SortDirection
import codecheck.github.utils.PrintList
import scopt.OptionParser


class RepositoryCommand(val api: GitHubAPI) extends Command {
  case class Config(
    cmd: String = "list",
    user: Option[String] = None,
    org: Option[String] = None,
    listType: String = "all",
    sort: String = "full_name",
    direction: String = "asc"
  )

  val parser = new OptionParser[Config]("label") {
    cmd("list") action { (x, c) =>
      c.copy(cmd="list")
    } text("list repositories") children(
      opt[String]('u', "user") action { (x, c) =>
        c.copy(user=Some(x))
      } text("List public repositories for the specified user."),
      opt[String]('o', "org") action { (x, c) =>
        c.copy(org=Some(x))
      } text("List repositories for the specified org."),
      opt[String]("type") action { (x, c) =>
        c.copy(listType=x)
      } text("Can be one of all, owner, public, private, member, forks, sources. Default: all"),
      opt[String]("sort") action { (x, c) =>
        c.copy(sort=x)
      } text("Can be one of created, updated, pushed, full_name. Default: full_name"),
      opt[String]("direction") action { (x, c) =>
        c.copy(direction=x)
      } text("The direction of the sort. Either asc or desc. Default: asc")
    )

  }

  def run(setting: CommandSetting, args: List[String]): Future[CommandSetting] = {
    parser.parse(args, new Config()) match {
      case Some(config) =>
        runSubcommand(config).map(_ => setting)
      case None =>
        Future(setting)
    }
  }

  def runSubcommand(config: Config): Future[Any] = {
    config.cmd match {
      case "list" =>
        list(config)
      case _ =>
        parser.showUsage
        Future(true)
    }
  }

  def list(config: Config): Future[Any] = {
    val option = RepositoryListOption(
      RepositoryListType.fromString(config.listType),
      RepositorySort.fromString(config.sort),
      SortDirection.fromString(config.direction)
    )
    config.user.map(u => api.listUserRepositories(u, option))
      .orElse(config.org.map(o => api.listOrgRepositories(o, option)))
      .getOrElse(api.listOwnRepositories())
      .map { list =>
        val owner = config.user.orElse(config.org).getOrElse(api.user.login)
        val rows = list.map { repo =>
          List(repo.name, repo.description.getOrElse(""), repo.open_issues_count)
        }
        PrintList("name", "description", "issues").build(rows)
      true
    }
  }

}

