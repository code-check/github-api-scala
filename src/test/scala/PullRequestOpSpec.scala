package codecheck.github
package operations

import models._

import org.scalatest.FunSpec
import scala.concurrent.Await
import java.util.Date

class PullRequestOpSpec extends FunSpec with api.Constants {

  describe("listPullRequests") {
    it("with valid repo should succeed") {
      val list = Await.result(api.listPullRequests(otherUser, otherUserRepo), TIMEOUT)
      assert(list.length >= 0)
      assert(list.exists(_.state == IssueState.open))
      assert(list.exists(_.mergeable == None))
      assert(list.exists(_.merged == None))
      assert(list.exists(_.merge_commit_sha.size == shaSize))
      assert(list.exists(_.merged_by == None))
      assert(list.exists(_.comments == None))
      assert(list.exists(_.commits == None))
      assert(list.exists(_.additions == None))
      assert(list.exists(_.deletions == None))
      assert(list.exists(_.changed_files == None))
      assert(list.exists(_.maintainer_can_modify == None))
      assert(list.exists(_.base.repo.full_name == s"$otherUser/$otherUserRepo"))
      assert(list.exists(_.base.user.login == otherUser))
      assert(list.exists(_.base.repo.name == otherUserRepo))
    }
  }

  describe("getPullRequest") {
    it("with open PR should succeed") {
      val pr = Await.result(api.getPullRequest(otherUser, otherUserRepo, 21L), TIMEOUT)
      assert(pr.size >= 0)
      assert(pr.exists(_.state == IssueState.closed))
      assert(pr.exists(_.mergeable == Some(false)))
      assert(pr.exists(_.merge_commit_sha.size == shaSize))
      assert(pr.exists(_.merged_by == None))
      assert(pr.exists(_.comments.exists(_ >= 0)))
      assert(pr.exists(_.commits.exists(_ >= 0)))
      assert(pr.exists(_.additions.exists(_ >= 0)))
      assert(pr.exists(_.deletions.exists(_ >= 0)))
      assert(pr.exists(_.changed_files.exists(_ >= 0)))
      assert(pr.exists(_.maintainer_can_modify == Some(false)))
    }
  }

  describe("createPullRequest(owner, repo, input)") {
    val username = otherUser
    val reponame = otherUserRepo

    it("should success create and close") {
      val title = "Test Pull Request " + new Date().toString()
      val input = PullRequestInput(title, "githubapi-test-pr", "master", Some("PullRequest body"))
      val result = Await.result(api.createPullRequest(username, reponame, input), TIMEOUT)
      assert(result.title == title)
      assert(result.state == IssueState.open)

      val result2 = Await.result(api.closePullRequest(username, reponame, result.number), TIMEOUT)
      assert(result2.title == title)
      assert(result2.state == IssueState.closed)
    }

  }

}
