package codecheck.github.app

import java.io.File
import java.util.Base64
import scala.concurrent.ExecutionContext.Implicits.global
import codecheck.github.api.GitHubAPI
import com.ning.http.client.AsyncHttpClient

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

  val appName = "gh-shell"

  private def usage = {
    out("You must specify command name.")
  }

  def run(args: String*): Unit = {
    main(args.toArray)
  }

  private def out(s: String) = println(s)

  private def parseArgs(map: Map[String, String], args: List[String]): Map[String, String] = {
    args match {
      case Nil => map
      case "-u" :: user :: tail =>
        parseArgs(map + ("user" -> user), tail)
      case "-p" :: pass :: tail =>
        parseArgs(map + ("pass" -> pass), tail)
      case str :: tail =>
        out("Unknown argument: " + str)
        sys.exit(-1)
    }
  }

  def main(args: Array[String]): Unit = {
    implicit lazy val client = new AsyncHttpClient()

    val map = parseArgs(Map.empty, args.toList)
    val api = (for (
      user <- map.get("user");
      pass <- map.get("pass")
    ) yield {
      (Base64.getEncoder.encodeToString((user + ":" + pass).getBytes("utf-8")), "Basic")
    }).orElse {
      sys.env.get("GITHUB_TOKEN").map((_, "token"))
    }.map { case (token, tokenType) =>
      val client = new AsyncHttpClient()
      new GitHubAPI(token, client, tokenType)
    }.orElse {
      out("""Usage: 
      |  $appName -u USERNAME -p PASSWORD
      |    or
      |  env GITHUB_TOKEN=YOUR_GITHUB_TOKEN $appName
      """)
      None
    }
    api.foreach { api =>
      api.listOrganizations.map { list =>
        out(list.toString)
        api.close
      }
    }
    /*
    val token = 
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
    */
  }

}