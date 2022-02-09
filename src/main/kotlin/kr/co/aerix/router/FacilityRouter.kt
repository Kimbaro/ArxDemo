package kr.co.aerix.router

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kr.co.aerix.model.FacilityRequest
import kr.co.aerix.model.WorkplaceRequest
import kr.co.aerix.service.FacilityService

fun Routing.facility(service: FacilityService) {
    route("/facility") {
        get {
            call.respond(service.getAll())
        }
        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull() ?: throw BadRequestException("Parameter id is null");
            call.respond(service.getById(id))
        }
        post {
            val body = call.receive<FacilityRequest>()
            println(body.name);
            println(body.workplace_id);
            service.new(body.name, body.workplace_id);
            call.response.status(HttpStatusCode.Created)
        }
    }
}