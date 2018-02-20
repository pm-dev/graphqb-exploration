package com.starwars.character

import com.framework.models.edge.Relationship
import com.framework.models.edge.to
import com.framework.models.node.AbstractNode
import com.google.gson.annotations.SerializedName
import com.starwars.episode.Episode
import java.time.Instant

abstract class Character(
        @SerializedName("name") val name: String,
        @SerializedName("appearsIn") val appearsIn: Set<Episode>,
        createdAt: Instant
) : AbstractNode(
        createdAt = createdAt
) {

    val toFriends get() = to(friends)

    val toSecondDegreeFriends get() = to(secondDegreeFriends)

    companion object {
        val friends = Relationship.AsymmetricManyToMany<Character, Character>(name = "friends")
        val secondDegreeFriends = friends.to(friends)
    }
}
