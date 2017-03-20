# GitHubAPI for scala
[![Build Status](https://travis-ci.org/code-check/github-api-scala.svg?branch=master)](https://travis-ci.org/code-check/github-api-scala)
GitHubAPI wrapper for scala

## Dependencies
- joda-time
- json4s
- async-http-client

## How to develop
To develop this, you have to get GitHub API Token.  
You can get it from [here](https://github.com/settings/applications).

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