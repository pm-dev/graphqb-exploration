package com.starwars


import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.Test

internal class StarwarsTest {

    @Test
    fun test() {
        Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver")
        transaction {
        }
    }
}
