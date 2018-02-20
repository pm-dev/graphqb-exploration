package com.framework.models.node

import com.framework.exposed.tables.Nodes
import com.framework.models.NodeType
import com.framework.models.PrimaryKey
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import java.time.Instant

class NodeDBFilter(
        val pks: Collection<PrimaryKey>? = null,
        val restrictTo: Set<NodeType>? = null,
        val attributesRegex: String? = null,
        val createdSince: Instant? = null
) {

    constructor(vararg pks: PrimaryKey,
                restrictTo: Set<NodeType>? = null,
                attributesRegex: String? = null,
                createdSince: Instant? = null
    ) : this(
            pks = pks.asList(),
            restrictTo = restrictTo,
            attributesRegex = attributesRegex,
            createdSince = createdSince)

    constructor(pk: PrimaryKey,
                restrictTo:Set<NodeType>? = null,
                attributesRegex: String? = null,
                createdSince: Instant? = null
    ) : this(
            pks = listOf(pk),
            restrictTo = restrictTo,
            attributesRegex = attributesRegex,
            createdSince = createdSince)


    private fun pksFilter(alias: Alias<Nodes>?) = pks?.let {
        (alias?.let { it[Nodes.pk] } ?: Nodes.pk) inList pks.toSet()
    }

    private fun typesFilter(alias: Alias<Nodes>?) = restrictTo?.let {
        (alias?.let { it[Nodes.type] } ?: Nodes.type) inList restrictTo
    }

    private fun attributesFilter(alias: Alias<Nodes>?) = attributesRegex?.let {
        RegexpOp(alias?.let { it[Nodes.attributes] } ?: Nodes.attributes, stringParam(attributesRegex))
    }

    private fun createdSinceFilter(alias: Alias<Nodes>?) = createdSince?.let {
        GreaterEqOp(alias?.let { it[Nodes.createdAt] } ?: Nodes.createdAt, longParam(createdSince.epochSecond))
    }

    internal fun toBooleanOp(alias: Alias<Nodes>? = null): Op<Boolean>? {
        val op = listOfNotNull(
                pksFilter(alias),
                typesFilter(alias),
                attributesFilter(alias),
                createdSinceFilter(alias))
                .stream()
                .reduce { a, b -> a and b }
        return if (op.isPresent) op.get() else null
    }
}
