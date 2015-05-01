package codecheck.github.app

case class CommandSetting(repo: Option[Repo] = None) {
  def repositoryOwner = repo.map(_.owner)
}

case class Repo(owner: String, name: String) 
