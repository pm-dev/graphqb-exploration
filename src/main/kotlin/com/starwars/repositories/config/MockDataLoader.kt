package com.starwars.repositories.config

import com.framework.GraphDBRepository
import com.framework.exposed.tables.GraphDBSchemaGenerator
import com.framework.models.edge.Edge
import com.starwars.character.Character
import com.starwars.droid.Droid
import com.starwars.episode.Episode
import com.starwars.human.Human
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Component
class MockDataLoader(
        val graphdb: GraphDBRepository
): ApplicationListener<ApplicationReadyEvent> {

    @Transactional
    override fun onApplicationEvent(event: ApplicationReadyEvent?) {
        loadStarwars()
    }

    private fun loadStarwars() {

            GraphDBSchemaGenerator.dropTabels()
            GraphDBSchemaGenerator.createTables()

            val now = Instant.now()

            val lukeSkywalker = Human(
                    name = "Luke Skywalker",
                    homePlanet = "Tatooine",
                    appearsIn = setOf(Episode.newHope, Episode.jedi, Episode.empire),
                    createdAt = now)

            val darthVader = Human(
                    name = "Darth Vader",
                    homePlanet = "Tatooine",
                    appearsIn = setOf(Episode.newHope, Episode.jedi, Episode.empire),
                    createdAt = now)

            val hanSolo = Human(
                    name = "Han Solo",
                    appearsIn = setOf(Episode.newHope, Episode.jedi, Episode.empire),
                    homePlanet = null,
                    createdAt = now)

            val leiaOrgana = Human(
                    name = "Leia Organa",
                    homePlanet = "Alderaan",
                    appearsIn = setOf(Episode.newHope, Episode.jedi, Episode.empire),
                    createdAt = now)

            val wilhuffTarkin = Human(
                    name = "Wilhuff Tarkin",
                    appearsIn = setOf(Episode.newHope),
                    homePlanet = null,
                    createdAt = now)

            val c3po = Droid(
                    name = "C-3PO",
                    appearsIn = setOf(Episode.newHope, Episode.jedi, Episode.empire),
                    primaryFunction = "Protocol",
                    createdAt = now)

            val aretoo = Droid(
                    name = "R2-D2",
                    appearsIn = setOf(Episode.newHope, Episode.jedi, Episode.empire),
                    primaryFunction = "Astromech",
                    createdAt = now)


            graphdb.saveEdges(friendEdges(
                    from = lukeSkywalker,
                    to = listOf(hanSolo, leiaOrgana, c3po, aretoo)))

            graphdb.saveEdges(friendEdges(
                    from = darthVader,
                    to = listOf(wilhuffTarkin)))

            graphdb.saveEdges(friendEdges(
                    from = hanSolo,
                    to = listOf(lukeSkywalker, leiaOrgana, aretoo)))

            graphdb.saveEdges(friendEdges(
                    from = leiaOrgana,
                    to = listOf(lukeSkywalker, hanSolo, c3po, aretoo)))

            graphdb.saveEdges(friendEdges(
                    from = wilhuffTarkin,
                    to = listOf(darthVader)))

            graphdb.saveEdges(friendEdges(
                    from = c3po,
                    to = listOf(lukeSkywalker, hanSolo, leiaOrgana, aretoo)))

            graphdb.saveEdges(friendEdges(
                    from = aretoo,
                    to = listOf(lukeSkywalker, hanSolo, leiaOrgana)))

        println("Loaded Starwars Data")
    }

    private fun friendEdges(
            from: Character,
            to: List<Character>
    ): List<Edge<Character, Character>> =
            to.map { Edge(
                    from = from,
                    relationship =
                    Character.friends,
                    to = it,
                    createdAt = Instant.now())
            }

}
