# neo4akka
Neo4j client using Akka-Http

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

