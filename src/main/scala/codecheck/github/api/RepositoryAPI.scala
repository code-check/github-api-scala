package codecheck.github.api

import scala.concurrent.Future
import codecheck.github.models.Label
import codecheck.github.models.LabelInput
import codecheck.github.models.Issue
import codecheck.github.models.IssueInput
import codecheck.github.models.Milestone
import codecheck.github.models.MilestoneInput
import codecheck.github.models.MilestoneListOption
import codecheck.github.models.PullRequest
import codecheck.github.models.PullRequestInput
import codecheck.github.models.PullRequestListOption
import codecheck.github.models.ReviewRequest

case class RepositoryAPI(api: GitHubAPI, owner: String, repo: String) {
  //IssueOp
  def getIssue(number: Long): Future[Option[Issue]] =
    api.getIssue(owner, repo, number)

  def editIssue(number: Long, params: IssueInput): Future[Issue] =
    api.editIssue(owner, repo, number, params)

  def assign(number: Long, assignee: String): Future[Issue] =
    api.assign(owner, repo, number, assignee)

  def unassign(number: Long): Future[Issue] =
    api.unassign(owner, repo, number)

  //LabelOp
  def addLabels(number: Long, labels: String*): Future[List[Label]] =
    api.addLabels(owner, repo, number, labels:_*)

  def replaceLabels(number: Long, labels: String*): Future[List[Label]] =
    api.replaceLabels(owner, repo, number, labels:_*)

  def removeAllLabels(number: Long): Future[List[Label]] =
    api.removeAllLabels(owner, repo, number)

  def removeLabel(number: Long, label: String): Future[List[Label]] =
    api.removeLabel(owner, repo, number, label)

  def listLabels(number: Long): Future[List[Label]] =
    api.listLabels(owner, repo, number)

  def listLabelDefs: Future[List[Label]] =
    api.listLabelDefs(owner, repo)

  def getLabelDef(label: String): Future[Option[Label]] =
    api.getLabelDef(owner, repo, label)

  def createLabelDef(label: LabelInput): Future[Label] =
    api.createLabelDef(owner, repo, label)

  def updateLabelDef(name: String, label: LabelInput): Future[Label] =
    api.updateLabelDef(owner, repo, name, label)

  def removeLabelDef(label: String): Future[Boolean] =
    api.removeLabelDef(owner, repo, label)

  //MilestoneOp
  def listMilestones(option: MilestoneListOption = MilestoneListOption()): Future[List[Milestone]] =
    api.listMilestones(owner, repo, option)

  def getMilestone(number: Int): Future[Option[Milestone]] =
    api.getMilestone(owner, repo, number)

  def createMilestone(input: MilestoneInput): Future[Milestone] =
    api.createMilestone(owner, repo, input)

  def updateMilestone(number: Int, input: MilestoneInput): Future[Milestone] =
    api.updateMilestone(owner, repo, number, input)

  def removeMilestone(number: Int): Future[Boolean] =
    api.removeMilestone(owner, repo, number)

  // PullRequestOp
  def listPullRequests(option: PullRequestListOption = PullRequestListOption()): Future[List[PullRequest]] =
    api.listPullRequests(owner, repo, option)

  def getPullRequest(number: Long): Future[Option[PullRequest]] =
    api.getPullRequest(owner, repo, number)

  def createPullRequest(input: PullRequestInput): Future[PullRequest] =
    api.createPullRequest(owner, repo, input)

  def closePullRequest(number: Long): Future[PullRequest] =
    api.closePullRequest(owner, repo, number)

  def addReviewRequest(number: Long, reviewers: String*): Future[ReviewRequest] =
    api.addReviewRequest(owner, repo, number, reviewers:_*)

  def removeReviewRequest(number: Long, reviewers: String*): Future[Boolean] =
    api.removeReviewRequest(owner, repo, number, reviewers:_*)

}
