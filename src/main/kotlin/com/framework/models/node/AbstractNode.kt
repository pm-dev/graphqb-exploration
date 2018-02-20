package com.framework.models.node

import com.framework.models.PrimaryKey
import com.google.gson.Gson
import com.google.gson.JsonObject
import java.io.Serializable
import java.time.Instant
import java.util.*

abstract class AbstractNode(
        @Transient override var pk: PrimaryKey? = null,
        @Transient override var createdAt: Instant
): AbstractIdentifiable(), Node {

    override val isPersisted: Boolean get() = pk != null

    override val attributes: JsonObject get() = gson.toJsonTree(this).asJsonObject

    @Transient
    private var _id: String? = null

    override val id: String get() =
        _id ?: pk?.toString(radix = 10) ?: {
            val id = UUID.randomUUID().toString()
            _id = id; id
        }()

    companion object {
        private val gson = Gson()
    }
}
