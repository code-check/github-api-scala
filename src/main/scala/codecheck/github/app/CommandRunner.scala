package codecheck.github.app

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import codecheck.github.api.GitHubAPI
import codecheck.github.app.commands._

class CommandRunner(api: GitHubAPI) {
  var commands: Map[String, Command] = Map(
    "cr" -> new ChangeRepositoryCommand(api),
    "label" -> new LabelCommand(api)
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

  def process(line: String) = {
    try {
      val args = split(line)
      val ret = args match {
        case Nil =>
          Future(setting)
        case str :: tail =>
          commands.get(str).map(_.run(setting, tail)).getOrElse {
            println("Unknown command: " + str)
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
    Iterator.continually(scala.io.StdIn.readLine).takeWhile { s =>
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
