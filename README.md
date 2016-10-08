# neo4akka
Neo4j client using Akka-Http

All existing Scala clients try to be too clever adding obfuscation on top of the communication, creating a limiting DSL, or not using true non-blocking communication.

At its core this client will attempt to be a very clean and representative client allowing full use of Neo4j with Akka-Http in an entirely non-blocking way while still remaining true to the Cypher query language.

## Status

Still very early development, but it is usable, albeit somewhat less useful than the ultimate intent. However, since we currently
have the capacity to validate interpolated cypher queries at compile-time, it's already a leg-up on every other Scala Neo4j framework
that exists.

## Features

* [X] Asynchronous, true non-blocking IO with Akka HTTP
* [X] Cypher String Interpolator
* [X] Cypher Query Parser
* [X] Cypher Query Parser via Macro on Interpolation at compile-time
* [X] Structured QueryResponse
* [ ] Macro support for extracting case class from QueryResponse
* [ ] Cypher Query Parser generates mapping to case classes for true compile-time query and result validation and mapping

## Documentation

In desperate need of help. For now just look at the tests until we can get the first release finished.