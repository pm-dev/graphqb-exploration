package com.framework

import com.framework.models.NodeType
import com.framework.models.PrimaryKey
import com.framework.models.edge.Traversal
import com.framework.models.node.Node
import com.framework.models.node.NodeDBFilter
import java.time.Instant
import kotlin.reflect.KClass

interface NodeReadRepository {

    val <TYPE : Node> KClass<TYPE>.nodeTypes: Set<NodeType>

    fun <TYPE: Node> select(where: NodeDBFilter? = null): List<TYPE>

    fun <FROM : Node, TO : Node> traverse(
            traversal: Traversal.MultiBoundToOne<FROM, TO>,
            where: NodeDBFilter? = null
    ): Map<FROM, TO?>

    fun <FROM : Node, TO : Node> traverse(
            traversal: Traversal.MultiBoundToSingle<FROM, TO>,
            where: NodeDBFilter? = null
    ): Map<FROM, TO>

    fun <FROM : Node, TO : Node> traverse(
            traversal: Traversal.MultiBoundToMany<FROM, TO>,
            where: NodeDBFilter? = null
    ): Map<FROM, List<TO>>

    fun <FROM: Node, TO: Node> traverse(
            traversal: Traversal.SingleBoundToOne<FROM, TO>,
            where: NodeDBFilter? = null
    ): TO? =
            traverse(traversal = traversal.toMultiBound(), where = where).values.single()

    fun <FROM: Node, TO: Node> traverse(
            traversal: Traversal.SingleBoundToSingle<FROM, TO>,
            where: NodeDBFilter? = null
    ): TO =
            traverse(traversal = traversal.toMultiBound(), where = where).values.single()

    fun <FROM: Node, TO: Node> traverse(
            traversal: Traversal.SingleBoundToMany<FROM, TO>,
            where: NodeDBFilter? = null
    ): List<TO> =
            traverse(traversal = traversal.toMultiBound(), where = where).values.single()
}

inline fun <reified TYPE: Node> NodeReadRepository.fetch(
        pk: PrimaryKey,
        attributesRegex: String? = null,
        createdSince: Instant? = null
): TYPE? =
        select<TYPE>(
                where = NodeDBFilter(
                        pks = listOf(pk),
                        restrictTo =  TYPE::class.nodeTypes,
                        attributesRegex = attributesRegex,
                        createdSince = createdSince))
                .optional()

inline fun <reified TYPE: Node> NodeReadRepository.fetch(
        pks: Collection<PrimaryKey>? = null,
        attributesRegex: String? = null,
        createdSince: Instant? = null
): List<TYPE> =
        select(
                where = NodeDBFilter(
                        pks = pks,
                        restrictTo = TYPE::class.nodeTypes,
                        attributesRegex = attributesRegex,
                        createdSince = createdSince))

inline fun <reified TYPE: Node> NodeReadRepository.fetch(
        vararg pks: PrimaryKey,
        attributesRegex: String? = null,
        createdSince: Instant? = null
): List<TYPE> =
        select(
                where = NodeDBFilter(
                        pks = pks.asList(),
                        restrictTo = TYPE::class.nodeTypes,
                        attributesRegex = attributesRegex,
                        createdSince = createdSince))

fun <T> Iterable<T>.optional(): T? = if (!iterator().hasNext()) null else single()
