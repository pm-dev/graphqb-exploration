package com.starwars.repositories.config

import org.postgresql.ds.PGSimpleDataSource
import org.springframework.stereotype.Component
import ru.yandex.qatools.embed.postgresql.EmbeddedPostgres.cachedRuntimeConfig
import ru.yandex.qatools.embed.postgresql.PostgresProcess
import ru.yandex.qatools.embed.postgresql.PostgresStarter
import ru.yandex.qatools.embed.postgresql.config.AbstractPostgresConfig
import ru.yandex.qatools.embed.postgresql.config.PostgresConfig
import ru.yandex.qatools.embed.postgresql.distribution.Version
import java.io.File
import javax.sql.DataSource

@Component
class DataSourceFactory {

    private var process: PostgresProcess? = null
    private val localDatabaseName get() = "local_postgres_db"
    private val postgresVersion get() = Version.V9_6_5
    private val databaseUsername get() = "master"
    private val cachedPostgresBinary get() = File(System.getProperty("user.home"), ".embedded_pg_bin_$postgresVersion").toPath()

    operator fun invoke(): DataSource {
        if (process != null) {
            throw IllegalStateException("Local postgres process already started")
        }
        try {
            val dbName = "starwars_local_db"
            val localDatabaseDirectory = File(System.getProperty("user.home"), dbName)
            val config = PostgresConfig(
                    postgresVersion,
                    AbstractPostgresConfig.Net(),
                    AbstractPostgresConfig.Storage(localDatabaseName, localDatabaseDirectory.absolutePath),
                    AbstractPostgresConfig.Timeout(),
                    AbstractPostgresConfig.Credentials(databaseUsername, databaseUsername))
                    .withAdditionalInitDbParams(listOf(
                            "-E", "UTF-8",
                            "--locale=en_US.UTF-8",
                            "--lc-collate=en_US.UTF-8",
                            "--lc-ctype=en_US.UTF-8"))
            val runtime = PostgresStarter.getInstance(cachedRuntimeConfig(cachedPostgresBinary))
            val exec = runtime.prepare(config)
            process = exec.start()
            Runtime.getRuntime().addShutdownHook(Thread(Runnable { process?.stop() }))
            val dataSource = PGSimpleDataSource()
            val host = config.net().host()
            dataSource.serverName = host
            val port = config.net().port()
            dataSource.portNumber = port
            dataSource.databaseName = config.storage().dbName()
            dataSource.user = databaseUsername
            dataSource.password = databaseUsername
            return dataSource
        } catch (e: Exception) {
            process?.stop()
            process = null
            throw e
        }
    }
}
