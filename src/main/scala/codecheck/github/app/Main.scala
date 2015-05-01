package codecheck.github.app

import java.io.File
import java.util.Base64
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import codecheck.github.api.GitHubAPI
import codecheck.github.app.commands._
import com.ning.http.client.AsyncHttpClient
import scopt.OptionParser

class Main(api: GitHubAPI) {
  import codecheck.github.utils.Json4s.formats

  var repo: Option[Repo] = None

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
    val str = repo.map(v => v.owner + "/" + v.repo).getOrElse("") + ">"
    print(str)
  }

  def process(line: String) = {
    val commands = split(line)
    val ret = commands match {
      case Nil =>
        Future(true)
      case "label" :: tail =>
        LabelCommand(api, repo).process(tail)
        Future(true)
      case "cr" :: o :: r :: Nil =>
        repo = Some(Repo(o, r))
        Future(true)
      case "cr" :: str :: Nil if (str.split("/").length == 2) =>
        val strs = str.split("/")
        repo = Some(Repo(strs(0), strs(1)))
        Future(true)
      case str :: tail =>
        println("Unknown command: " + str)
        Future(true)
    }
    ret.map { b =>
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

object Main {

  case class Config(user: String = "", pass: String = "") {
    def userToken: Option[(String, String)] = {
      if (user.length > 0 && pass.length > 0) {
        val encoded = Base64.getEncoder.encodeToString((user + ":" + pass).getBytes("utf-8"))
        Some((encoded, "Basic"))
      } else {
        None
      }
    }
  }

  val appName = "gh-shell"
  val parser = new OptionParser[Config](appName) {
    head(appName, "0.1.0")
    opt[String]('u', "user") action { (x, c) =>
      c.copy(user=x) 
    } text("username for GitHub")
    opt[String]('p', "password") action { (x, c) =>
      c.copy(pass=x) 
    } text("password")
    note(s"""
      |Shell for GitHub
      |
      |#Use with login
      |
      |  $appName -u USERNAME - p PASSWORD
      |
      |#Use with GITHUB_TOKEN which set in environment variable
      |
      |  env GITHUB_TOKEN=YOUR_GITHUB_TOKEN $appName
    """.stripMargin)
  }

  def run(config: Config) = {
    config.userToken.orElse {
      sys.env.get("GITHUB_TOKEN").map(s => (s, "token"))
    }.map { case (token, tokenType) =>
      val client = new AsyncHttpClient()
      val api = new GitHubAPI(token, client, tokenType)
      new Main(api).run
      0
    }.getOrElse {
      parser.showUsage
      -1
    }
  }

  def main(args: Array[String]): Unit = {
    parser.parse(args, Config()) match {
      case Some(config) => run(config)
      case None =>
    }
  }

}