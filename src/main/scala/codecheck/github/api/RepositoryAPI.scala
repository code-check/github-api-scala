package codecheck.github.api

import scala.concurrent.Future
import codecheck.github.models.Label
import codecheck.github.models.Issue
import codecheck.github.models.IssueEditParams

case class RepositoryAPI(api: GitHubAPI, owner: String, repo: String) {
  def editIssue(number: Long, params: IssueEditParams): Future[Issue] = 
    api.editIssue(owner, repo, number, params)

  def assign(number: Long, assignee: String): Future[Issue] = 
    api.assign(owner, repo, number, assignee)

  def unassign(number: Long): Future[Issue] = 
    api.unassign(owner, repo, number)

  def addLabels(number: Long, labels: String*): Future[List[Label]] = 
    api.addLabels(owner, repo, number, labels:_*)
  

  def replaceLabels(number: Long, labels: String*): Future[List[Label]] = 
    api.replaceLabels(owner, repo, number, labels:_*)

  def removeAllLabels(number: Long): Future[List[Label]] = 
    api.removeAllLabels(owner, repo, number)

  def removeLabels(number: Long, label: String): Future[List[Label]] = 
    api.removeLabels(owner, repo, number, label)

  def listLabels(number: Long): Future[List[Label]] = 
    api.listLabels(owner, repo, number)

  def listLabelDefs: Future[List[Label]] = 
    api.listLabelDefs(owner, repo)

  def getLabelDef(label: String): Future[Label] = 
    api.getLabelDef(owner, repo, label)

  def createLabelDef(label: Label): Future[Label] = 
    api.createLabelDef(owner, repo, label)

  def updateLabelDef(name: String, label: Label): Future[Label] = 
    api.updateLabelDef(owner, repo, name, label)

  def removeLabelDef(label: String): Future[Boolean] = 
    api.removeLabelDef(owner, repo, label)
}