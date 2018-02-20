package com.framework.exposed.tables

import org.jetbrains.exposed.sql.Table

/**
 * What I want is a Unique constraint on SymmetricOneToOneEdges on (NAME and (FROM or TO))
 * For example
 * Row1 - Name: "sample" From: "1" To: "2"
 * Row2 - Name: "sample" From: "2" To: "3"
 * ... would fail the constraint.
 *
 * I'm not sure if it is possible to create a constraint like that, so I am creating a table
 * to enforce this constraint.
 *
 * Every time a row is created in SymmetricOneToOneEdges, we'll create entry here for both the
 * 'from' and 'to' node. Similarily if a row is deleted from SymmetricOneToOneEdges, we'll
 * delete both the 'from' and 'to' entry from this table.
 */
object SymmetricOneToOneConstraints : Table() {
    val node = long(name = "node_id").index()
    val name = varchar(name = "name", length = 100)
    init {
        index(isUnique = true, columns = *arrayOf(node, name))
    }
}
