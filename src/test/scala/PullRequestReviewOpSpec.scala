package codecheck.github
package operations

import models._

import org.scalatest.FunSpec
import scala.concurrent.Await
import java.util.Date

class PullRequestReviewOpSpec extends FunSpec with api.Constants {

  describe("listPullRequestReviews") {
    it("with valid repo should succeed") {
      val list = Await.result(api.listPullRequestReviews(otherUser, otherUserRepo, 47), TIMEOUT)
      assert(list.length >= 0)
      assert(list.exists(_.id >= 0))
      assert(list.exists(_.state == PullRequestReviewState.approved))
      assert(list.exists(_.commit_id.size == shaSize))
    }
  }

  describe("getPullRequestReview") {
    it("with valid repo should succeed") {
      val review = Await.result(api.getPullRequestReview(otherUser, otherUserRepo, 47, 32477105), TIMEOUT)
      assert(review.size >= 0)
      assert(review.exists(_.id >= 0))
      assert(review.exists(_.state == PullRequestReviewState.approved))
      assert(review.exists(_.commit_id.size == shaSize))
    }
  }

  describe("createPullRequestReview(owner, repo, number, input)") {
    val username = otherUser
    val reponame = otherUserRepo

    it("should success create and close") {
      val body = "Test PR review " + new Date().toString()
      val input = PullRequestReviewInput(
        Some(body),
        Some(PullRequestReviewStateInput.REQUEST_CHANGES),
        Seq(
          PullRequestReviewCommentInput(
            "challenge.json",
            1L,
            "Comment body"
          )
        )
      )

      // NOTE: You can only add reviews to PRs that aren't your own
      val result = Await.result(api.createPullRequestReview(username, reponame, 47, input), TIMEOUT)
      assert(result.body == body)
      assert(result.state == PullRequestReviewState.changes_requested)

      // NOTE: You can only dismiss reviews on repos you have rights
      // val result2 = Await.result(api.dismissPullRequestReview(username, reponame, 47, result.id, "githubapi-test-pr-review"), TIMEOUT)
      // assert(result.body == Some(body))
      // assert(result.state == PullRequestReviewState.dismissed)
    }

  }

}
