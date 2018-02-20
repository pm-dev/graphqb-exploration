package com.framework.exposed.tables

internal object SymmetricManyToManyEdges : Edges() {
    val symmetricKey = long(name = "symmetricKey").index()
    init {
        index(isUnique = true, columns = *arrayOf(name, symmetricKey))
    }
}
