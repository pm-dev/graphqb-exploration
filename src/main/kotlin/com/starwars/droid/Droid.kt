package com.starwars.droid

import com.google.gson.annotations.SerializedName
import com.framework.models.NodeType
import com.starwars.character.Character
import com.starwars.episode.Episode
import java.time.Instant

class Droid(
        @SerializedName("primaryFunction") val primaryFunction: String,
        name: String,
        appearsIn: Set<Episode>,
        createdAt: Instant
) : Character(
        name = name,
        appearsIn = appearsIn,
        createdAt = createdAt
) {

    override val type get() = Companion.type

    companion object {
        const val type = "Droid"
    }
}
