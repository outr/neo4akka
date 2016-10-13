# neo4akka

[![Build Status](https://travis-ci.org/outr/neo4akka.svg?branch=master)](https://travis-ci.org/outr/neo4akka)
[![Stories in Ready](https://badge.waffle.io/outr/neo4akka.png?label=ready&title=Ready)](https://waffle.io/outr/neo4akka)
[![Gitter](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/outr/neo4akka)
[![Maven Central](https://img.shields.io/maven-central/v/com.outr/neo4akka_2.11.svg)](https://maven-badges.herokuapp.com/maven-central/com.outr/neo4akka_2.11)

Neo4j client for Scala using Akka-Http

All existing Scala clients try to be too clever adding obfuscation on top of the communication, creating a limiting DSL, or not using true non-blocking communication.

At its core this client will attempt to be a very clean and representative client allowing full use of Neo4j with Akka-Http in an entirely non-blocking way while still remaining true to the Cypher query language.

## Status

Fully functional API that supports the majority of use-cases but ongoing development is still happening to make it more
useful and convenient.

## Features for 1.0.0

* [X] Asynchronous, true non-blocking IO with Akka HTTP
* [X] Cypher String Interpolator
* [X] Cypher Query Parser
* [X] Cypher Query Parser via Macro on Interpolation at compile-time
* [X] Structured QueryResponse
* [X] Macro support for extracting case class from QueryResponse

## Features for 1.1.0
* [ ] Insert and update from case class into database
* [ ] Transactions
* [ ] Practical Pagination support

## Documentation

In desperate need of help. For now just look at the tests until we can get the first release finished.

## Setup

neo4akka is published to Sonatype OSS and Maven Central and supports JVM with 2.11 and 2.12:

```
libraryDependencies += "com.outr" %% "neo4akka" % "1.0.0"
```

## Using

### Imports

The only import necessary to use neo4akka is the following import along with an execution context for futures:

```scala
import com.outr.neo4akka._

import scala.concurrent.ExecutionContext.Implicits.global
```

### Creating a Session

Supply the host, port, and credentials to validate and establish a session with neo4j:

```scala
val sessionFuture[Future[Neo4Akka]] = Neo4Akka("localhost", 7474, "neo4j", "password")
```

### Creating a Query

With neo4akka's powerful Cypher language interpolation (uses neo4j's built-in query validation) we can create compile-time
validated Cypher queries with ease:

```scala
val name = "Tom Hanks"
val query = cypher"MATCH (p: Person {name: $name}) RETURN p"
```

If the query is malformed compilation will fail and injected arguments are properly assigned to the argument list for sending
to the database as unique arguments.

### Executing a Query

Since our `Neo4Akka` instance may or may not have completed yet, we can `flatMap` the `Future` in order to execute the
query against the database and get back a `Future[ResultSet]`. For the purposes of this example, we'll simply do a blocking
`Await` to get the `ResultSet`:

```scala
val resultSetFuture[Future[ResultSet]] = sessionFuture.flatMap(session => session(query))
val resultSet: ResultSet = Await.result(request, Duration.Inf)
```

### Processing Results

Now that we have a `ResultSet` we can access the return `p` from our query by name and use a compile-time Macro to populate
a case class `Person` with the values:

```scala
case class Person(name: String, born: Int)

val people: Vector[Person] = resultSet("p")[Person]
```

### Disposing

Because neo4akka uses Akka Actors internally, it must be properly disposed to release the `ActorSystem` and `ActorMaterializer`.

Note: An instance of `Neo4Akka` uses Akka HTTP for true asynchronous non-blocking IO, so there is no state or unsafe reference
information stored in the `session`.

```scala
sessionFuture.map(_.dispose())
```