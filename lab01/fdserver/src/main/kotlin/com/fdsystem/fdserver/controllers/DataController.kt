package com.fdsystem.fdserver.controllers

import com.fdsystem.fdserver.controllers.services.FacadeService
import com.fdsystem.fdserver.domain.MeasurementDTO
import io.swagger.annotations.*
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/data")
@Api(value = "dataApi", description = "Api for get from and send to buckets", tags = ["Data Retrieve and Send API"])
class DataController(val facadeService: FacadeService)
{
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
    // Нормальные респонсы
    @GetMapping("/{bucket}")
    fun getData(@PathVariable("bucket") bucket: String,
                @RequestParam("charname") characteristicName: String): List<MeasurementDTO>
    {
        if (!facadeService.checkAuth())
            throw Exception("Not authorized")

        val infoPairs = facadeService.getMeasurementFromBucket(bucket, characteristicName)
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
    @PostMapping("/{bucket}")
    fun addData(@PathVariable("bucket") bucket: String,
                @RequestParam("charname") characteristicName: String,
                @RequestBody charsList: List<String>): String
    {
        return if (facadeService.checkAuth())
        {
            facadeService.sendToBucket(bucket, characteristicName, charsList)
            "Done"
        }
        else
            "Not authorized"
    }
}