package com.framework.models.edge

import com.framework.models.node.AbstractIdentifiable
import com.framework.models.node.Persistable
import com.framework.models.PrimaryKey
import com.framework.models.node.Node
import java.time.Instant

class Edge<FROM: Node, TO: Node>(
        override var pk: PrimaryKey? = null,
        val from: FROM,
        val relationship: Relationship.Hop<FROM, TO>,
        val to: TO,
        val createdAt: Instant,
        var deletedAt: Instant? = null
): AbstractIdentifiable(), Persistable {

    override val isPersisted: Boolean get() = pk != null

    val forward: Edge<out Node, out Node>
        get() {
        return when (relationship.direction ?: return this) {
            Relationship.Hop.Direction.FORWARD -> this
            Relationship.Hop.Direction.BACKWARD -> this.inverse
        }
    }

    private val inverse: Edge<TO, FROM>
        get() = Edge(
                pk = pk,
                from = to,
                to = from,
                relationship = relationship.inverse,
                createdAt = createdAt,
                deletedAt = deletedAt)


    @Transient
    private var _id: String? = null

    override val id: String get() =
        _id ?: pk?.toString(radix = 10) ?: {
            val id = when (relationship.direction) {
                Relationship.Hop.Direction.FORWARD -> relationship.name + ":" + from.id + ":" + to.id
                Relationship.Hop.Direction.BACKWARD -> relationship.name + ":" + to.id + ":" + from.id
                null -> if (from.id < to.id) {
                    relationship.name + ":" + from.id + ":" + to.id
                } else {
                    relationship.name + ":" + to.id + ":" + from.id
                }
            }
            _id = id; id
        }()
}
