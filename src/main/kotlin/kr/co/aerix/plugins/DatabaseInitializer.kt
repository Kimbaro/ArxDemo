package kr.co.aerix.plugins

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kr.co.aerix.entity.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.transactions.transaction


object DatabaseInitializer {
    fun init() {
        Database.connect(HikariDataSource(hikariConfig()))
        transaction {
            create(Todos, WorkplaceScheme, FacilityScheme, SensorScheme)
        }
    }
}

private fun hikariConfig() = HikariConfig().apply {
    driverClassName = "org.h2.Driver"
    jdbcUrl = "jdbc:h2:mem:test"
    maximumPoolSize = 3
    isAutoCommit = false
    username = "aerix"
    password = "!aerix123"
    transactionIsolation = "TRANSACTION_REPEATABLE_READ"
    validate()
}

suspend fun <T> query(block: () -> T): T = withContext(Dispatchers.IO) {
    transaction {
        block()
    }
}