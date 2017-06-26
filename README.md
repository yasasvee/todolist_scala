[<img src="https://img.shields.io/travis/playframework/play-scala-starter-example.svg"/>](https://travis-ci.org/playframework/play-scala-starter-example)

# Play Scala Starter: TODO List

This is a starter application that shows how Play works.  Please see the documentation at https://www.playframework.com/documentation/latest/Home for more details.

Project summary: Creating simple todo's and writing to reactie mongo DB

## Running

Run this using [sbt](http://www.scala-sbt.org/).  If you downloaded this project from http://www.playframework.com/download then you'll find a prepackaged version of sbt in the project directory:

```
sbt run
```

And then go to http://localhost:9000 to see the running web application.

There are several demonstration files available in this template.

## Controllers

- HomeController.scala:

  Shows how to handle simple HTTP requests, passing on BSON objects to write into reactive mongo DB

## Models

- Task.scala

  Defines a Form object used to create tasks, also helps to write into and read from the DB