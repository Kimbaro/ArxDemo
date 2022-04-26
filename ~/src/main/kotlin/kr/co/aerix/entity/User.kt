package kr.co.aerix.entity

import kr.co.aerix.entity.FacilityScheme.references
import kr.co.aerix.entity.FacilityScheme.uniqueIndex
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object UserScheme : IntIdTable() {
    val user_id = text("user_id")
    val password = text("password")
    val level = text("level").default("1") //1, 2, 3..
}

class User(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Facility>(FacilityScheme)

    var name by FacilityScheme.name
    var workplace_id by FacilityScheme.workplace_id
}