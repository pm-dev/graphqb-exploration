package com.framework.exposed.tables

import com.framework.models.PrimaryKey
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

open class PrimaryKeyTable: Table() {
    val pk: Column<PrimaryKey> = long(name = "pk").autoIncrement().primaryKey()
}
