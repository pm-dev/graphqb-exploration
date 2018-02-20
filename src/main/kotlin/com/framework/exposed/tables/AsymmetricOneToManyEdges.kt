package com.framework.exposed.tables

internal object AsymmetricOneToManyEdges : Edges() {
    init {
        index(isUnique = true, columns = *arrayOf(name, to))
    }
}
