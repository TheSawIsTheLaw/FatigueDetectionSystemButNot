package com.fdsystem.fdserver.controllers

import com.fdsystem.fdserver.controllers.components.JwtTokenUtil
import com.fdsystem.fdserver.controllers.services.DataService
import com.fdsystem.fdserver.controllers.services.JwtUserDetailsService
import com.fdsystem.fdserver.domain.*
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.web.bind.annotation.*
import java.security.Principal
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/api/v1/data")
class DataController(
    val dataService: DataService,
    val jwtTokenUtil: JwtTokenUtil,
)
{

    // Тут нужна ДТО для возврата. Получится лист дата классов со стрингом и инстантом, иначе очень плохо всё.
    @Operation(
        summary = "Gets info about user",
        description = "Gets necessary information from the bucket",
        tags = ["Data operations"],
        responses = [
            io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "Successful operation",
                content = [
                    Content(
                        schema = Schema(
                            implementation =
                            Array<Array<DataServiceMeasurement>>::class
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
                description = "Internal server error"
            )
        ]
    )
    @GetMapping()
    fun getData(
        @Parameter(
            description = "Name of measurement to get from bucket",
            required = true,
            example = "ArterialPressure"
        )
        @RequestParam("charnames") characteristicsNames: List<String>,
        request: HttpServletRequest
    ): ResponseEntity<*>
    {
        val outList: List<List<DataServiceMeasurement>>
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
                dataService.getMeasurements(token, bucket, characteristicsNames)
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
    @PostMapping()
    fun addData(
        @Parameter(
            description = "Names of measurements to add and it's values",
            required = true,
            example = "Pulse"
        )
        @RequestBody charsList: List<DataServiceMeasurements>,
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
            return ResponseEntity(null, HttpStatus.INTERNAL_SERVER_ERROR)
        }

        return ResponseEntity(null, HttpStatus.OK)
    }
}