package com.framework

import com.starwars.repositories.config.DataSourceFactory
import com.starwars.repositories.config.TransactionManagerFactory
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.transaction.annotation.EnableTransactionManagement
import javax.sql.DataSource


@SpringBootApplication
@EnableTransactionManagement
@EnableAutoConfiguration
@ComponentScan(basePackages = ["com.framework", "com.starwars"])
class Application(
        val dataSourceFactory: DataSourceFactory,
        val transactionManagerFactory: TransactionManagerFactory
) {
    @Bean
    fun dataSource() = dataSourceFactory()

    @Bean
    fun transactionManager(dataSource: DataSource) = transactionManagerFactory(dataSource)
}

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}
