package kr.co.aerix.entity

import kr.co.aerix.entity.FacilityScheme.references
import kr.co.aerix.entity.FacilityScheme.uniqueIndex
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

//table scheme
object SensorScheme : IntIdTable() {
    val mac = text("mac");
    val model = text("model");
    val status = text("status").default("UNKNOWN");
    val name = text("name");
    val workplace_id = integer("workplace_id").references(WorkplaceScheme.id)
}

class Sensor(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Sensor>(SensorScheme)

    var mac by SensorScheme.mac
    var model by SensorScheme.model
    var status by SensorScheme.status
    var name by SensorScheme.name
    var workplace_id by SensorScheme.workplace_id
}

data class Sensor_domain(val mac: String, val name: String);