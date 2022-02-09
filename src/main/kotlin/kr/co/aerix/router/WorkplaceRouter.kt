package kr.co.aerix.router

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kr.co.aerix.model.WorkplaceRequest
import kr.co.aerix.service.WorkplaceService
import main.kotlin.model.TodoRequest

fun Routing.workplace(service: WorkplaceService) {
    route("/workplace") {
        get {
//            call.respond(service.getAll())
            call.respond(service.getMainDashboardItems())

        }
        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull() ?: throw BadRequestException("Parameter id is null");
            var workplace: WorkplaceRequest = service.getById(id)
            println(workplace);

            call.respond(service.getById(id))
        }
        post {
            val body = call.receive<WorkplaceRequest>()
            println(body.name);
            service.new(body.name)
            call.response.status(HttpStatusCode.Created)
        }
    }
}