package kr.co.aerix.service

import io.ktor.features.*
import io.ktor.util.reflect.*
import kr.co.aerix.entity.Todo
import kr.co.aerix.entity.Todos
import kr.co.aerix.plugins.query
import main.kotlin.model.TodoResponse
import org.jetbrains.exposed.sql.SortOrder

class TodoService {
    suspend fun getAll() = query {
        Todo.all()
            .orderBy(Todos.id to SortOrder.DESC)
            .map(TodoResponse.Companion::of)
            .toList()
    }

    suspend fun getById(id: Int) = query {
        Todo.findById(id)?.run(TodoResponse.Companion::of) ?: throw NotFoundException()
    }

    suspend fun new(content: String) = query {
        Todo.new { this.content = content }
    }
}