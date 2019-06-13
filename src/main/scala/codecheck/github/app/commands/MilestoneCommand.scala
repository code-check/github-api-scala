package codecheck.github.app.commands

import java.io.File
import java.util.Calendar
import org.joda.time.DateTime
import codecheck.github.api.GitHubAPI
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import codecheck.github.app.Command
import codecheck.github.app.CommandSetting
import codecheck.github.app.Repo
import codecheck.github.models.AbstractJson
import codecheck.github.models.Milestone
import codecheck.github.models.MilestoneInput
import codecheck.github.models.MilestoneListOption
import codecheck.github.models.MilestoneState
import codecheck.github.models.MilestoneSort
import codecheck.github.models.SortDirection
import codecheck.github.utils.PrintList
import scopt.OptionParser
import org.json4s._
import org.json4s.jackson.JsonMethods


class MilestoneCommand(val api: GitHubAPI) extends Command {
  case class Config(
    repo: Option[Repo],
    cmd: String = "list",
    sort: String = "due_date",
    direction: String = "asc",
    verbose: Boolean = false,
    title: Option[String] = None,
    description: Option[String] = None,
    due_on: Option[Calendar] = None,
    state: Option[String] = None,
    number: Int = 0,
    file: File = null
  ) {
    def input = MilestoneInput(
      title,
      state.map(MilestoneState.fromString(_)),
      description,
      due_on.map(new DateTime(_))
    )

    def listOption = MilestoneListOption(
      MilestoneState.fromString(state.getOrElse("open")),
      MilestoneSort.fromString(sort),
      SortDirection.fromString(direction)
    )
  }

  val parser = new OptionParser[Config]("milestone") {
    opt[String]('r', "repo") action { (x, c) =>
      c.copy(repo=Some(parseRepo(x, c.repo)))
    } text("target repository [OWNER]/[REPO]")

    cmd("list") action { (x, c) =>
      c.copy(cmd="list")
    } text("List milestones") children(
      opt[String]("state") action { (x, c) =>
        c.copy(state=Some(x))
      } text("The state of the milestone. Either open, closed, or all. Default: open"),
      opt[String]("sort") action { (x, c) =>
        c.copy(sort=x)
      } text("What to sort results by. Either due_date or completeness. Default: due_date"),
      opt[String]("direction") action { (x, c) =>
        c.copy(direction=x)
      } text("The direction of the sort. Either asc or desc. Default: asc"),
      opt[Unit]('v', "verbose") action { (_, c) =>
        c.copy(verbose=true)
      } text("Show detailed output or not. Default: false")
    )

    cmd("add") action { (x, c) =>
      c.copy(cmd="add")
    } text("Create a milestone.") children (
      arg[String]("<title>") action { (x, c) =>
        c.copy(title=Some(x))
      } text("Required. The title of the milestone."),

      opt[String]('D', "description") action { (x, c) =>
        c.copy(description=Some(x))
      } text("A description of the milestone."),

      opt[Calendar]('d', "due_on") action { (x, c) =>
        c.copy(due_on=Some(x))
      } text("The milestone due date. Format: YYYY-MM-DD.")
    )

    cmd("merge") action { (x, c) =>
      c.copy(cmd="merge")
    } text("Create or update milestones from file") children(
      arg[File]("<file>") action { (x, c) =>
        c.copy(file=x)
      } text("Json file to merge")
    )

    cmd("update") action { (x, c) =>
      c.copy(cmd="update")
    } text("Update a milestone.") children (
      arg[Int]("<number>") action { (x, c) =>
        c.copy(number=x)
      } text("Required. The number of milestone."),

      opt[String]('t', "title") action { (x, c) =>
        c.copy(title=Some(x))
      } text("The title of the milestone."),

      opt[String]('s', "state") action { (x, c) =>
        c.copy(state=Some(x))
      } text("The state of the milestone. Either open or closed."),

      opt[Unit]('c', "close") action { (_, c) =>
        c.copy(state=Some("closed"))
      } text("Change state to closed."),

      opt[String]('D', "description") action { (x, c) =>
        c.copy(description=Some(x))
      } text("A description of the milestone."),

      opt[Calendar]('d', "due_on") action { (x, c) =>
        c.copy(due_on=Some(x))
      } text("The milestone due date. Format: YYYY-MM-DD.")
    )

    cmd("rm") action { (x, c) =>
      c.copy(cmd="rm")
    } text("remove a label") children (
      arg[Int]("<number>") action { (x, c) =>
        c.copy(number=x)
      } text("Required. The number of milestone.")
    )

  }

  def run(setting: CommandSetting, args: List[String]): Future[CommandSetting] = {
    parser.parse(args, new Config(setting.repo)) match {
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
      case "add" =>
        add(config)
      case "merge" =>
        merge(config)
      case "update" =>
        update(config)
      case "rm" =>
        remove(config)
      case _ =>
        parser.showUsage
        Future(true)
    }
  }

  def printDetail(m: Milestone): Unit = {
    println(s"""
      |---------------------------------------------
      |${m.title}
      |  number: ${m.number}
      |  due_on: ${m.due_on.getOrElse("")}
      |  open  : ${m.open_issues}
      |  closed: ${m.closed_issues}
    """.stripMargin)
    m.description.foreach(println(_))
  }

  def list(config: Config): Future[Any] = withRepo(config.repo) { rapi =>
    rapi.listMilestones(config.listOption).map { list =>
      if (config.verbose) {
        list.foreach(printDetail)
      } else {
        val rows = list.map { m =>
          List(
            m.number,
            m.title,
            s"${m.open_issues}/${m.open_issues + m.closed_issues}",
            m.due_on.map(_.toString("yyyy-MM-dd")).getOrElse("")
          )
        }
        PrintList("No.", "title", "count", "due_on").build(rows)
      }
      true
    }
  }

  def add(config: Config): Future[Any] = withRepo(config.repo) { rapi =>
    val input = config.input
    rapi.createMilestone(input).map { m =>
      printDetail(m)
      true
    }
  }

  def update(config: Config): Future[Any] = withRepo(config.repo) { rapi =>
    val input = config.input
    rapi.updateMilestone(config.number, input).map { m =>
      printDetail(m)
      true
    }
  }

  def remove(config: Config): Future[Any] = withRepo(config.repo) { rapi =>
    rapi.removeMilestone(config.number).map { b =>
      println(s"Removed ${config.number}")
      true
    }
  }

  def merge(config: Config): Future[Any] = withRepo(config.repo) { rapi =>
    def doCreateMilestone(milestone: Option[Milestone], input: MilestoneInput): Future[String] = {
      milestone match {
        case Some(m) =>
          rapi.updateMilestone(m.number, input).map(_ => s"Update milestone ${m.title}")
        case None =>
          rapi.createMilestone(input).map(m => s"Create milestone ${m.title}")
      }
    }
    val json = JsonMethods.parse(config.file)
    val items = (json match {
      case JArray(list) => list
      case JObject => List(json)
      case _ => throw new IllegalArgumentException("Invalid json file\n" + JsonMethods.pretty(json))
    }).map{ v => 
      val json = new AbstractJson(v)
      MilestoneInput(
        Some(json.get("title")),
        json.opt("state").map(MilestoneState.fromString(_)),
        json.opt("description"),
        json.opt("due_on").map(new DateTime(_))
      )
    }
    rapi.listMilestones(MilestoneListOption(state=MilestoneState.all)).flatMap { milestones =>
      val ret = items.map { input =>
        doCreateMilestone(milestones.find(
          m => input.title.exists(_ == m.title)
        ), input).map { s =>
          println(s)
          s
        }
      }
      Future.sequence(ret)
    }
  }

}

