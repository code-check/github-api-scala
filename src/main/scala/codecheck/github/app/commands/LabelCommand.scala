package codecheck.github.app.commands

import java.io.File
import codecheck.github.api.GitHubAPI
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import codecheck.github.app.Command
import codecheck.github.app.CommandSetting
import codecheck.github.app.Repo
import codecheck.github.models.Label
import codecheck.github.models.LabelInput
import scopt.OptionParser
import org.json4s._
import org.json4s.jackson.JsonMethods


class LabelCommand(val api: GitHubAPI) extends Command {
  case class Config(
    repo: Option[Repo],
    cmd: String = "list",
    name: String = "",
    color: String = "",
    file: File = null,
    newName: Option[String] = None
  )

  val parser = new OptionParser[Config]("label") {
    opt[String]('r', "repo") action { (x, c) =>
      c.copy(repo=Some(parseRepo(x, c.repo)))
    } text("target repository [OWNER]/[REPO]")

    cmd("list") action { (x, c) =>
      c.copy(cmd="list")
    } text("list labels")

    cmd("add") action { (x, c) =>
      c.copy(cmd="add")
    } text("add a label") children (
      arg[String]("<name>") action { (x, c) =>
        c.copy(name=x)
      } text("Label name"),

      arg[String]("<color>") action { (x, c) =>
        c.copy(color=x)
      } text("Label color")
    )

    cmd("merge") action { (x, c) =>
      c.copy(cmd="merge")
    } text("add or update lables from file") children(
      arg[File]("<file>") action { (x, c) =>
        c.copy(file=x)
      } text("Json file to merge")
    )

    cmd("update") action { (x, c) =>
      c.copy(cmd="update")
    } text("update a label") children (
      arg[String]("<name>") action { (x, c) =>
        c.copy(name=x)
      } text("Label name"),

      arg[String]("<color>") action { (x, c) =>
        c.copy(color=x)
      } text("Label color"),

      opt[String]('n', "newname") action { (x, c) =>
        c.copy(newName=Some(x))
      } text("New name for label")
    )

    cmd("rm") action { (x, c) =>
      c.copy(cmd="rm")
    } text("remove a label") children (
      arg[String]("<name>") action { (x, c) =>
        c.copy(name=x)
      } text("Label name")
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

  def list(config: Config): Future[Any] = withRepo(config.repo) { rapi =>
    rapi.listLabelDefs.map { list =>
      val nameLen = list.map(_.name.length).max + 4
      list.foreach{ l =>
        val name = l.name + (" " * (nameLen - l.name.length))
        println(s"$name ${l.color}")
      } 
      true
    }
  }

  def add(config: Config): Future[Any] = withRepo(config.repo) { rapi =>
    val input = LabelInput(config.name, config.color)
    rapi.createLabelDef(input).map { l =>
      println(s"Created ${l.name} - ${l.color}")
      true
    }
  }

  def update(config: Config): Future[Any] = withRepo(config.repo) { rapi =>
    val input = LabelInput(config.newName.getOrElse(config.name), config.color)
    rapi.updateLabelDef(config.name, input).map { l =>
      println(s"Updated ${l.name} - ${l.color}")
      true
    }
  }

  def remove(config: Config): Future[Any] = withRepo(config.repo) { rapi =>
    rapi.removeLabelDef(config.name).map { b =>
      println(s"RemovedCreated ${config.name}")
      true
    }
  }

  def merge(config: Config): Future[Any] = withRepo(config.repo) { rapi =>
    def doCreateLabel(label: Option[Label], input: LabelInput): Future[String] = {
      label match {
        case Some(l) if (l.color == input.color) =>
          Future(s"Skip create label ${input.name}")
        case Some(l) =>
          rapi.updateLabelDef(input.name, input).map(_ => s"Update label ${input.name}")
        case None =>
          rapi.createLabelDef(input).map(_ => s"Create label ${input.name}")
      }
    }
    val json = JsonMethods.parse(config.file)
    val items = (json match {
      case JArray(list) => list
      case JObject => List(json)
      case _ => throw new IllegalArgumentException("Invalid json file\n" + JsonMethods.pretty(json))
    }).map(v => LabelInput(
      (v \ "name").extract[String],
      (v \ "color").extract[String]
    ))
    rapi.listLabelDefs.flatMap { labels =>
      val ret = items.map { input =>
        doCreateLabel(labels.find(_.name == input.name), input).map { s =>
          println(s)
          s
        }
      }
      Future.sequence(ret)
    }
  }

}

