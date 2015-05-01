package codecheck.github.app.commands

import scala.concurrent.ExecutionContext.Implicits.global
import codecheck.github.api.GitHubAPI
import codecheck.github.app.Repo

class LabelCommand(api: GitHubAPI, repo: Option[Repo]) {
  def process(commands: List[String]) = {
  }

  /*
  def add(owner: String, repo: String, file: File) = {
    val rapi = api.repositoryAPI(owner, repo)

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
    val json = JsonMethods.parse(file)
    val items = (json match {
      case JArray(list) => list
      case JObject => List(json)
      case _ => throw new IllegalArgumentException(JsonMethods.pretty(json))
    }).map(v => LabelInput(
      (v \ "name").extract[String],
      (v \ "color").extract[String]
    ))
    rapi.listLabelDefs.map { labels =>
      val ret = items.map { input =>
        doCreateLabel(labels.find(_.name == input.name), input).map { s =>
          println(s)
          s
        }
      }
      done(ret)
    }
  }

  def list(owner: String, repo: String) = {
    api.repositoryAPI(owner, repo).listLabelDefs.map(_.map{ l =>
      println(s"${l.name} ${l.color}")
      done
    })
  }
  */
}

object LabelCommand {
  def apply(api: GitHubAPI, repo: Option[Repo]) = new LabelCommand(api, repo)

}
