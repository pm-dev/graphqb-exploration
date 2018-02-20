package com.starwars.human

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import com.framework.GraphDBRepository
import com.framework.fetch
import graphql.schema.DataFetchingEnvironment
import org.springframework.stereotype.Component

@Component
class HumanQueryResolver(
        val db: GraphDBRepository
) : GraphQLQueryResolver {

    fun human(name: String, environment: DataFetchingEnvironment) =
            db.fetch<Human>().find { it.name == name }
}
