package com.framework.exposed.tables

internal object AsymmetricOneToOneEdges : Edges() {
    init {
        index(isUnique = true, columns = *arrayOf(name, from))
        index(isUnique = true, columns = *arrayOf(name, to))
    }
}


