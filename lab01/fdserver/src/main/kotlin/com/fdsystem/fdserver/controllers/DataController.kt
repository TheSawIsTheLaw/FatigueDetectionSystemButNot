package com.fdsystem.fdserver.controllers

import com.fdsystem.fdserver.controllers.components.JwtTokenUtil
import com.fdsystem.fdserver.controllers.services.DataService
import com.fdsystem.fdserver.domain.service.data.DataServiceMeasurement
import com.fdsystem.fdserver.domain.service.data.MeasurementWithTime
import com.fdsystem.fdserver.domain.service.data.MeasurementsToSend
import com.fdsystem.fdserver.domain.service.data.RequiredMeasurementsNames
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import org.apache.commons.logging.LogFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.logging.Logger
import javax.servlet.http.HttpServletRequest

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
                responseCode = "405",
                description = "Not authorized"
            ),
            io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "500",
                description = "Internal server error"
            )
        ]
    )
    // Можно попробовать реквестить хедер и сразу из него брать токен
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
        request: HttpServletRequest
    ): ResponseEntity<*>
    {
        val outList: List<MeasurementWithTime>
        val userJwtToken =
            request.getHeader("Authorization")
                .split(" ")[1]
                .trim()

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
            return ResponseEntity(null, HttpStatus.INTERNAL_SERVER_ERROR)
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
                description = "Successful operation"
            ),
            io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "401",
                description = "Not authorized"
            ),
            io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "500",
                description = "Internal server error"
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
        request: HttpServletRequest
    ): ResponseEntity<*>
    {
        val userJwtToken =
            request.getHeader("Authorization")
                .split(" ")[1]
                .trim()

        val bucket = jwtTokenUtil.getUsernameFromToken(userJwtToken)
        val token =
            jwtTokenUtil.getAllClaimsFromToken(userJwtToken)["DBToken"].toString()

        try
        {
            dataService.sendMeasurements(token, bucket, charsList)
        }
        catch (exc: Exception)
        {
            LogFactory.getLog(javaClass).error(exc.message)
            return ResponseEntity(null, HttpStatus.INTERNAL_SERVER_ERROR)
        }

        return ResponseEntity(null, HttpStatus.OK)
    }
}