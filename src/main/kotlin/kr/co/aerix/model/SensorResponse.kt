package kr.co.aerix.model

import kr.co.aerix.entity.Facility
import kr.co.aerix.entity.Sensor

data class SensorResponse(
    val mac: String,
    val model: String,
    val status: String?,
    val name: String,
    val workplace_id: Int
) {
    companion object {
        fun of(sensor: Sensor) =
            SensorResponse(
                mac = sensor.mac,
                model = sensor.model,
                status = sensor.status,
                name = sensor.name,
                workplace_id = sensor.workplace_id
            )
    }
}
