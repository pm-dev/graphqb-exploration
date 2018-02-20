package com.framework

import com.framework.models.node.Node

interface NodeWriteRepository {

    fun <TYPE : Node> save(node: TYPE): TYPE
            = saveNodes(nodes = listOf(node)).single()

    fun <TYPE : Node> save(vararg nodes: TYPE): List<TYPE>
            = saveNodes(nodes = nodes.asIterable())

    fun <TYPE : Node> saveNodes(nodes: Iterable<TYPE>): List<TYPE>

    fun <TYPE : Node> persistIfNecessary(node: TYPE): TYPE
            = persistIfNecessary(nodes = listOf(node)).single()

    fun <TYPE : Node> persistIfNecessary(vararg nodes: TYPE): List<TYPE>
            = persistIfNecessary(nodes = nodes.asList())

    fun <TYPE : Node> persistIfNecessary(nodes: List<TYPE>): List<TYPE> {
        nodes.groupBy { it.isPersisted }[false]?.let { saveNodes(nodes = it) }
        return nodes
    }
}
