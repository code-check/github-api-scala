package codecheck.github.app

import java.io.File
import codecheck.github.api.GitHubAPI

class Main(val token: String) extends Command
  with CreateLabels
  with ListLabels
{
  val api = GitHubAPI(token)

  private def routing(command: String, args: List[String]) = {
    command match {
      case "createLabels" =>
        args match {
          case owner :: repo :: filepath :: Nil =>
            createLabels(owner, repo, new File(filepath))
          case _ =>
            println("usage: createLabels [OWNER] [REPO] [FILEPATH]")
        }
      case "listLabels" =>
        args match {
          case owner :: repo :: Nil =>
            listLabels(owner, repo)
          case _ =>
            println("usage: listLabels [OWNER] [REPO]")
        }
      case _ =>
        println("Unknown command: " + command)
    }
  }

}

object Main {

  private def usage = {
    println("You must specify command name.")
  }

  def run(args: String*): Unit = {
    main(args.toArray)
  }

  def main(args: Array[String]): Unit = {
    val apiKey = sys.env.get("GITHUB_TOKEN").orElse {
      println("You must export an environment variable GITHUB_TOKEN.")
      None
    }.map { token =>
      if (args.length == 0) {
        usage
      } else {
        new Main(token).routing(args.head, args.tail.toList)
      }
      0
    }
  }

}