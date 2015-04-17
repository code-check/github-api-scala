package codecheck.github.events

import org.json4s.JValue
import codecheck.github.models.AbstractJson

case class DefaultEvent(name: String, value: JValue) extends AbstractJson(value) with GitHubEvent
