package codecheck.github.app

import java.util.Base64
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import codecheck.github.api.GitHubAPI
import codecheck.github.exceptions.UnauthorizedException
import com.ning.http.client.AsyncHttpClient
import scopt.OptionParser

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
      try {
        api.user
        new CommandRunner(api).run
        0
      } catch {
        case e: UnauthorizedException =>
          api.close
          println("Unauthorized user")
          -1
      }
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