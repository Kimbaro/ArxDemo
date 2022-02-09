package kr.co.aerix

import io.ktor.application.*
import io.ktor.routing.*
import kr.co.aerix.plugins.*
import kr.co.aerix.router.facility
import kr.co.aerix.router.sensor
import kr.co.aerix.router.todo
import kr.co.aerix.router.workplace
import kr.co.aerix.service.FacilityService
import kr.co.aerix.service.SensorService
import kr.co.aerix.service.TodoService
import kr.co.aerix.service.WorkplaceService

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    configureMonitoring()
    configureSerialization()
    configureHTTP()

    //custom
    DatabaseInitializer.init()

    install(Routing) {
        todo(TodoService())
        workplace(WorkplaceService())
        facility(FacilityService())
        sensor(SensorService())
    }
}
