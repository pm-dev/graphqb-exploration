package com.framework

import com.framework.models.edge.Edge
import com.framework.models.node.Node

interface EdgeWriteRepository {

    fun <FROM: Node, TO: Node> save(
            edge: Edge<FROM, TO>
    ): Edge<FROM, TO>
            = saveEdges(edges = listOf(edge)).single()

    fun <FROM: Node, TO: Node> save(
            vararg edges: Edge<FROM, TO>
    ): List<Edge<FROM, TO>>
            = saveEdges(edges = edges.asIterable())

    fun <FROM: Node, TO: Node> saveEdges(
            edges: Iterable<Edge<FROM, TO>>
    ): List<Edge<FROM, TO>>
}
