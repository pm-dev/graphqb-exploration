package com.framework.exposed.tables

internal object Nodes: PrimaryKeyTable() {
    val type = varchar(name = "type", length = 100).index()
    val attributes = text(name = "attributes")
    val createdAt = long(name = "created_at")
}
