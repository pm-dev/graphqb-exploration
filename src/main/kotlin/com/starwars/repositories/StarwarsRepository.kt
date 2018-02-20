package com.starwars.repositories

import com.framework.exposed.FrameworkRepository
import com.framework.models.NodeType
import com.framework.models.node.Node
import com.google.common.collect.ImmutableBiMap
import com.starwars.character.Character
import com.starwars.droid.Droid
import com.starwars.human.Human
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import kotlin.reflect.KClass

@Component
@Transactional
class StarwarsRepository : FrameworkRepository {

    override val nodeTypesByClass: ImmutableBiMap<KClass<out Node>, Set<NodeType>> = ImmutableBiMap.copyOf(mapOf(
            Character::class to setOf(Droid.type, Human.type),
            Droid::class to setOf(Droid.type),
            Human::class to setOf(Human.type)))
}
