package com.framework.exposed.tables

internal object AsymmetricManyToManyEdges: Edges() {
    init {
        index(isUnique = true, columns = *arrayOf(name, from, to))
    }
}
