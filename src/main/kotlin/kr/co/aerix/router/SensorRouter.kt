package kr.co.aerix.router

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kr.co.aerix.model.SensorPatch
import kr.co.aerix.model.SensorRequest
import kr.co.aerix.model.WorkplacePatch
import kr.co.aerix.service.SensorService
import kr.co.aerix.service.WorkplaceService
import main.kotlin.model.TodoRequest

fun Routing.sensor(service: SensorService, returnService: WorkplaceService) {
    route("/demo") {
        get {
            call.respond(service.getAll())
        }
        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull() ?: throw BadRequestException("Parameter id is null");
            call.respond(service.getById(id))
        }

        delete("/sensor/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
                ?: throw BadRequestException("Parameter id is null")
            service.delete(id)
            call.respond(
                returnService.getMainDashboardItems()
            ).apply { HttpStatusCode.OK }
        }

        patch("/sensor") {
            val body = call.receive<SensorPatch>()
            service.patch(body)
            call.respond(
                SensorPatch(
                    id = body.id,
                    mac = body.mac,
                    model = body.model,
                    status = body.status,
                    name = body.name,
                    provider = body.provider,
                    placeId = body.placeId,
                    min = body.min,
                    max = body.max
                )
            ).apply {
                HttpStatusCode.OK
            }
        }

        post("/sensor") {
            val body = call.receive<SensorRequest>()
            println(body.name);
            service.new(
                mac = body.mac,
                model = body.model,
                name = body.name,
                provider = body.provider,
                placeId = body.placeId
            )
            call.respond(
                returnService.getMainDashboardItems()
            ).apply { HttpStatusCode.OK }
        }
    }
}