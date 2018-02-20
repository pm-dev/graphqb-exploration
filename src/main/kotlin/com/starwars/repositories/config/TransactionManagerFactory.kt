package com.starwars.repositories.config

import org.jetbrains.exposed.spring.SpringTransactionManager
import org.springframework.stereotype.Component
import org.springframework.transaction.PlatformTransactionManager
import java.sql.Connection
import javax.sql.DataSource

@Component
class TransactionManagerFactory {

    operator fun invoke(dataSource: DataSource): PlatformTransactionManager {
        val manager = SpringTransactionManager(
                dataSource = dataSource,
                defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE)
        manager.isNestedTransactionAllowed = false
        manager.isRollbackOnCommitFailure = true
        return manager
    }
}
