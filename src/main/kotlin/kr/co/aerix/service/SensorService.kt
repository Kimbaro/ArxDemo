package kr.co.aerix.service

import io.ktor.features.*
import kr.co.aerix.entity.*
import kr.co.aerix.model.SensorPatch
import kr.co.aerix.model.SensorResponse
import kr.co.aerix.plugins.query
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

    suspend fun delete(id: Int) = query {
        Sensor.findById(id)?.delete() ?: throw NotFoundException()
    }

    suspend fun getById(id: Int) = query {
        Sensor.findById(id)?.run(SensorResponse.Companion::of) ?: throw NotFoundException()
    }

    suspend fun patch(req: SensorPatch) = query {
        val patcher = Sensor.findById(req.id) ?: throw NotFoundException();
        if (req.min != null && req.max != null) {
            patcher.apply {
                mac = req.mac
                model = req.model
                name = req.name
                provider = req.provider
                min = req.min
                max = req.max
            }
        }else{
        }
    }

    suspend fun new(mac: String, model: String, name: String, provider: String, placeId: Int) =
        query {
            var uniqueCheck: Boolean = false
            SensorScheme.select { SensorScheme.mac.eq(mac) }.limit(1).forEach {
                uniqueCheck = true;
            }
            if (uniqueCheck) {
                throw IllegalAccessError("중복된 맥주소가 존재합니다.")
            } else {
                Sensor.new {
                    this.mac = mac
                    this.model = model
                    this.name = name
                    this.provider = provider
                    this.placeId = placeId
                }
            }
        }
}