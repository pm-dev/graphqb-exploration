package com.framework.exposed.tables

import org.jetbrains.exposed.sql.SchemaUtils

object GraphDBSchemaGenerator {

    private val tables = listOf(
            Nodes,
            AsymmetricOneToOneEdges,
            AsymmetricOneToManyEdges,
            AsymmetricManyToManyEdges,
            SymmetricOneToOneEdges,
            SymmetricOneToOneConstraints,
            SymmetricManyToManyEdges)

    fun dropTabels() =
            SchemaUtils.drop(tables = *tables.toTypedArray())


    fun createTables() =
            SchemaUtils.create(tables = *tables.toTypedArray())
}
