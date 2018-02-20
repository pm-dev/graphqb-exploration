package com.starwars.character

import com.framework.GraphDBRepository

interface CharacterTypeResolver {

    val db: GraphDBRepository

    fun getId(character: Character) = character.id
    fun getName(character: Character) = character.name
    fun getAppearsIn(character: Character) = character.appearsIn
    fun getFriends(character: Character) = db.traverse(character.toFriends)

    fun getSecondDegreeFriends(character: Character, limit: Int?): List<Character> {
        val secondDegree = db.traverse(character.toSecondDegreeFriends)
                .distinct()
                .filter { secondDegreeFriend -> secondDegreeFriend != character }
        return if (limit == null) secondDegree else secondDegree.subList(0, minOf(limit, secondDegree.size))
    }
}
