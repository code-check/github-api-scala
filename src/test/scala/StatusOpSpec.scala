// package codecheck.github
// package operations

// import models._

import codecheck.github.models.Status
import codecheck.github.models.StatusInput
import codecheck.github.models.StatusState

// import exceptions._

import codecheck.github.exceptions.GitHubAPIException
import codecheck.github.exceptions.NotFoundException

import org.scalatest.FunSpec
import scala.concurrent.Await

class StatusOpSpec extends FunSpec with /*api.*/Constants {

  describe("listStatus(owner, repo, sha)") {

    it("should have zero or more statuses") {
      val result = Await.result(api.listStatus(otherUser, otherUserRepo, otherSha), TIMEOUT)
      result.map { status =>
        assert(StatusState.values.contains(status.state))
      }
    }
  }

  describe("getStatus(owner, repo, sha)") {

    it("should have a status or not") {
      val result = Await.result(api.getStatus(otherUser, otherUserRepo, otherSha), TIMEOUT)
      assert(StatusState.values.contains(result.state))
      assert(result.sha == otherSha)
      assert(result.total_count >= 0L)
      assert(result.statuses.length >= 0L)
      result.statuses.map { status =>
        assert(StatusState.values.contains(status.state))
      }
    }
  }

  describe("createStatus(owner, repo, sha, input)") {

    it("should be pending") {
      val input = StatusInput(StatusState.pending)
      val result = Await.result(api.createStatus(otherUser, otherUserRepo, otherSha, input), TIMEOUT)
      assert(result.state == StatusState.pending)
      assert(result.target_url == None)
      assert(result.description == None)
      assert(result.context == "default")
    }

    it("should be success") {
      val input = StatusInput(StatusState.success, Some("http://"))
      val result = Await.result(api.createStatus(otherUser, otherUserRepo, otherSha, input), TIMEOUT)
      assert(result.state == StatusState.success)
      assert(result.target_url == Some("http://"))
    }

    it("should be error") {
      val input = StatusInput(StatusState.error, description = Some("Description"))
      val result = Await.result(api.createStatus(otherUser, otherUserRepo, otherSha, input), TIMEOUT)
      assert(result.state == StatusState.error)
      assert(result.description == Some("Description"))
    }

    it("should be failure") {
      val input = StatusInput(StatusState.failure, context = Some("context"))
      val result = Await.result(api.createStatus(otherUser, otherUserRepo, otherSha, input), TIMEOUT)
      assert(result.state == StatusState.failure)
      assert(result.context == "context")
    }
  }
}
