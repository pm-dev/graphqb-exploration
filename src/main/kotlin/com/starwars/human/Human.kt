package com.starwars.human

import com.google.gson.annotations.SerializedName
import com.framework.models.NodeType
import com.starwars.character.Character
import com.starwars.episode.Episode
import java.time.Instant

class Human(
        @SerializedName("homePlanet") val homePlanet: String?,
        name: String,
        appearsIn: Set<Episode>,
        createdAt: Instant
) : Character(
        name = name,
        appearsIn = appearsIn,
        createdAt = createdAt
) {

    override val type: NodeType get() = Companion.type

    companion object {
        const val type: NodeType = "Human"
    }
}
