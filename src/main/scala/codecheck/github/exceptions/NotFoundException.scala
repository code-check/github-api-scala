package codecheck.github.exceptions

import org.json4s.JValue

class NotFoundException(body: JValue) extends GitHubAPIException(body)
