package com.starwars.droid

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import com.framework.GraphDBRepository
import com.framework.fetch
import graphql.schema.DataFetchingEnvironment
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class DroidQueryResolver(
        val db: GraphDBRepository
): GraphQLQueryResolver {

    @Transactional(readOnly = true)
    fun droid(name: String, environment: DataFetchingEnvironment) =
            db.fetch<Droid>().find { it.name == name }
}
