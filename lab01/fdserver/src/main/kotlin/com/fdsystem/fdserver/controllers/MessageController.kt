package com.fdsystem.fdserver.controllers

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

import com.fdsystem.fdserver.controllers.services.MessageService
import io.swagger.v3.oas.annotations.Operation

@Table("MESSAGES")
data class Message(@Id val id: String?, val text: String)

@RestController
class MessageController(val service: MessageService)
{
    @GetMapping("/messages")
    @Operation(hidden = true)
    fun index(): List<Message> = service.findMessages()

    @PostMapping("/messages")
    @Operation(hidden = true)
    fun post(@RequestBody message: Message)
    {
        service.post(message)
    }
}
