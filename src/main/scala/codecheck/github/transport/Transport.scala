package codecheck.github.transport

trait Transport {

  def prepareGet(url: String): Request
  def preparePost(url: String): Request
  def preparePut(url: String): Request
  def prepareDelete(url: String): Request

  def close: Unit
}

