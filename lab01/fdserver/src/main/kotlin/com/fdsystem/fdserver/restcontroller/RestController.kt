package com.fdsystem.fdserver.restcontroller

import com.fdsystem.fdserver.data.CharRepositoryImpl
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

@Service
class BucketsService()
{
    private var repository: CharRepositoryImpl? = null

    fun loginToInflux(connectionString: String, token: String, org: String)
    {
        repository = CharRepositoryImpl(connectionString, token, org)
    }

    fun checkDBHealth(): String
    {
        repository?: return "Dead"
        return if (repository!!.checkHealth()) "Authorized" else "Error"
    }

    fun checkAuth(): Boolean
    {
        repository?: return false
        return repository!!.checkHealth()
    }

    fun logout()
    {
        repository = null
    }
}

@RestController
@RequestMapping("/api/user")
class UserController(val bucketsService: BucketsService)
{
    @GetMapping("/login/{token}")
    fun login(@PathVariable("token") token: String, @RequestParam("url") connectionUrl: String,
              @RequestParam("org") org: String): String
    {
        bucketsService.loginToInflux(connectionUrl, token, org)
        return bucketsService.checkDBHealth()
    }

    @GetMapping("/logout")
    fun logout(): String
    {
        bucketsService.logout()
        return "Done"
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