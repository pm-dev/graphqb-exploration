package com.starwars.character

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import com.framework.GraphDBRepository
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
            db.fetch<Human>().single { it.name == "Luke Skywalker" }

    @Transactional(readOnly = true)
    fun character(name: String, environment: DataFetchingEnvironment) =
            db.fetch<Character>().find { it.name == name }
}
