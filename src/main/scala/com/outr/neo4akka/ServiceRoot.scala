package com.outr.neo4akka

case class ServiceRoot(node: String,
                       relationship: String,
                       nodeIndex: String,
                       relationshipIndex: String,
                       extensionsInfo: String,
                       relationshipTypes: String,
                       batch: String,
                       cypher: String,
                       indexes: String,
                       constraints: String,
                       transaction: String,
                       nodeLabels: String,
                       neo4jVersion: String)
