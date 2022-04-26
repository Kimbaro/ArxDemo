package main.kotlin.model

import kr.co.aerix.entity.Todo

import java.time.LocalDateTime

data class TodoResponse(
    val id: Int,
    val content: String,
    val done: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {

}