package codecheck.github.utils

case class PrintList(headers: String*) {
  def format(lens: Seq[Int], row: Seq[Any]): Unit = {
    lens.zip(row).foreach { case (n, s) =>
      print(s)
      print(" " * (n - s.toString.length))
    }
    println
  }

  def build(items: Seq[Seq[Any]]) = {
    if (items.size == 0) {
      println("No items")
    } else {
      val lens = items.foldLeft(
        headers.map(_.length)
      ) { (ret, row) =>
        ret.zip(row).map { case (n, s) => 
          Math.max(n, s.toString.length)
        }
      }.map(_ + 2)

      format(lens, headers)
      println("-" * lens.sum)
      items.foreach(row => format(lens,row))
   }
  }
}