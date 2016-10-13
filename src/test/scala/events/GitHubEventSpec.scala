package codecheck.github
package events

import org.scalatest.FunSpec
import org.scalatest.Inside
import org.scalatest.Matchers

class GitHubEventSpec extends FunSpec with Matchers with Inside
    with IssueEventJson
    with PullRequestEventJson {

  describe("GitHubEvent(issue, JValue)") {
    val event = GitHubEvent("issue", issueEventJson)

    it("should yield IssueEvent") {
      event shouldBe a [IssueEvent]
    }
    it("should have a name") {
      inside(event) {
        case e @ IssueEvent(name, _) =>
          assert(name === "issue")
      }
    }
    it("should have an action") {
      inside(event) {
        case e @ IssueEvent(_, _) =>
          assert(e.action === models.IssueAction.opened)
      }
    }
    it("should have an issue") {
      inside(event) {
        case e @ IssueEvent(_, _) =>
          e.issue shouldBe a [models.Issue]
      }
    }
    describe("Issue") {
      inside(event) {
        case e @ IssueEvent(_, _) => {
          val issue = e.issue
          it("should have a number") {
            assert(issue.number === 2l)
          }
          it("should have a title") {
            assert(issue.title === "Spelling error in the README file")
          }
          it("should have a state") {
            assert(issue.state === "open")
          }
          it("should have a body") {
            val exp = "It looks like you accidently spelled 'commit' with two 't's."
            assert(issue.body === Some(exp))
          }
        }
      }
    }
  }

  describe("GitHubEvent(pull_request, JValue)") {
    val event = GitHubEvent("pull_request", pullRequestEventJson)

    it("should yield PullRequestEvent") {
      event shouldBe a [PullRequestEvent]
    }
    it("should have a name") {
      inside(event) {
        case e @ PullRequestEvent(name, _) =>
          assert(name === "pull_request")
      }
    }
    it("should have a number") {
      inside(event) {
        case e @ PullRequestEvent(_, _) =>
          assert(e.number === 1l)
      }
    }
    it("should have an action") {
      inside(event) {
        case e @ PullRequestEvent(_, _) =>
          assert(e.action === models.PullRequestAction.opened)
      }
    }
    it("should have a pull request") {
      inside(event) {
        case e @ PullRequestEvent(_, _) =>
          e.pull_request shouldBe a [models.PullRequest]
      }
    }
    describe("PullRequest") {
      inside(event) {
        case e @ PullRequestEvent(_, _) => {
          val pr = e.pull_request
          it("should have a number") {
            assert(pr.number === 1l)
          }
          it("should have a title") {
            assert(pr.title === "Update the README with new information")
          }
          it("should have a state") {
            assert(pr.state === "open")
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
}
