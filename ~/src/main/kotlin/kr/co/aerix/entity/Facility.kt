package kr.co.aerix.entity

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

//table scheme

object FacilityScheme : IntIdTable() {
    val name = text("name")
    val workplace_id = integer("workplace_id").uniqueIndex().references(WorkplaceScheme.id)
}

class Facility(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Facility>(FacilityScheme)

    var name by FacilityScheme.name
    var workplace_id by FacilityScheme.workplace_id
}