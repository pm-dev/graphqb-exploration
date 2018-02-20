package com.framework

import com.framework.models.edge.Edge
import com.framework.models.edge.Relationship
import com.framework.models.node.Node

interface EdgeReadRepository {

    fun <FROM : Node, TO : Node> fetchEdge(
            from: FROM,
            hop: Relationship.Hop.ToOptional<FROM, TO>
    ): Edge<FROM, TO>? =
            fetchEdges(froms = listOf(from), hop = hop).values.single()

    fun <FROM : Node, TO : Node> fetchEdge(
            from: FROM,
            hop: Relationship.Hop.ToSingle<FROM, TO>
    ): Edge<FROM, TO> =
            fetchEdges(froms = listOf(from), hop = hop).values.single() ?:
            throw IllegalStateException("Unable to traverse to a ToSingle relationship")

    fun <FROM : Node, TO : Node> fetchEdges(
            from: FROM,
            hop: Relationship.Hop<FROM, TO>
    ): List<Edge<FROM, TO>> =
            fetchEdges(froms = listOf(from), hop = hop).values.single()

    fun <FROM : Node, TO : Node> fetchEdges(
            froms: Collection<FROM>,
            hop: Relationship.Hop.ToOne<FROM, TO>
    ): Map<FROM, Edge<FROM, TO>?>

    fun <FROM : Node, TO : Node> fetchEdges(
            froms: Collection<FROM>,
            hop: Relationship.Hop<FROM, TO>
    ): Map<FROM, List<Edge<FROM, TO>>>
}
