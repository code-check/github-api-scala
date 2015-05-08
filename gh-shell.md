# gh-shell
Sample application of github-api-scala

It provides shell for GitHub.

## How to run
This app is not released yet.  
So you have to build this with SBT.

To run this, you can use GitHub username and password, or GITHUB_TOKEN

```
$git clone git@github.com:code-check/github-api-scala.git
$cd github-api-scala
$sbt "run -u USERNAME -p PASSWORD"

or 

$export GITHUB_TOKEN=xxxxx
$sbt run
```

## How it works
It works like shell.  
You can control GitHub with this.

``` bash
$sbt "run -u shunjikonishi -p xxxxx"

shunjikonishi>cr code-check test-repo
shunjikonishi@code-check/test-repo>label list
bug             fc2929
duplicate       cccccc
enhancement     84b6eb
help wanted     159818
invalid         e6e6e6
question        cc317c
wontfix         ffffff

shunjikonishi@code-check/test-repo>milestone merge data/milestones.json
Create milestone Sprint1
Create milestone Sprint3
Create milestone Sprint4
Create milestone Sprint2

shunjikonishi@code-check/test-repo>milestone list
1   Sprint1     0/0      2015-04-26
4   Sprint2     0/0      2015-05-10
2   Sprint3     0/0      2015-05-24
3   Sprint4     0/0      2015-06-07
shunjikonishi@code-check/test-repo>milestone rm 1
Removed 1

shunjikonishi@code-check/test-repo>milestone list -v

---------------------------------------------
Sprint2
  number: 4
  due_on: 2015-05-10T00:00:00.000+09:00
  open  : 0
  closed: 0
    
2015/04/27 to 2015/05/10

---------------------------------------------
Sprint3
  number: 2
  due_on: 2015-05-24T00:00:00.000+09:00
  open  : 0
  closed: 0
    
From 2015/05/11 to 2015/05/24

---------------------------------------------
Sprint4
  number: 3
  due_on: 2015-06-07T00:00:00.000+09:00
  open  : 0
  closed: 0
    
2015/05/25 to 2015/06/07
```

## Commands
Currently following commands are implemented

### exit
Exit from app.

### cr
Change repository

```
$ cr code-check test-repo
```

If owner is omitted, the owner of current repository or logined user will be used.

### label
Label control

- list
- add
- update
- rm
- merge(Update multiple labels from file)

### milestone
Milestone control

- list
- add
- update
- rm
- merge(Update multiple milestones from file)

### repo
Repository control

- list

### issue
Issue control

- list
