package com.fdsystem.fdserver.controllers

import com.fdsystem.fdserver.controllers.services.FacadeService
import com.fdsystem.fdserver.domain.*
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/data")
class DataController(val facadeService: FacadeService)
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
                    Content(schema = Schema(implementation = Array<MeasurementList>::class))
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
    @GetMapping("/{bucket}")
    fun getData(
        @Parameter(description = "User's bucket to get info from", required = true, example = "Yuriy Stroganov")
        @PathVariable("bucket") bucket: String,
        @Parameter(
            description = "Name of measurement to get from bucket", required = true,
            example = "ArterialPressure"
        )
        @RequestParam("charnames") characteristicsNames: List<String>
    ): ResponseEntity<*>
    {
        if (!facadeService.checkAuth())
            return ResponseEntity(null, HttpStatus.UNAUTHORIZED)

        val outList: MutableList<MeasurementList> = mutableListOf()
        try
        {
            // После этих слов в украинском поезде начался сущий кошмар
            for (curChar in characteristicsNames)
            {
                val addList = facadeService.getMeasurementFromBucket(bucket, curChar)
                val convertedList: MutableList<MeasurementDTO> = mutableListOf()
                for (i in addList)
                {
                    convertedList.add(MeasurementDTO(i.first, i.second))
                }
                outList.add(MeasurementList(curChar, convertedList))
            }
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
    @PostMapping("/{bucket}")
    fun addData(
        @Parameter(description = "User's bucket to add info", required = true, example = "Yuriy Stroganov")
        @PathVariable("bucket") bucket: String,
        @Parameter(description = "Names of measurements to add and it's values", required = true, example = "Pulse")
        @RequestBody charsList: List<MeasurementListLight>
    ): ResponseEntity<*>
    {
        if (!facadeService.checkAuth())
        {
            return ResponseEntity(null, HttpStatus.UNAUTHORIZED)
        }

        try
        {
            for (curChar in charsList)
            {
                facadeService.sendToBucket(bucket, curChar.measurementName, curChar.measurements)
            }
        }
        catch (exc: Exception)
        {
            return ResponseEntity(null, HttpStatus.INTERNAL_SERVER_ERROR)
        }

        return ResponseEntity(null, HttpStatus.OK)
    }
}