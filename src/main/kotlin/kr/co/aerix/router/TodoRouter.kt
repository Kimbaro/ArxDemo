package kr.co.aerix.router

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.*
import kr.co.aerix.service.TodoService
import io.ktor.http.HttpStatusCode
import main.kotlin.model.TodoRequest

fun Routing.todo(service: TodoService) {
    route("/todos") {
        get {
            call.respond(service.getAll())
        }
        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull() ?: throw BadRequestException("Parameter id is null");
            call.respond(service.getById(id))
        }
        post {
            val body = call.receive<TodoRequest>()
            println(body.content);
            service.new(body.content)
            call.response.status(HttpStatusCode.Created)
        }
    }
}