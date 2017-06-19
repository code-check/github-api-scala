# GitHubAPI for scala
[![Build Status](https://travis-ci.org/code-check/github-api-scala.svg?branch=master)](https://travis-ci.org/code-check/github-api-scala)
[![Latest version](https://index.scala-lang.org/code-check/github-api-scala/github-api/latest.svg?color=orange)](https://index.scala-lang.org/code-check/github-api-scala)

GitHubAPI wrapper for scala

## Dependencies
- joda-time
- json4s
- async-http-client

## Getting started

To develop this, you have to get GitHub API Token.  
You can get it from [here](https://github.com/settings/applications).

Add this library and an HTTP client library to your `build.sbt` file.
Both versions 1.9 and 2.0 of the Asnyc HTTP Client are supported, so
you choose.  Ning's HTTP client will request a log binding, so we'll
provide a basic one.

```scala
libraryDependencies ++= Seq(
  "com.ning" % "async-http-client" % "1.9.21",
  "org.slf4j" % "slf4j-simple" % "1.7.24",
  "io.code-check" %% "github-api" % "0.2.0"
)
```

Using the code is as simple as starting an HTTP client instance, and
providing it to the main API class.

```scala
import com.ning.http.client.AsyncHttpClient

import codecheck.github.transport.asynchttp19.AsyncHttp19Transport
import codecheck.github.api.GitHubAPI
import codecheck.github.models._

import org.slf4j.LoggerFactory

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Main {

  val logger = LoggerFactory.getLogger(getClass)

  def main(args: Array[String]): Unit = {

    val githubToken = "a0b1c2d3e4f5g6h7ijklmnopqrst5u4v3w2x1y0z"

    val httpClient = new AsyncHttp19Transport(new AsyncHttpClient())
    
    val githubApi = new GitHubAPI(githubToken, httpClient)
    
    val repoParams =
      RepositoryListOption(
        RepositoryListType.public,
        RepositorySort.updated,
        SortDirection.desc
      )
    
    val repoListOp: Future[List[Repository]] =
      githubApi.listOwnRepositories(repoParams)

    val exec: Future[Unit] =
      for (repos <- repoListOp) 
      yield
        for (repo <- repos)
        yield println(repoToJson(repo))

    exec.onFailure {
      case e: Throwable => logger.error(e.toString)
    }

    Await.ready(exec, Duration.Inf)

    httpClient.close
  }

  /** Unsophisticated JSON serialization */
  def repoToJson(repo: Repository): String =
    s"""{
       |  id: ${repo.id},
       |  name: "${repo.name}",
       |  full_name: "${repo.full_name}",
       |  url: "${repo.url}",
       |  description: "${repo.description.getOrElse("")}",
       |  owner: "${repo.owner.login}",
       |  open_issues_count: ${repo.open_issues_count}
       |}""".stripMargin

}
```

## How to develop

``` bash
export GITHUB_USER=[Your GitHub username] 
export GITHUB_REPO=[Your GitHub test repo] 
export GITHUB_TOKEN=[Your GitHub Token] 
git clone -o upstream git@github.com:code-check/github-api.git
cd github-api
sbt test
```

Currently, Java 8 is required to build this library.  If you have
multiple versions of Java installed on your system, set it to Java 8
(also known as version 1.8).  One method for choosing the Java version
is to override the value of `JAVA_HOME` in the environment sbt runs.

```
$ env JAVA_HOME="$(/usr/libexec/java_home -v 1.8)" sbt
```

## About models
We don't aim to define all fields of JSON.
Because these are too much and might be changed by GitHub.

We just define the fields we need.

## License
MIT