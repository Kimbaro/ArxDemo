package kr.co.aerix.plugins

import io.ktor.jackson.*
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import io.ktor.features.*
import io.ktor.application.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

const val DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss"
fun Application.configureSerialization() {
    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
            registerModule(JavaTimeModule().apply {
                addSerializer(
                    LocalDateTimeSerializer(
                        DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)
                    )
                )
                addDeserializer(
                    LocalDateTime::class.java,
                    LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT))
                )
            })
        }
    }
/*    routing {
        get("/json/jackson") {
            call.respond(mapOf("hello" to "world"))
        }
    }*/
}
