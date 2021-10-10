package com.fdsystem.fdserver.restcontroller

import org.springframework.data.annotation.Id
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.*

@Table("MESSAGES")
data class Message(@Id val id: String?, val text: String)

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

@RestController
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

@RestController
@RequestMapping("/api/user")
class UserController()
{
    @GetMapping("/login/{token}")
    fun login(@PathVariable("token") token: String): String
    {
        return token
    }

    @GetMapping("/logout")
    fun logout()
    {
    }

    @GetMapping("/data/{bucket}")
    fun getData(@PathVariable("bucket") bucket: String): String
    {
        return bucket
    }

    @PostMapping("/data/{bucket}")
    fun addData(@PathVariable("bucket") bucket: String): String
    {
        return bucket
    }
}

@RestController
@RequestMapping("/api/subject")
class SubjectController()
{
    @PostMapping("/{bucket}")
    fun createNewBucket(@PathVariable("bucket") bucket: String): String
    {
        return bucket
    }

    @DeleteMapping("/{bucket}")
    fun deleteBucket(@PathVariable("bucket") bucket: String): String
    {
        return bucket
    }
}