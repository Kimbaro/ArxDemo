package kr.co.aerix.entity

import kr.co.aerix.entity.FacilityScheme.references
import kr.co.aerix.entity.FacilityScheme.uniqueIndex
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

//table scheme
object SensorScheme : IntIdTable() {
    val mac = text("mac")
    val model = text("model");
    val status = text("status").default("UNKNOWN");
    val provider = text("provider")
    val name = text("name");
    val placeId = integer("placeId").references(WorkplaceScheme.id)
    val min = double("min").nullable()
    val max = double("max").nullable()
}

class Sensor(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Sensor>(SensorScheme)
    var mac by SensorScheme.mac
    var model by SensorScheme.model
    var status by SensorScheme.status
    var provider by SensorScheme.provider
    var name by SensorScheme.name
    var placeId by SensorScheme.placeId
    var min by SensorScheme.min
    var max by SensorScheme.max
}

data class Sensor_domain(val mac: String, val name: String);
