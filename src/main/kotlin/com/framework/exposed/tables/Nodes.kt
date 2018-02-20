package com.framework.exposed.tables

internal object Nodes: PrimaryKeyTable() {
    val type = varchar(name = "type", length = 100).index()
    /**
     * Ideally, [Nodes.attributes] would be jsonb, which would allow us to filter based
     * on any attributes of a node. The Exposed SQL library doesn't offer json or jsonb as a
     * column type, so for now, we only have the ability to pattern match on the json string blob.
     * (Otherwise it would be possible to be more space efficient by serializing the json blob as binary)
     */
    val attributes = text(name = "attributes")
    val createdAt = long(name = "created_at")
}
