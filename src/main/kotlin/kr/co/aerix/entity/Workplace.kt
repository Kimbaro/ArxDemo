package kr.co.aerix.entity

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Table

//table scheme
object WorkplaceScheme : IntIdTable() {
    val name = text("name");
}

class Workplace(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Workplace>(WorkplaceScheme)

    var name by WorkplaceScheme.name
}

data class Workplace_domain(val id: Int, val name: String)

data class MainDashboardItem(val id: Int, val name: String, val items: List<MainDashboardSubItem>?)

data class MainDashboardSubItem(
    val id: Int,
    val name: String,
    val mac: String,
    val provider: String,
    val model: String
);