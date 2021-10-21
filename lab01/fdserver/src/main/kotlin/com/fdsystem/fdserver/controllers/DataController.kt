package com.fdsystem.fdserver.controllers

import com.fdsystem.fdserver.controllers.services.FacadeService
import com.fdsystem.fdserver.domain.CharacteristicsGetDTO
import com.fdsystem.fdserver.domain.MeasurementDTO
import io.swagger.annotations.*
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.Instant

@RestController
@RequestMapping("/api/v1/data")
@Api(value = "dataApi", description = "Api for get from and send to buckets", tags = ["Data Retrieve and Send API"])
class DataController(val facadeService: FacadeService)
{
    // Тут нужна ДТО для возврата. Получится лист дата классов со стрингом и инстантом, иначе очень плохо всё.
    @Operation(summary = "Gets info about user", description = "Gets necessary information from the bucket")
    @ApiResponses(
        value = [
            ApiResponse(code = 200, message = "Success", response = CharacteristicsGetDTO::class),
            ApiResponse(code = 401, message = "Not authorized"),
            ApiResponse(code = 500, message = "Internal server error")
        ]
    )
    @GetMapping("/{bucket}")
    fun getData(
        @Parameter(description = "User's bucket to get info from", required = true, example = "Yuriy Stroganov")
        @PathVariable("bucket") bucket: String,
        @Parameter(
            description = "Name of measurement to get from bucket", required = true,
            example = "http://kidnappers.com/"
        )
        @RequestParam("charName") characteristicName: String
    ): ResponseEntity<*>
    {
        if (!facadeService.checkAuth())
            return ResponseEntity(null, HttpStatus.UNAUTHORIZED)

        val infoPairs: List<Pair<String, Instant>>
        try
        {
            infoPairs = facadeService.getMeasurementFromBucket(bucket, characteristicName)
        }
        catch (exc: Exception)
        {
            return ResponseEntity(null, HttpStatus.INTERNAL_SERVER_ERROR)
        }
        val outList = mutableListOf<MeasurementDTO>()
        for (measurement in infoPairs)
        {
            outList.add(0, MeasurementDTO(measurement.first, measurement.second))
        }

        return ResponseEntity(outList, HttpStatus.OK)
    }

    @Operation(summary = "Adds data to bucket", description = "Adds necessary information to the bucket")
    @ApiResponses(
        value = [
            ApiResponse(code = 200, message = "Success", response = CharacteristicsGetDTO::class),
            ApiResponse(code = 401, message = "Not authorized"),
            ApiResponse(code = 500, message = "Internal server error")
        ]
    )
    @PostMapping("/{bucket}")
    fun addData(
        @Parameter(description = "User's bucket to add info", required = true, example = "Yuriy Stroganov")
        @PathVariable("bucket") bucket: String,
        @Parameter(description = "Name of a measurement to add", required = true, example = "Pulse")
        @RequestParam("measname") characteristicName: String,
        @RequestBody charsList: List<String>
    ): ResponseEntity<*>
    {
        if (facadeService.checkAuth())
        {
            try
            {
                facadeService.sendToBucket(bucket, characteristicName, charsList)
            }
            catch (exc: Exception)
            {
                return ResponseEntity(null, HttpStatus.INTERNAL_SERVER_ERROR)
            }
            return ResponseEntity(null, HttpStatus.OK)
        }
        else
            return ResponseEntity(null, HttpStatus.UNAUTHORIZED)
    }
}