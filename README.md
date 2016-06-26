# neo4akka
Neo4j client using Akka-Http

All existing Scala clients try to be too clever adding obfuscation on top of the communication, creating a limiting DSL, or not using true non-blocking communication.

At its core this client will attempt to be a very clean and representative client allowing full use of Neo4j with Akka-Http in an entirely non-blocking way while still remaining true to the Cypher query language.
