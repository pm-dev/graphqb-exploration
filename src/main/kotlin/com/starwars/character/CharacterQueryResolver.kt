package com.starwars.character

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import com.framework.GraphDBRepository
import com.framework.exposed.tables.Nodes
import com.framework.fetch
import com.starwars.human.Human
import graphql.schema.DataFetchingEnvironment
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class CharacterQueryResolver(
        val db: GraphDBRepository
) : GraphQLQueryResolver {

    @Transactional(readOnly = true)
    fun hero(environment: DataFetchingEnvironment): Character =
            /**
             * Ideally, [Nodes.attributes] would be jsonb, which would allow us to filter based
             * on any attributes of a node. The Exposed SQL library doesn't offer json or jsonb as a
             * column type, so for now, we only have the ability to pattern match on the json string blob
             * that results from the object's gson serialization.
             */
            db.fetch<Human>(attributesContains = "\"name\":\"Luke Skywalker\"").single()

    @Transactional(readOnly = true)
    fun character(name: String, environment: DataFetchingEnvironment) =
            db.fetch<Character>().find { it.name == name }
}
