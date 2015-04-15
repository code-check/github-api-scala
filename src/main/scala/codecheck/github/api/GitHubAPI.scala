package codecheck.github.api

class GitHubAPI(token: String) {
  def repository(owner: String, repo: String) = new GitHubRepository(this, owner, repo)
}

object GitHubAPI {
  def apply(token: String) = new GitHubAPI(token)
}