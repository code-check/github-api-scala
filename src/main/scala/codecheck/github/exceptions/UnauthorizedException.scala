package codecheck.github.exceptions

import org.json4s.JValue

class UnauthorizedException(body: JValue) extends GitHubAPIException(body)
