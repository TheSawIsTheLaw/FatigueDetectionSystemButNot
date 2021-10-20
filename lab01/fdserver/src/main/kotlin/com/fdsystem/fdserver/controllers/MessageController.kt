package com.fdsystem.fdserver.controllers

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import springfox.documentation.annotations.ApiIgnore

import com.fdsystem.fdserver.controllers.services.MessageService

@Table("MESSAGES")
data class Message(@Id val id: String?, val text: String)



@RestController
@ApiIgnore
class MessageController(val service: MessageService)
{
    @GetMapping("/messages")
    fun index(): List<Message> = service.findMessages()

    @PostMapping("/messages")
    fun post(@RequestBody message: Message)
    {
        service.post(message)
    }
}