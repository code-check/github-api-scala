package codecheck.github
package events

import org.scalatest.FunSpec
import org.scalatest.Inside
import org.scalatest.Matchers

class GitHubEventSpec extends FunSpec with Matchers with Inside
    with IssueEventJson
    with PullRequestEventJson
    with PullRequestReviewEventJson
    with PushEventJson {

  describe("GitHubEvent(issue, JValue)") {
    val event = GitHubEvent("issues", issueEventJson)

    it("should yield IssueEvent") {
      event shouldBe a [IssueEvent]
    }
    describe("IssueEvent") {
      inside(event) {
        case e @ IssueEvent(name, _) =>
          it("should have a name") {
            assert(name === "issues")
          }
          it("should have an action") {
            assert(e.action === models.IssueAction.opened)
          }
          it("should have an issue") {
            e.issue shouldBe a [models.Issue]
          }
          describe("Issue") {
            val issue = e.issue
            it("should have a number") {
              assert(issue.number === 2L)
            }
            it("should have a title") {
              assert(issue.title === "Spelling error in the README file")
            }
            it("should have a state") {
              assert(issue.state === models.IssueState.open)
            }
            it("should have a body") {
              val exp = ""
              assert(issue.body === exp)
            }
          }
      }
    }
  }

  describe("GitHubEvent(push, JValue)") {
    val event = GitHubEvent("push", pushEventJson)

    it("should yield PushEvent") {
      event shouldBe a [PushEvent]
    }
    describe("Push") {
      inside(event) {
        case e @ PushEvent(name, _) =>
          it("should have a name") {
            assert(name === "push")
          }
          it("should have a ref") {
            assert(e.ref === "refs/heads/changes")
          }
          it("should have a base ref") {
            assert(e.base_ref === None)
          }
          it("should have a before") {
            assert(e.before === "9049f1265b7d61be4a8904a9a27120d2064dab3b")
          }
          it("should have an after") {
            assert(e.after === "0d1a26e67d8f5eaf1f6ba5c57fc3c7d91ac0fd1c")
          }
          it("should have a head commit") {
            e.head_commit shouldBe a [PushCommit]
          }
          describe("PushCommit") {
            val commit = e.head_commit
            it("should have a id") {
              assert(commit.id === "0d1a26e67d8f5eaf1f6ba5c57fc3c7d91ac0fd1c")
            }
            it("should have a message") {
              assert(commit.message === "Update README.md")
            }
            it("should have a timestamp") {
              assert(commit.timestamp === "2015-05-05T19:40:15-04:00")
            }
            it("should have a tree_id") {
              assert(commit.tree_id === "f9d2a07e9488b91af2641b26b9407fe22a451433")
            }
            it("should have a comitter") {
              commit.committer shouldBe a [models.User]
            }
          }
          it("should have a repository") {
            e.repository shouldBe a [models.Repository]
          }
          describe("Repository") {
            val repo = e.repository
            it("should have an id") {
              assert(repo.id === 35129377)
            }
            it("should have a name") {
              assert(repo.name === "public-repo")
            }
            it("should have a full_name") {
              assert(repo.full_name === "baxterthehacker/public-repo")
            }
            it("should have a owner") {
              repo.owner shouldBe a [models.User]
            }
          }
          it("should have a pusher") {
            e.pusher shouldBe a [models.User]
          }
          it("should have a sender") {
            e.sender shouldBe a [models.User]
          }
      }
    }
  }

  describe("GitHubEvent(pull_request, JValue)") {
    val event = GitHubEvent("pull_request", pullRequestEventJson)

    it("should yield PullRequestEvent") {
      event shouldBe a [PullRequestEvent]
    }
    describe("PullRequest") {
      inside(event) {
        case e @ PullRequestEvent(name, _) =>
          it("should have a name") {
            assert(name === "pull_request")
          }
          it("should have a number") {
            assert(e.number === 1L)
          }
          it("should have an action") {
            assert(e.action === models.PullRequestAction.opened)
          }
          it("should have a pull request") {
            e.pull_request shouldBe a [models.PullRequest]
          }
          describe("PullRequest") {
            val pr = e.pull_request
            it("should have a number") {
              assert(pr.number === 1L)
            }
            it("should have a title") {
              assert(pr.title === "Update the README with new information")
            }
            it("should have a state") {
              assert(pr.state === models.IssueState.open)
            }
            it("should have a body") {
              val exp = "This is a pretty simple change that we need to pull into master."
              assert(pr.body === exp)
            }
            it("should have a head") {
              pr.head shouldBe a [models.PullRequestRef]
            }
            describe("PullRequestRef") {
              val head = pr.head
              it("should have a label") {
                assert(head.label === "baxterthehacker:changes")
              }
              it("should have a ref") {
                assert(head.ref === "changes")
              }
              it("should have a sha") {
                assert(head.sha === "0d1a26e67d8f5eaf1f6ba5c57fc3c7d91ac0fd1c")
              }
              it("should have a user") {
                head.user shouldBe a [models.User]
              }
              it("should have a repo") {
                head.repo shouldBe a [models.Repository]
              }
            }
            it("should have a base") {
              pr.base shouldBe a [models.PullRequestRef]
            }
          }
      }
    }
  }

  describe("GitHubEvent(pull_request_review, JValue)") {
    val event = GitHubEvent("pull_request_review", pullRequestReviewEventJson)

    it("should yield PullRequestReviewEvent") {
      event shouldBe a [PullRequestReviewEvent]
    }
    describe("PullRequestReviewEvent") {
      inside(event) {
        case e @ PullRequestReviewEvent(name, _) =>
          it("should have a name") {
            assert(name === "pull_request_review")
          }
          it("should have an action") {
            assert(e.action === models.PullRequestReviewAction.submitted)
          }
          it("should have a review") {
            e.review shouldBe a [models.PullRequestReview]
          }
          describe("PullRequestReview") {
            val review = e.review
            it("should have an id") {
              assert(review.id === 2626884L)
            }
            it("should have a state") {
              assert(review.state === models.PullRequestReviewState.approved)
            }
            it("should have a body") {
              val exp = "Looks great!"
              assert(review.body === exp)
            }
          }
          it("should have a pull request") {
            e.pull_request shouldBe a [models.PullRequest]
          }
          describe("PullRequest") {
            val pr = e.pull_request
            it("should have a number") {
              assert(pr.number === 8L)
            }
            it("should have a title") {
              assert(pr.title === "Add a README description")
            }
            it("should have a state") {
              assert(pr.state === models.IssueState.open)
            }
            it("should have a body") {
              val exp = "Just a few more details"
              assert(pr.body === exp)
            }
            it("should have a head") {
              pr.head shouldBe a [models.PullRequestRef]
            }
            describe("PullRequestRef") {
              val head = pr.head
              it("should have a label") {
                assert(head.label === "skalnik:patch-2")
              }
              it("should have a ref") {
                assert(head.ref === "patch-2")
              }
              it("should have a sha") {
                assert(head.sha === "b7a1f9c27caa4e03c14a88feb56e2d4f7500aa63")
              }
              it("should have a user") {
                head.user shouldBe a [models.User]
              }
              it("should have a repo") {
                head.repo shouldBe a [models.Repository]
              }
            }
            it("should have a base") {
              pr.base shouldBe a [models.PullRequestRef]
            }
          }
          it("should have a repository") {
            e.repository shouldBe a [models.Repository]
          }
          describe("Repository") {
            val repo = e.repository
            it("should have an id") {
              assert(repo.id === 35129377L)
            }
            it("should have a name") {
              assert(repo.name === "public-repo")
            }
            it("should have a full_name") {
              assert(repo.full_name === "baxterthehacker/public-repo")
            }
            it("should have a url") {
              assert(repo.url === "https://api.github.com/repos/baxterthehacker/public-repo")
            }
          }
          it("should have a sender") {
            e.sender shouldBe a [models.User]
          }
          describe("User") {
            val user = e.sender
            it("should have an id") {
              assert(user.id === 6752317L)
            }
            it("should have a login") {
              assert(user.login === "baxterthehacker")
            }
            it("should have a name") {
              assert(user.name === None)
            }
          }
      }
    }
  }
}
