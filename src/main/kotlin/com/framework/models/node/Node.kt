package com.framework.models.node

import com.google.gson.JsonObject
import com.framework.models.NodeType
import java.io.Serializable
import java.time.Instant

interface Node: Persistable, Identifiable, Serializable {
    override val id: String
    val type: NodeType
    var createdAt: Instant
    val attributes: JsonObject
}
