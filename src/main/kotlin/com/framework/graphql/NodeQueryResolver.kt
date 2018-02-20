package com.framework.graphql

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import com.framework.GraphDBRepository
import com.framework.fetch
import com.framework.models.node.Node
import graphql.schema.DataFetchingEnvironment
import org.springframework.stereotype.Component

@Component
class NodeQueryResolver(
        val db: GraphDBRepository
): GraphQLQueryResolver {

    fun node(id: Long, environment: DataFetchingEnvironment) =
            db.fetch<Node>(pk = id)
}
