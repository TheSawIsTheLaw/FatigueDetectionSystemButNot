package com.fdsystem.fdserver.controllers

import com.fdsystem.fdserver.controllers.components.JwtTokenUtil
import com.fdsystem.fdserver.controllers.services.DataService
import com.fdsystem.fdserver.domain.response.ResponseCreator
import com.fdsystem.fdserver.domain.response.ResponseMessage
import com.fdsystem.fdserver.domain.service.data.MeasurementWithTime
import com.fdsystem.fdserver.domain.service.data.MeasurementsToSend
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/data")
class DataController(
    val dataService: DataService,
    val jwtTokenUtil: JwtTokenUtil,
)
{
    @Operation(
        summary = "Gets info about the logged in user",
        description = "Gets necessary information from the user's bucket",
        tags = ["Data operations"],
        responses = [
            io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "Successful operation",
                content = [
                    Content(
                        schema = Schema(
                            implementation =
                            Array<MeasurementWithTime>::class
                        )
                    )
                ]
            ),
            io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "401",
                description = "Not authorized"
            ),
            io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "500",
                description = "Internal server error",
                content = [
                    Content(
                        schema = Schema(
                            implementation =
                            ResponseMessage::class
                        )
                    )]
            )
        ]
    )
    @GetMapping
    fun getData(
        @io.swagger.v3.oas.annotations.Parameter(
            description = "Names of measurements to get from bucket",
            required = true,
            content = [
                Content(
                    schema = Schema(
                        implementation =
                        Array<String>::class
                    )
                )
            ]
        )
        @RequestParam characteristicsNames: List<String>,
        @Parameter(
            description = "User JWToken",
            required = true
        )
        @CookieValue("FDSystemAuth") jwtToken: String
    ): ResponseEntity<*>
    {
        val outList: List<MeasurementWithTime>
        val userJwtToken = jwtToken.split(" ")[1].trim()

        val bucket = jwtTokenUtil.getUsernameFromToken(userJwtToken)
        val token =
            jwtTokenUtil.getAllClaimsFromToken(userJwtToken)["DBToken"].toString()

        try
        {
            outList =
                dataService.getMeasurements(
                    token, bucket,
                    characteristicsNames
                )
        }
        catch (exc: Exception)
        {
            return ResponseCreator.internalServerErrorResponse(
                "Data server is dead :(",
                "Let's dance on its grave!"
            )
        }

        return ResponseEntity(outList.toList(), HttpStatus.OK)
    }

    @Operation(
        summary = "Adds data to bucket",
        description = "Adds necessary information to the bucket",
        tags = ["Data operations"],
        responses = [
            io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "Successful operation",
                content = [
                    Content(
                        schema = Schema(
                            implementation =
                            ResponseMessage::class
                        )
                    )]
            ),
            io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "401",
                description = "Not authorized"
            ),
            io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "500",
                description = "Internal server error",
                content = [
                    Content(
                        schema = Schema(
                            implementation =
                            ResponseMessage::class
                        )
                    )]
            )
        ]
    )
    @PostMapping
    fun addData(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "List of measurements with values to send",
            required = true,
            content = [
                Content(
                    schema = Schema(
                        implementation =
                        Array<String>::class
                    )
                )]
        )
        @RequestBody charsList: MeasurementsToSend,
        @Parameter(
            description = "User JWToken",
            required = true
        )
        @CookieValue("FDSystemAuth") jwtToken: String
    ): ResponseEntity<*>
    {
        val userJwtToken = jwtToken.split(" ")[1].trim()

        val bucket = jwtTokenUtil.getUsernameFromToken(userJwtToken)
        val token =
            jwtTokenUtil.getAllClaimsFromToken(userJwtToken)["DBToken"].toString()

        try
        {
            dataService.sendMeasurements(token, bucket, charsList)
        }
        catch (exc: Exception)
        {
            return ResponseCreator.internalServerErrorResponse(
                "Data server is dead :(",
                "Let's dance on its grave!"
            )
        }

        return ResponseCreator.okResponse(
            "Measurements were carefully sent",
            "We know all about you now >:c"
        )
    }
}