package kr.co.aerix.router

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kr.co.aerix.model.SensorRequest
import kr.co.aerix.service.SensorService
import main.kotlin.model.TodoRequest

fun Routing.sensor(service: SensorService) {
    route("/sensor") {
        get {
            call.respond(service.getAll())
        }
        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull() ?: throw BadRequestException("Parameter id is null");
            call.respond(service.getById(id))
        }
        post {
            val body = call.receive<SensorRequest>()
            println(body.name);

            service.new(
                mac = body.mac,
                model = body.model,
                name = body.name,
                provider = body.provider,
                workplace_id = body.workplace_id
            )
            call.response.status(HttpStatusCode.Created)
        }
    }
}