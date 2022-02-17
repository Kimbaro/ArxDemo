package kr.co.aerix

import io.ktor.application.*
import io.ktor.client.features.json.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.routing.*
import kr.co.aerix.plugins.*
import kr.co.aerix.router.*
import kr.co.aerix.service.*

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
        workplace(WorkplaceService())
        facility(FacilityService())
        sensor(SensorService(), WorkplaceService())
        data(DataService(), SensorService())
    }
}
