package codecheck.github.operations

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import codecheck.github.api.GitHubAPI
import codecheck.github.utils.ToDo
import codecheck.github.models.Repository

trait RepositoryOp {
  self: GitHubAPI =>

  def listOwnRepositories: Future[List[Repository]] = ToDo[Future[List[Repository]]]
  def listOrgRepositories: Future[List[Repository]] = ToDo[Future[List[Repository]]]
  def listUserRepositories: Future[List[Repository]] = ToDo[Future[List[Repository]]]
}
