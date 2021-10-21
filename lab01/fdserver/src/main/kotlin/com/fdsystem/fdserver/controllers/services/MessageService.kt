package com.fdsystem.fdserver.controllers.services

import com.fdsystem.fdserver.controllers.Message
import io.swagger.annotations.Api
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Service

interface MessageRepository : CrudRepository<Message, String>
{
    @Query("select * from messages")
    fun findMessages(): List<Message>
}

@Service
class MessageService(val db: MessageRepository)
{
    fun findMessages(): List<Message> = db.findMessages()

    fun post(message: Message)
    {
        db.save(message)
    }
}