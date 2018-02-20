package com.starwars.human

import com.coxautodev.graphql.tools.GraphQLResolver
import com.starwars.character.CharacterTypeResolver
import com.framework.GraphDBRepository
import org.springframework.stereotype.Component

@Component
class HumanTypeResolver(
        override val db: GraphDBRepository
): CharacterTypeResolver, GraphQLResolver<Human> {

    fun getHomePlanet(human: Human) = human.homePlanet

    // These redundant overrides are necessary for graphql.tools
    fun getId(node: Human) = super.getId(node)
    fun getName(character: Human) = super.getName(character)
    fun getAppearsIn(character: Human) = super.getAppearsIn(character)
    fun getFriends(character: Human) = super.getFriends(character)
    fun getSecondDegreeFriends(character: Human, limit: Int?) = super.getSecondDegreeFriends(character, limit)
}
