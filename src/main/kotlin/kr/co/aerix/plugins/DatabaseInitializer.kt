package kr.co.aerix.plugins

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kr.co.aerix.entity.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.transactions.transactionManager
import java.sql.ResultSet


object DatabaseInitializer {
    var db: Database? = null;
    var db_psql: Database? = null;
    fun init() {
        db = Database.connect(HikariDataSource(hikariConfig()))
        transaction(db) {
            create(Todos, WorkplaceScheme, FacilityScheme, SensorScheme, UserScheme)
            WorkplaceScheme.insert {
                it[name] = "에어릭스"
            }

            SensorScheme.insert {
                it[max] = 0.05
                it[min] = 0.01
                it[mac] = "F0:B5:D1:9F:24:D8"
                it[name] = "진동센서(D8)"
                it[model] = "T435"
                it[provider] = "AERIX"
                it[placeId] = 1
            }
            SensorScheme.insert {
                it[max] = 0.05
                it[min] = 0.01
                it[mac] = "F0:B5:D1:9E:60:CE"
                it[name] = "진동센서(CE)"
                it[model] = "T435"
                it[provider] = "AERIX"
                it[placeId] = 1
            }
            SensorScheme.insert {
                it[max] = 0.05
                it[min] = 0.01
                it[mac] = "F0:B5:D1:9E:55:B3"
                it[name] = "진동센서(B3)"
                it[model] = "T435"
                it[provider] = "AERIX"
                it[placeId] = 1
            }
            SensorScheme.insert {
                it[max] = 0.05
                it[min] = 0.01
                it[mac] = "90:E2:02:07:9F:BD"
                it[name] = "진동센서(BD)"
                it[model] = "T435"
                it[provider] = "AERIX"
                it[placeId] = 1
            }
            SensorScheme.insert {
                it[max] = 0.05
                it[min] = 0.01
                it[mac] = "F0:B5:D1:9F:28:02"
                it[name] = "진동센서(02)"
                it[model] = "T435"
                it[provider] = "AERIX"
                it[placeId] = 1
            }
        }

        db_psql = Database.connect(HikariDataSource(hikariConfigByPSQL()))
        transaction(db_psql) {
            create(Data)
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

private fun hikariConfigByPSQL() = HikariConfig().apply {
    driverClassName = "org.postgresql.Driver"
    jdbcUrl = "jdbc:postgresql://112.175.232.200:5432/postgres"
    maximumPoolSize = 10
    isAutoCommit = false
    username = "postgres"
    password = "!aerix123"
    transactionIsolation = "TRANSACTION_REPEATABLE_READ"
    validate()
}

suspend fun <T> query(block: () -> T): T = withContext(Dispatchers.IO) {
    transaction(DatabaseInitializer.db) {
        block()
    }
}

suspend fun <T> query_psql(block: () -> T): T = withContext(Dispatchers.IO) {
    transaction(DatabaseInitializer.db_psql) {
        block()
    }
}