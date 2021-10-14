package com.fdsystem.fdserver.controller

import com.fdsystem.fdserver.data.CharRepositoryImpl
import com.fdsystem.fdserver.domain.MeasurementDTO
import com.google.gson.Gson
import io.swagger.annotations.*
import org.springframework.data.annotation.Id
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.*
import springfox.documentation.annotations.ApiIgnore
import java.time.Instant

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
        repository ?: return "Dead"
        return if (repository!!.checkHealth()) "Authorized" else "Error"
    }

    fun checkAuth(): Boolean
    {
        repository ?: return false
        return repository!!.checkHealth()
    }

    fun logout()
    {
        repository = null
    }

    fun getMeasurementFromBucket(bucketName: String, charName: String): List<Pair<String, Instant>>
    {
        val outList = mutableListOf<Pair<String, Instant>>()
        if (repository != null)
        {
            val gotInformation = repository!!.get(bucketName, Pair(0, 0), charName)
            for (i in gotInformation)
            {
                outList.add(0, Pair(i.second.toString(), i.third))
            }
        }

        return outList.toList()
    }

    fun sendToBucket(bucketName: String, charName: String, chars: List<String>)
    {
        if (repository == null)
            return

        repository!!.add(bucketName, charName, chars)
    }
}

@RestController
@RequestMapping("/api/user")
@Api(value = "userApi", description = "Api for users of a system", tags = ["User API"])
class UserController(val bucketsService: BucketsService)
{
    @ApiOperation(value = "Logs in DB user", response = String::class)
    @ApiParam(name = "token", value = "Token for DB access", required = true, example = "OAOAOOAOAAOMMMMMMM")
    @ApiImplicitParams(*[
        ApiImplicitParam(name = "url", value = "URL for DB access", required = true, example = "http://kidnappers.com/",
            type = "String"),
        ApiImplicitParam(name = "org", value = "User's organisation for DB access", required = true,
            example = "Kidnappers")
    ])
    @ApiResponses(
        value = [
            ApiResponse(code = 200, message = "Success"),
            ApiResponse(code = 401, message = "Illegal token, address or organization")
        ]
    )
    @GetMapping("/login/{token}")
    fun login(@PathVariable("token") token: String, @RequestParam("url") connectionUrl: String,
              @RequestParam("org") org: String): String
    {
        bucketsService.loginToInflux(connectionUrl, token, org)
        return bucketsService.checkDBHealth()
    }

    @ApiOperation(value = "Logs out DB user", response = String::class)
    @ApiResponse(code = 200, message = "Success")
    @GetMapping("/logout")
    fun logout(): String
    {
        bucketsService.logout()
        return "Done"
    }

    // Тут нужна ДТО для возврата. Получится лист дата классов со стрингом и инстантом, иначе очень плохо всё.
    @ApiOperation(value = "Gets info about user", response = MeasurementDTO::class)
    @ApiParam(name = "bucket", value = "User's bucket to get info from", required = true, example = "Yuriy Stroganov")
    @ApiImplicitParam(name = "charname", value = "Name of measurement to get from bucket", required = true,
        example = "http://kidnappers.com/",
        type = "String")
    @ApiResponses(
        value = [
            ApiResponse(code = 200, message = "Success"),
            ApiResponse(code = 401, message = "Not authorized")
        ]
    )
    @GetMapping("/data/{bucket}")
    fun getData(@PathVariable("bucket") bucket: String,
                @RequestParam("charname") characteristicName: String): List<MeasurementDTO>
    {
        if (!bucketsService.checkAuth())
            throw Exception("Not authorized")

        val infoPairs = bucketsService.getMeasurementFromBucket(bucket, characteristicName)
        val outList = mutableListOf<MeasurementDTO>()
        for (measurement in infoPairs)
        {
            outList.add(0, MeasurementDTO(measurement.first, measurement.second))
        }

        return outList
    }

    @ApiOperation(value = "Adds data to bucket", response = String::class)
    @ApiParam(name = "bucket",
        type = "string",
        value = "Name of a bucket to add measurement. If bucket doesn't exist - it will be created",
        example = "Stroganov Yuriy",
        required = true)
    @ApiImplicitParam(name = "charname", value = "Name of a measurement to add", required = true,
        example = "pulse")
    @ApiResponses(
        value = [
            ApiResponse(code = 200, message = "Success"),
            ApiResponse(code = 401, message = "Not authorized")
        ]
    )
    @PostMapping("/data/{bucket}")
    fun addData(@PathVariable("bucket") bucket: String, @RequestParam("charname") characteristicName: String,
                @RequestBody charsList: List<String>): String
    {
        return if (bucketsService.checkAuth())
        {
            bucketsService.sendToBucket(bucket, characteristicName, charsList)
            "Done"
        }
        else
            "Not authorized"
    }
}