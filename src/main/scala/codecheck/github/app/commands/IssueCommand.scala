package codecheck.github.app.commands

import java.io.File
import codecheck.github.api.GitHubAPI
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import codecheck.github.app.Command
import codecheck.github.app.CommandSetting
import codecheck.github.app.Repo
import codecheck.github.models.IssueListOption
import codecheck.github.models.IssueFilter
import codecheck.github.models.IssueState
import codecheck.github.models.IssueSort
import codecheck.github.models.SortDirection
import codecheck.github.utils.PrintList
import scopt.OptionParser
import org.joda.time.DateTime
import java.util.Calendar

class IssueCommand(val api: GitHubAPI) extends Command {
  case class Config(
    cmd: String = "list",
    user: Boolean = false,
    org: Option[String] = None,
    filter: String = "assigned",
    state: String = "open",
    labels: Seq[String] = Nil,
    sort: String = "created",
    direction: String = "desc",
    since: Option[Calendar] = None
  ) {

    def listOption = IssueListOption(
      IssueFilter.fromString(filter),
      IssueState.fromString(state),
      labels,
      IssueSort.fromString(sort),
      SortDirection.fromString(direction),
      since.map(new DateTime(_))
    )
  }

  val parser = new OptionParser[Config]("issue") {
    cmd("list") action { (x, c) =>
      c.copy(cmd="list")
    } text("List issues") children(
      opt[Unit]('u', "user") action { (_, c) =>
        c.copy(user=true)
      },
      opt[String]('o', "org") action { (x, c) =>
        c.copy(org=Some(x))
      } text("Organization name for listing issues."),
      opt[String]('f', "filter") action { (x, c) =>
        c.copy(filter=x)
      } text("Indicates which sorts of issues to return. Can be one of assigned, created, mentioned, subscribed, all. Default: assigned."),
      opt[String]("state") action { (x, c) =>
        c.copy(state=x)
      } text("Indicates the state of the issues to return. Can be either open, closed, or all. Default: open"),
      opt[Seq[String]]('l', "labels") action { (x, c) =>
        c.copy(labels=x)
      } text("A list of comma separated label names. Example: bug,ui,@high"),
      opt[String]("sort") action { (x, c) =>
        c.copy(sort=x)
      } text("What to sort results by. Can be either created, updated, comments. Default: created"),
      opt[String]("direction") action { (x, c) =>
        c.copy(direction=x)
      } text("The direction of the sort. Either asc or desc. Default: asc"),
      opt[Calendar]("since") action { (x, c) =>
        c.copy(since=Some(x))
      } text("Only issues updated at or after this time are returned. This is a timestamp in ISO 8601 format: YYYY-MM-DDTHH:MM:SSZ.")
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
    val option = config.listOption
    val future = if (config.user) {
      api.listUserIssues(option)
    } else {
      config.org.map { v =>
        api.listOrgIssues(v, option)
      }.getOrElse {
        api.listAllIssues(option)
      }
    }
    future.map { list =>
      val rows = list.map { i =>
        List(
          i.repository.owner.login,
          i.repository.name,
          i.number,
          i.title,
          i.assignee.map(_.login).getOrElse(""),
          i.milestone.map(_.title).getOrElse(""),
          i.comments,
          i.labels.map(_.name).mkString(", ")
        )
      }
      PrintList("owner", "repo", "No.", "title", "assignee", "milestone", "comments", "labels").build(rows)
    }
  }

}

