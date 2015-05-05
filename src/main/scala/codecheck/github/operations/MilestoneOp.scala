package codecheck.github.operations

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import org.json4s.JArray
import org.json4s.JString
import org.json4s.JNothing

import codecheck.github.api.GitHubAPI
import codecheck.github.exceptions.NotFoundException
import codecheck.github.models.Milestone
import codecheck.github.models.MilestoneInput
import codecheck.github.models.MilestoneListOption

trait MilestoneOp {
  self: GitHubAPI =>

  def listMilestones(
    owner: String, 
    repo: String, 
    option: MilestoneListOption = MilestoneListOption()
  ): Future[List[Milestone]] = {
    val path = s"/repos/$owner/$repo/milestones?state=${option.state}&sort=${option.sort}&direction=${option.direction}"
    exec("GET", path).map( 
      _.body match {
        case JArray(arr) => arr.map(v => Milestone(v))
        case _ => throw new IllegalStateException()
      }
    )
  }

  def getMilestone(owner: String, repo: String, number: Int): Future[Option[Milestone]] = {
    val path = s"/repos/$owner/$repo/milestones/$number"
    exec("GET", path, fail404=false).map { res =>
      res.statusCode match {
        case 404 => None
        case 200 => Some(Milestone(res.body))
      }
    }
  }

  def createMilestone(owner: String, repo: String, input: MilestoneInput): Future[Milestone] = {
    val path = s"/repos/$owner/$repo/milestones"
    exec("POST", path, input.value).map(res => Milestone(res.body))
  }

  def updateMilestone(owner: String, repo: String, number: Int, input: MilestoneInput): Future[Milestone] = {
    val path = s"/repos/$owner/$repo/milestones/$number"
    exec("PATCH", path, input.value).map(res => Milestone(res.body))
  }

  def removeMilestone(owner: String, repo: String, number: Int): Future[Boolean] = {
    val path = s"/repos/$owner/$repo/milestones/$number"
    exec("DELETE", path).map(_.statusCode == 204)
  }

}
