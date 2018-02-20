package com.starwars


import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.Test

object Edges : Table() {
    val id = integer("id").autoIncrement().primaryKey() // Column<String>
    val from = (integer("from") references Nodes.id)
    val to = (integer("to") references Nodes.id)
}

object Nodes : Table() {
    val id = integer("id").autoIncrement().primaryKey() // Column<Int>
    val name = varchar("name", 50) // Column<String>
}

internal class StarwarsTest {

    @Test
    fun test() {
        Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver")
        transaction {

            create (Edges, Nodes)

            val user1 = Nodes.insert {
                it[name] = "user1"
            } get Nodes.id

            val user2 = Nodes.insert {
                it[name] = "user2"
            } get Nodes.id

            val user3 = Nodes.insert {
                it[name] = "user3"
            } get Nodes.id

            val user4 = Nodes.insert {
                it[name] = "user4"
            } get Nodes.id

            val user5 = Nodes.insert {
                it[name] = "user5"
            } get Nodes.id

            val user6 = Nodes.insert {
                it[name] = "user6"
            } get Nodes.id

            val user7 = Nodes.insert {
                it[name] = "user7"
            } get Nodes.id

            val account1 = Nodes.insert {
                it[name] = "acct1"
            } get Nodes.id

            val subscription1 = Nodes.insert {
                it[name] = "sub1"
            } get Nodes.id


            Edges.insert {
                it[from] = user1
                it[to] = user2
            }

            Edges.insert {
                it[from] = user1
                it[to] = user5
            }

            Edges.insert {
                it[from] = user2
                it[to] = user3
            }

           Edges.insert {
                it[from] = user2
                it[to] = user4
            }

            Edges.insert {
                it[from] = user3
                it[to] = user5
            }

            Edges.insert {
                it[from] = user6
                it[to] = user4
            }

            val firstDegree = Edges.alias("firstDegree")
            val secondDegree = Edges.alias("secondDegree")
            val thirdDegree = Edges.alias("thirdDegree")
            val nodeFinal = Nodes.alias("nodesfinal")


            val to1 = Nodes
                    .join(
                            otherTable = firstDegree,
                            joinType = JoinType.INNER,
                            additionalConstraint = {
                                val forward = Nodes.id eq firstDegree[Edges.from]
                                val backward = Nodes.id eq firstDegree[Edges.to]
                                forward or backward
                            })

                    .join(
                            otherTable = secondDegree,
                            joinType = JoinType.INNER,
                            additionalConstraint = {
                                val forward = firstDegree[Edges.from] eq secondDegree[Edges.from] and firstDegree[Edges.from].neq(Nodes.id)
                                val backward = firstDegree[Edges.to] eq secondDegree[Edges.from] and firstDegree[Edges.to].neq(Nodes.id)
                                val forward2 = firstDegree[Edges.from] eq secondDegree[Edges.to] and firstDegree[Edges.from].neq(Nodes.id)
                                val backward2 = firstDegree[Edges.to] eq secondDegree[Edges.to] and firstDegree[Edges.to].neq(Nodes.id)
                                forward or backward or forward2 or backward2
                            })
                    .join(
                            otherTable = thirdDegree,
                            joinType = JoinType.INNER,
                            additionalConstraint = {
                                val forward = secondDegree[Edges.from] eq thirdDegree[Edges.from] and secondDegree[Edges.from].neq(firstDegree[Edges.from]) and secondDegree[Edges.from].neq(firstDegree[Edges.to])
                                val backward = secondDegree[Edges.to] eq thirdDegree[Edges.from] and secondDegree[Edges.to].neq(firstDegree[Edges.from])  and secondDegree[Edges.to].neq(firstDegree[Edges.to])
                                val forward2 = secondDegree[Edges.from] eq thirdDegree[Edges.to] and secondDegree[Edges.from].neq(firstDegree[Edges.from]) and secondDegree[Edges.from].neq(firstDegree[Edges.to])
                                val backward2 = secondDegree[Edges.to] eq thirdDegree[Edges.to] and secondDegree[Edges.to].neq(firstDegree[Edges.from]) and secondDegree[Edges.to].neq(firstDegree[Edges.to])
                                forward or backward or forward2 or backward2
                            })
                    .join(
                            otherTable = nodeFinal,
                            joinType = JoinType.INNER,
                            additionalConstraint = {
                                val forward = thirdDegree[Edges.from] eq nodeFinal[Nodes.id] and thirdDegree[Edges.from].neq(secondDegree[Edges.from]) and thirdDegree[Edges.from].neq(secondDegree[Edges.to])
                                val backward = thirdDegree[Edges.to] eq nodeFinal[Nodes.id] and thirdDegree[Edges.to].neq(secondDegree[Edges.from])  and thirdDegree[Edges.to].neq(secondDegree[Edges.to])
                                forward or backward
                            })

                    .slice(nodeFinal[Nodes.name])
                    .select { (Nodes.id eq user1) }
            println("Results ${to1.map { it }}")
        }
    }
}
