package kr.co.aerix.service

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import kr.co.aerix.entity.*
import kr.co.aerix.model.FacilityResponse
import kr.co.aerix.model.SensorResponse
import kr.co.aerix.plugins.query
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class SensorService {
    suspend fun getAll() = query {
        Sensor.all().orderBy(SensorScheme.id to SortOrder.DESC)
            .map(SensorResponse.Companion::of)
            .toList()
    }

    suspend fun getById(id: Int) = query {
        Sensor.findById(id)?.run(SensorResponse.Companion::of) ?: throw NotFoundException()
    }

    suspend fun new(mac: String, model: String, name: String, provider: String, workplace_id: Int) =
        newSuspendedTransaction {
            var uniqueCheck: Boolean = false
            SensorScheme.selectAll().forEach {
                if (it.get(SensorScheme.mac).equals(mac)) {
                    uniqueCheck = true;
                }
            }
            if (uniqueCheck) {
                throw IllegalAccessError("중복된 맥주소가 존재합니다.")
            } else {
                Sensor.new {
                    this.mac = mac
                    this.model = model
                    this.name = name
                    this.provider = provider
                    this.workplace_id = workplace_id
                }
            }
        }
}