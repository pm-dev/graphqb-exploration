package com.starwars.droid

import com.coxautodev.graphql.tools.GraphQLMutationResolver
import com.framework.GraphDBRepository
import com.framework.fetch
import com.framework.models.edge.Edge
import com.starwars.character.Character
import com.starwars.episode.Episode
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class DroidMutationResolver(
        val db: GraphDBRepository
) : GraphQLMutationResolver {

    fun createDroid(
            name: String,
            primaryFunction: String,
            friendIds: Set<Long>,
            appearsInIds: Set<Episode>
    ): Droid {
        val now = Instant.now()
        val droid = db.save(
                Droid(
                        primaryFunction = primaryFunction,
                        name = name,
                        appearsIn = appearsInIds,
                        createdAt = now))
        val friendEdges = db.fetch<Character>(friendIds).map { friend ->
            Edge(
                    from = droid,
                    relationship = Character.friends,
                    to = friend,
                    createdAt = now)
        }
        db.saveEdges(friendEdges)
        return droid
    }
}
