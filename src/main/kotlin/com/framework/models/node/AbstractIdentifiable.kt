package com.framework.models.node

import com.framework.models.Identifier

abstract class AbstractIdentifiable: Identifiable {

    abstract override val id: Identifier

    override fun equals(other: Identifier?) = other != null && other is Identifiable && id == other.id

    override fun hashCode() = id.hashCode()
}
