package com.framework.exposed

import com.framework.EdgeReadRepository
import com.framework.EdgeWriteRepository
import com.framework.NodeWriteRepository
import com.framework.exposed.tables.*
import com.framework.exposed.tables.SymmetricManyToManyEdges.symmetricKey
import com.framework.models.PrimaryKey
import com.framework.models.NodeAsJson
import com.framework.models.edge.Edge
import com.framework.models.edge.Relationship
import com.framework.models.node.Node
import com.framework.models.node.Persistable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.BatchInsertStatement
import org.jetbrains.exposed.sql.statements.UpdateStatement
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

interface ExposedWriteRepository : NodeWriteRepository, EdgeWriteRepository, EdgeReadRepository {

    fun <TYPE : Node> TYPE.toJson(): NodeAsJson

    override fun <TYPE : Node> saveNodes(
            nodes: Iterable<TYPE>
    ): List<TYPE>
            = Nodes.save(
            objects = nodes,
            onInsert = { node, stmt ->
                stmt[type] = node.type
                stmt[createdAt] = node.createdAt.epochSecond
                stmt[Nodes.attributes] = node.toJson()
            },
            onUpdate = { node, stmt ->
                stmt[Nodes.attributes] = node.toJson()
            })

    override fun <FROM : Node, TO : Node> saveEdges(
            edges: Iterable<Edge<FROM, TO>>
    ): List<Edge<FROM, TO>> {
        val byTable = edges
                .map { it.forward }.groupBy { it.relationship.edgeTable }
        byTable.forEach { table, edgesForTable ->
            table.save(
                    objects = edgesForTable,
                    onInsert = { edge, stmt ->
                        persistIfNecessary(edge.from, edge.to)
                        stmt[from] = edge.from.pk!!
                        stmt[to] = edge.to.pk!!
                        stmt[SymmetricOneToOneConstraints.name] = edge.relationship.name
                        stmt[createdAt] = edge.createdAt.epochSecond
                        stmt[deletedAt] = edge.deletedAt?.epochSecond
                        when (this) {
                            SymmetricManyToManyEdges -> {
                                stmt[symmetricKey] = edge.from.pk!! xor edge.to.pk!!
                            }
                        }
                        when (edge.relationship) {
                            is Relationship.Hop.SymmetricOneToOne<*> -> SymmetricOneToOneConstraints.add(
                                    relationship = edge.relationship,
                                    first = edge.from,
                                    second = edge.to,
                                    ignore = false)
                        }
                    },
                    onUpdate = { edge, stmt ->
                        stmt[deletedAt] = edge.deletedAt?.epochSecond
                        when (edge.relationship) {
                            is Relationship.Hop.SymmetricOneToOne<*> -> when (edge.deletedAt) {
                                null -> SymmetricOneToOneConstraints.add(
                                        relationship = edge.relationship,
                                        first = edge.from,
                                        second = edge.to,
                                        ignore = true)
                                else -> SymmetricOneToOneConstraints.remove(
                                        relationship = edge.relationship,
                                        first = edge.from,
                                        second = edge.to)
                            }
                        }
                    })
        }
        return edges.toList()
    }

    @Transactional(propagation = Propagation.MANDATORY, readOnly = false)
    private fun <TABLE : PrimaryKeyTable, TYPE : Persistable> TABLE.save(
            objects: Iterable<TYPE>,
            onInsert: TABLE.(TYPE, BatchInsertStatement) -> Unit,
            onUpdate: TABLE.(TYPE, UpdateStatement) -> Unit
    ): List<TYPE> {
        val byIsPersisted = objects.groupBy { it.isPersisted }
        byIsPersisted[false]?.let { nonPersisted ->
            batchInsert(nonPersisted) { obj -> onInsert(obj, this) }
                    .zip(nonPersisted).forEach { (key, obj) -> obj.pk = key[this.pk] as PrimaryKey }
        }
        byIsPersisted[true]?.map { persisted ->
            update(where = { this@save.pk eq persisted.pk }) { onUpdate(persisted, it) }
        }
        return objects.toList()
    }

    private fun SymmetricOneToOneConstraints.add(
            relationship: Relationship.Hop.SymmetricOneToOne<*>,
            first: Node,
            second: Node,
            ignore: Boolean) {
        persistIfNecessary(first, second)
        val table = this
        table.batchInsert(listOf(first, second), ignore = ignore) { node ->
            val stmt = this
            stmt[table.node] = node.pk!!
            stmt[table.name] = relationship.name
        }
    }

    private fun SymmetricOneToOneConstraints.remove(
            relationship: Relationship.Hop.SymmetricOneToOne<*>,
            first: Node,
            second: Node) {
        val table = this
        table.deleteWhere {
            (table.name eq relationship.name) and ((table.node eq first.pk) or (table.node eq second.pk))
        }
    }
}
