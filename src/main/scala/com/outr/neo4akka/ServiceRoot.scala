package com.outr.neo4akka

case class ServiceRoot(node: String,
                       relationship: String,
                       node_index: String,
                       relationship_index: String,
                       extensions_info: String,
                       relationship_types: String,
                       batch: String,
                       cypher: String,
                       indexes: String,
                       constraints: String,
                       transaction: String,
                       node_labels: String,
                       neo4j_version: String)
