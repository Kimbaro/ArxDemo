package kr.co.aerix.entity

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime
import java.util.*

// Table scheme
object Todos : IntIdTable() {
    val content = text("content").default("")
    val done = bool("done").default(false)
    val createdAt = datetime("created_at").index().default(LocalDateTime.now())
    val updatedAt = datetime("updated_at").default(LocalDateTime.now())
}

// Entity
class Todo(){
    companion object{
        var old_date: Date? = null;
    }
}