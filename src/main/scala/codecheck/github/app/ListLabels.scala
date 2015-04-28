package codecheck.github.app

import scala.concurrent.ExecutionContext.Implicits.global

trait ListLabels extends Command {
  def listLabels(owner: String, repo: String) = {
    api.repositoryAPI(owner, repo).listLabelDefs.map(_.map{ l =>
      println(s"${l.name} ${l.color}")
      done
    })
  }
}