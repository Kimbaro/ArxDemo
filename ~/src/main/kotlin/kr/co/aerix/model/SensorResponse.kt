package kr.co.aerix.model

import kr.co.aerix.entity.Sensor
import org.jetbrains.exposed.sql.Column

data class SensorResponse(
    val mac: String,
    val model: String,
    val status: String?,
    val name: String,
    val provider: String,
    val placeId: Int,
    val min: Double?,
    val max: Double?
) {
    companion object {
        fun of(sensor: Sensor) =
            SensorResponse(
                mac = sensor.mac,
                model = sensor.model,
                status = sensor.status,
                name = sensor.name,
                provider = sensor.provider,
                placeId = sensor.placeId,
                min = sensor.min,
                max = sensor.max
            )
    }
}
