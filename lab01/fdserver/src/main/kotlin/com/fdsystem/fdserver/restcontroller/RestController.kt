package com.fdsystem.fdserver.restcontroller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.data.annotation.Id
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@Table("MESSAGES")
data class Message(@Id val id: String?, val text: String)

interface MessageRepository : CrudRepository<Message, String>
{

    @Query("select * from messages")
    fun findMessages(): List<Message>
}

@Service
class MessageService(val db: MessageRepository) {

    fun findMessages(): List<Message> = db.findMessages()

    fun post(message: Message){
        db.save(message)
    }
}

@RestController
class MessageResource(val service: MessageService) {
    @GetMapping("/api")
    fun index(): List<Message> = service.findMessages()

    @PostMapping("/api")
    fun post(@RequestBody message: Message) {
        service.post(message)
    }
}