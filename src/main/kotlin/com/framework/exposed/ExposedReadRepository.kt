package com.framework.exposed

import com.framework.EdgeReadRepository
import com.framework.NodeReadRepository
import com.framework.exposed.tables.Nodes
import com.framework.exposed.tables.edgeTable
import com.framework.models.NodeAsJson
import com.framework.models.NodeType
import com.framework.models.PrimaryKey
import com.framework.models.edge.Edge
import com.framework.models.edge.Relationship
import com.framework.models.edge.Traversal
import com.framework.models.node.Node
import com.framework.models.node.NodeDBFilter
import com.framework.optional
import com.google.common.collect.ImmutableMultimap
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.isNull
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.time.Instant


interface ExposedReadRepository : NodeReadRepository, EdgeReadRepository {

    fun <TYPE : Node> NodeAsJson.fromJson(type: NodeType): TYPE

    fun <TYPE : Node> TYPE.toJson(): NodeAsJson


    /**
     * Fetching edges
     */

    override fun <FROM : Node, TO : Node> fetchEdges(
            froms: Collection<FROM>,
            hop: Relationship.Hop.ToOne<FROM, TO>
    ): Map<FROM, Edge<FROM, TO>?> =
            fetchEdgesInternal(froms, hop)
                    .mapValues { it.value.optional() }

    override fun <FROM : Node, TO : Node> fetchEdges(
            froms: Collection<FROM>,
            hop: Relationship.Hop<FROM, TO>
    ): Map<FROM, List<Edge<FROM, TO>>> =
            fetchEdgesInternal(froms, hop)

    /**
     * Traversing edges from nodes to nodes
     */
    override fun <FROM : Node, TO : Node> traverse(
            traversal: Traversal.MultiBoundToOne<FROM, TO>,
            where: NodeDBFilter?
    ): Map<FROM, TO?> =
            traverseInternal(traversal, where)
                    .mapValues { it.value.optional() }

    override fun <FROM : Node, TO : Node> traverse(
            traversal: Traversal.MultiBoundToSingle<FROM, TO>,
            where: NodeDBFilter?
    ): Map<FROM, TO> =
            traverseInternal(traversal, where)
                    .mapValues { it.value.single() }

    override fun <FROM : Node, TO : Node> traverse(
            traversal: Traversal.MultiBoundToMany<FROM, TO>,
            where: NodeDBFilter?
    ): Map<FROM, List<TO>> =
            traverseInternal(traversal, where)

    /**
     * Fetching nodes
     */
    @Transactional(propagation = Propagation.MANDATORY)
    override fun <TYPE : Node> select(where: NodeDBFilter?): List<TYPE> {
        val resultRows = where?.toBooleanOp()?.let { Nodes.select { it } } ?: Nodes.selectAll()
        return resultRows.map { it.toNode<TYPE>() }
    }

    /**
     * Private
     */
    private fun <FROM : Node, TO : Node> fetchEdgesInternal(
            froms: Collection<FROM>,
            hop: Relationship.Hop<FROM, TO>
    ): Map<FROM, List<Edge<FROM, TO>>> {
        val edgeTable = hop.edgeTable
        val edgeRows = selectEdgeRows(froms = froms.mapNotNull { it.pk }, hop = hop)
        val toPks = edgeRows.flatMap { entry ->
            entry.value.map { row ->
                when (entry.key) {
                    row[edgeTable.from] -> row[edgeTable.to]
                    row[edgeTable.to] -> row[edgeTable.from]
                    else -> throw IllegalStateException()
                }
            }
        }
        val tos = select<TO>(where = NodeDBFilter(pks = toPks)).associateBy { it.pk!! }
        return froms.associateBy({ it }, { from ->
            val rows = edgeRows[from.pk] ?: listOf()
            rows.map { row ->
                val toPk = when (from.pk) {
                    row[edgeTable.from] -> row[edgeTable.to]
                    row[edgeTable.to] -> row[edgeTable.from]
                    else -> throw IllegalStateException()
                }
                Edge(
                        pk = row[edgeTable.pk],
                        from = from,
                        relationship = hop,
                        to = tos[toPk]!!,
                        createdAt = row[edgeTable.createdAt].let { Instant.ofEpochSecond(it) },
                        deletedAt = row[edgeTable.deletedAt]?.let { Instant.ofEpochSecond(it) })
            }
        })
    }

    @Transactional(propagation = Propagation.MANDATORY)
    private fun selectEdgeRows(
            froms: Collection<PrimaryKey>,
            hop: Relationship.Hop<*, *>
    ): Map<PrimaryKey, Iterable<ResultRow>> {
        if (froms.isEmpty()) {
            return froms.associateBy({ it }, { emptyList<ResultRow>() })
        }
        val edgeTable = hop.edgeTable
        val pks = froms.toSet()
        val results = when (hop.direction) {

            Relationship.Hop.Direction.FORWARD -> edgeTable.select {
                (edgeTable.from inList pks) and (edgeTable.name eq hop.name) and edgeTable.deletedAt.isNull()
            }.fold(ImmutableMultimap.builder<PrimaryKey, ResultRow>()) { map, row ->
                        map.put(row[edgeTable.from], row)
                    }

            Relationship.Hop.Direction.BACKWARD -> edgeTable.select {
                (edgeTable.to inList pks) and (edgeTable.name eq hop.name) and edgeTable.deletedAt.isNull()
            }.fold(ImmutableMultimap.builder<PrimaryKey, ResultRow>()) { map, row ->
                        map.put(row[edgeTable.to], row)
                    }

            null /* Symmetric */ -> edgeTable.select {
                ((edgeTable.from inList pks) or (edgeTable.to inList pks)) and (edgeTable.name eq hop.name) and edgeTable.deletedAt.isNull()
            }.fold(ImmutableMultimap.builder<PrimaryKey, ResultRow>()) { map, row ->
                        map.put(row[edgeTable.to], row)
                        map.put(row[edgeTable.from], row)
                        map
                    }
        }.build()
        return froms.associateBy({ it }, { results[it] })
    }

    private fun <FROM : Node, TO : Node> traverseInternal(
            traversal: Traversal.MultiBound<FROM, TO>,
            where: NodeDBFilter?
    ): Map<FROM, List<TO>> =
            traversal.froms.associateBy({ it }) { from ->
                traverseInternal(
                        from = from,
                        relationship = traversal.relationship,
                        where = where)
            }

    @Transactional(propagation = Propagation.MANDATORY)
    private fun <FROM : Node, TO : Node> traverseInternal(
            from: FROM,
            relationship: Relationship<FROM, TO>,
            where: NodeDBFilter? = null
    ): List<TO> {
        var joinedColumns: ColumnSet = Nodes // All the joined columns
        var previousSourceColumns = listOf<Column<PrimaryKey>>() // Used to prevent symmetric edges from traversing backward
        var nextSourceColumns = listOf(Nodes.pk) // The columns to join on
        relationship.hops.forEachIndexed { idx, hop ->
            val hopTable = hop.edgeTable.alias("edges$idx")
            val constraints = when (hop.direction) {
                Relationship.Hop.Direction.FORWARD -> {
                    val columnsToExclude = previousSourceColumns
                    previousSourceColumns = nextSourceColumns
                    nextSourceColumns = listOf(hopTable[hop.edgeTable.to])
                    previousSourceColumns.map { sourceColumn ->
                        val constraint = sourceColumn eq hopTable[hop.edgeTable.from]
                        if (columnsToExclude.isNotEmpty()) {
                            constraint and columnsToExclude.map { NeqOp(it, sourceColumn) as Op<Boolean> }.reduce { a, b -> a and b }
                        } else {
                            constraint
                        }
                    }
                }
                Relationship.Hop.Direction.BACKWARD -> {
                    val columnsToExclude = previousSourceColumns
                    previousSourceColumns = nextSourceColumns
                    nextSourceColumns = listOf(hopTable[hop.edgeTable.from])
                    previousSourceColumns.map { sourceColumn ->
                        val constraint = sourceColumn eq hopTable[hop.edgeTable.to]
                        if (columnsToExclude.isNotEmpty()) {
                            constraint and columnsToExclude.map { NeqOp(it, sourceColumn) as Op<Boolean> }.reduce { a, b -> a and b }
                        } else {
                            constraint
                        }
                    }
                }
                null -> {// SYMMETRIC
                    val columnsToExclude = previousSourceColumns
                    previousSourceColumns = nextSourceColumns
                    nextSourceColumns = listOf(hopTable[hop.edgeTable.to], hopTable[hop.edgeTable.from])
                    previousSourceColumns.map { sourceColumn ->
                        val forward = sourceColumn eq hopTable[hop.edgeTable.from]
                        val backward = sourceColumn eq hopTable[hop.edgeTable.to]
                        if (columnsToExclude.isNotEmpty()) {
                            (forward and columnsToExclude.map { NeqOp(it, sourceColumn) as Op<Boolean> }.reduce { a, b -> a and b }) or
                                    (backward and columnsToExclude.map { NeqOp(it, sourceColumn) as Op<Boolean> }.reduce { a, b -> a and b })
                        } else {
                            forward or backward
                        }
                    }
                }
            }.reduce { a, b -> a or b } and (hopTable[hop.edgeTable.name] eq hop.name) and hopTable[hop.edgeTable.deletedAt].isNull()
            joinedColumns = joinedColumns.join(
                    otherTable = hopTable,
                    joinType = JoinType.INNER,
                    additionalConstraint = { constraints })
        }
        val resultNode = Nodes.alias("resultNode")
        var finalConstraints = nextSourceColumns.map { sourceColumn ->
            val constraint = sourceColumn eq resultNode[Nodes.pk]
            if (previousSourceColumns.isNotEmpty()) {
                constraint and previousSourceColumns.map { NeqOp(it, sourceColumn) as Op<Boolean> }.reduce { a, b -> a and b }
            } else {
                constraint
            }
        }.reduce { a, b -> a or b }
        finalConstraints = where?.toBooleanOp(resultNode)?.let { finalConstraints and it } ?: finalConstraints
        joinedColumns = joinedColumns.join(
                otherTable = resultNode,
                joinType = JoinType.INNER,
                additionalConstraint = { finalConstraints })
        return joinedColumns
                .slice(
                        resultNode[Nodes.pk],
                        resultNode[Nodes.type],
                        resultNode[Nodes.attributes],
                        resultNode[Nodes.createdAt])
                .select { Nodes.pk eq from.pk }
                .map { it.toNode<TO>() }
    }

    private fun <TYPE : Node> ResultRow.toNode(alias: Alias<Nodes>? = null): TYPE =
            if (alias != null) {
                val obj = this[alias[Nodes.attributes]].fromJson<TYPE>(type = this[alias[Nodes.type]])
                obj.pk = this[alias[Nodes.pk]]
                obj.createdAt = Instant.ofEpochSecond(this[alias[Nodes.createdAt]])
                obj
            } else {
                val obj = this[Nodes.attributes].fromJson<TYPE>(type = this[Nodes.type])
                obj.pk = this[Nodes.pk]
                obj.createdAt = Instant.ofEpochSecond(this[Nodes.createdAt])
                obj
            }
}
