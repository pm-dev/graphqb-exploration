package com.framework.exposed

import com.framework.GraphDBRepository
import com.framework.models.NodeType
import com.framework.models.NodeAsJson
import com.framework.models.node.Node
import com.google.common.collect.ImmutableBiMap
import com.google.gson.Gson
import kotlin.reflect.KClass

interface FrameworkRepository : GraphDBRepository, ExposedReadRepository, ExposedWriteRepository {

    val nodeSerializer get() = defaultGson

    val nodeTypesByClass: ImmutableBiMap<KClass<out Node>, Set<NodeType>>

    override fun <TYPE : Node> TYPE.toJson(): NodeAsJson = nodeSerializer.toJson(this)

    override fun <TYPE : Node> NodeAsJson.fromJson(
            type: NodeType
    ): TYPE {
        val kClass = nodeTypesByClass.inverse()[setOf(type)]
                ?: throw IllegalStateException("Make sure to register KClass for type $type")
        return nodeSerializer.fromJson<TYPE>(this, kClass.java)
    }

    override val <TYPE : Node> KClass<TYPE>.nodeTypes: Set<NodeType> get() =
        nodeTypesByClass[this] ?: throw IllegalStateException("Make sure to register type for KClass $this")

    companion object {
        private val defaultGson = Gson()
    }
}
