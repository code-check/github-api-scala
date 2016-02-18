package codecheck.github.exceptions

import org.json4s.JValue

class PermissionDeniedException(body: JValue) extends GitHubAPIException(body)
