package com.framework.exposed.tables

internal open class Edges: PrimaryKeyTable() {
    val createdAt = long(name = "created_at")
    val deletedAt = long(name = "deleted_at").nullable().index()
    val from = (long(name = "from_id") references Nodes.pk).index()
    val to = (long(name = "to_id") references Nodes.pk).index()
    val name = varchar(name = "name", length = 100).index()
}
