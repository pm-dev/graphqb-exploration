package com.framework.exposed.tables

import com.framework.models.edge.Relationship

internal val Relationship.Hop<*, *>.edgeTable: Edges
    get() = when (type) {
        Relationship.Hop.Type.ASYMMETRIC_SINGLE_TO_OPTIONAL -> AsymmetricOneToOneEdges
        Relationship.Hop.Type.ASYMMETRIC_OPTIONAL_TO_SINGLE -> AsymmetricOneToOneEdges
        Relationship.Hop.Type.ASYMMETRIC_OPTIONAL_TO_OPTIONAL -> AsymmetricOneToOneEdges
        Relationship.Hop.Type.ASYMMETRIC_SINGLE_TO_SINGLE -> AsymmetricOneToOneEdges
        Relationship.Hop.Type.ASYMMETRIC_MANY_TO_SINGLE -> AsymmetricOneToManyEdges
        Relationship.Hop.Type.ASYMMETRIC_MANY_TO_OPTIONAL -> AsymmetricOneToManyEdges
        Relationship.Hop.Type.ASYMMETRIC_SINGLE_TO_MANY -> AsymmetricOneToManyEdges
        Relationship.Hop.Type.ASYMMETRIC_OPTIONAL_TO_MANY -> AsymmetricOneToManyEdges
        Relationship.Hop.Type.ASYMMETRIC_MANY_TO_MANY -> AsymmetricManyToManyEdges
        Relationship.Hop.Type.SYMMETRIC_OPTIONAL_TO_OPTIONAL -> SymmetricOneToOneEdges
        Relationship.Hop.Type.SYMMETRIC_SINGLE_TO_SINGLE -> SymmetricOneToOneEdges
        Relationship.Hop.Type.SYMMETRIC_MANY_TO_MANY -> SymmetricManyToManyEdges
    }
