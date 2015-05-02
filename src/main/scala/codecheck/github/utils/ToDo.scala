package codecheck.github.utils

trait ToDo {
  throw new UnsupportedOperationException()
}

object ToDo  {
  def apply[T]: T = {
    throw new UnsupportedOperationException()
  }
}