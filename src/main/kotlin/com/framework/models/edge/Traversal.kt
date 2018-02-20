package com.framework.models.edge

interface Traversal<FROM, TO> {

    val relationship: Relationship<FROM, TO>

    interface SingleBound<FROM, TO>: Traversal<FROM, TO> {
        val from: FROM
    }

    interface MultiBound<FROM, TO>: Traversal<FROM, TO> {
        val froms: Collection<FROM>
    }

    interface ToOne<FROM, TO>: Traversal<FROM, TO> {
        override val relationship: Relationship.ToOne<FROM, TO>
        fun toMultiBound(): MultiBoundToOne<FROM, TO>
    }

    interface ToMany<FROM, TO>: Traversal<FROM, TO> {
        override val relationship: Relationship.ToMany<FROM, TO>
        fun toMultiBound(): MultiBoundToMany<FROM, TO>
    }

    interface SingleBoundToOne<FROM, TO>: SingleBound<FROM, TO>, ToOne<FROM, TO>

    interface MultiBoundToOne<FROM, TO>: MultiBound<FROM, TO>, ToOne<FROM, TO>

    data class SingleBoundToMany<FROM, TO>(
            override val from: FROM,
            override val relationship: Relationship.ToMany<FROM, TO>
    ): SingleBound<FROM, TO>, ToMany<FROM, TO> {
        override fun toMultiBound() = MultiBoundToMany(froms = listOf(from), relationship = relationship)
    }

    data class MultiBoundToMany<FROM, TO>(
            override val froms: Collection<FROM>,
            override val relationship: Relationship.ToMany<FROM, TO>
    ): MultiBound<FROM, TO>, ToMany<FROM, TO> {
        override fun toMultiBound() = this
    }

    data class SingleBoundToOptional<FROM, TO>(
            override val from: FROM,
            override val relationship: Relationship.ToOptional<FROM, TO>
    ): SingleBoundToOne<FROM, TO> {
        override fun toMultiBound() = MultiBoundToOptional(froms = listOf(from), relationship = relationship)
    }

    data class MultiBoundToOptional<FROM, TO>(
            override val froms: Collection<FROM>,
            override val relationship: Relationship.ToOptional<FROM, TO>
    ): MultiBoundToOne<FROM, TO> {
        override fun toMultiBound() = this
    }

    data class SingleBoundToSingle<FROM, TO>(
            override val from: FROM,
            override val relationship: Relationship.ToSingle<FROM, TO>
    ): SingleBoundToOne<FROM, TO> {
        override fun toMultiBound() = MultiBoundToSingle(froms = listOf(from), relationship = relationship)
    }

    data class MultiBoundToSingle<FROM, TO>(
            override val froms: Collection<FROM>,
            override val relationship: Relationship.ToSingle<FROM, TO>
    ): MultiBoundToOne<FROM, TO> {
        override fun toMultiBound() = this
    }
}

fun <FROM, TO> FROM.to(relationship: Relationship.ToOptional<FROM, TO>) =
        Traversal.SingleBoundToOptional(from = this, relationship = relationship)

fun <FROM, TO> Collection<FROM>.to(relationship: Relationship.ToOptional<FROM, TO>) =
        Traversal.MultiBoundToOptional(froms = this, relationship = relationship)

fun <FROM, TO> FROM.to(relationship: Relationship.ToSingle<FROM, TO>) =
        Traversal.SingleBoundToSingle(from = this, relationship = relationship)

fun <FROM, TO> Collection<FROM>.to(relationship: Relationship.ToSingle<FROM, TO>) =
        Traversal.MultiBoundToSingle(froms = this, relationship = relationship)

fun <FROM, TO> FROM.to(relationship: Relationship.ToMany<FROM, TO>) =
        Traversal.SingleBoundToMany(from = this, relationship = relationship)

fun <FROM, TO> Collection<FROM>.to(relationship: Relationship.ToMany<FROM, TO>) =
        Traversal.MultiBoundToMany(froms = this, relationship = relationship)
