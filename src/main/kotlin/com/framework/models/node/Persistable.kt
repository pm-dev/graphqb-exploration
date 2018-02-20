package com.framework.models.node

import com.framework.models.PrimaryKey

interface Persistable {
    var pk: PrimaryKey?
    val isPersisted: Boolean
}
