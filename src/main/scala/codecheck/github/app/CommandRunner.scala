package codecheck.github.app

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import codecheck.github.api.GitHubAPI
import codecheck.github.app.commands._

class CommandRunner(api: GitHubAPI) {
  var commands: Map[String, Command] = Map(
    "cr" -> new ChangeRepositoryCommand(api),
    "label" -> new LabelCommand(api),
    "milestone" -> new MilestoneCommand(api),
    "repo" -> new RepositoryCommand(api),
    "issue" -> new IssueCommand(api)
  )

  var setting = CommandSetting()

  def split(s: String): List[String] = {
    val r = "[^\\s\"']+|\"[^\"]*\"|'[^']*'".r
    r.findAllMatchIn(s).toList.map { m => 
      val str = m.toString
      if (str.startsWith("\"") && str.endsWith("\"")) {
        str.substring(1, str.length - 1)
      } else {
        str
      }
    }
  }
  def prompt = {
    val repo = setting.repo.map(v => "@" + v.owner + "/" + v.name).getOrElse("")
    print(api.user.login + repo + ">")
  }

  def help = {
    println("Avaiable commands:")
    commands.keys.toList.sorted.foreach(key => println(s"  - $key"))
  }

  def process(line: String) = {
    try {
      val args = split(line)
      val ret = args match {
        case Nil =>
          Future(setting)
        case "help" :: Nil =>
          help
          Future(setting)
        case "help" :: cmd :: Nil =>
          commands.get(cmd).map(_.run(setting, "help" :: Nil)).getOrElse {
            println("Unknown command: " + cmd)
            Future(setting)
          }
        case cmd :: tail =>
          commands.get(cmd).map(_.run(setting, tail)).getOrElse {
            println("Unknown command: " + cmd)
            Future(setting)
          }
      }
      ret.map { s =>
        setting = s
        prompt
      }.failed.map { e =>
        println(e.getMessage)
        prompt
      }
    } catch {
      case e: Exception =>
        println(e.toString)
        prompt      
    }
  }

  def run = {
    prompt
    Iterator.continually(Console.in.readLine).takeWhile { s =>
      val end = s == null || s.trim == "exit"
      if (end) {
        api.close
        println("Bye!")
      }
      !end
    }.foreach { line =>
      Option(line).map(process(_))
    }
  }
}
