package com.fdsystem.fdserver.controllers

import com.fdsystem.fdserver.controllers.components.JwtTokenUtil
import com.fdsystem.fdserver.controllers.services.DataService
import com.fdsystem.fdserver.domain.dtos.MeasurementDTO
import com.fdsystem.fdserver.domain.dtos.MeasurementData
import com.fdsystem.fdserver.domain.dtos.ResponseMeasurementsDTO
import com.fdsystem.fdserver.domain.logicentities.DSMeasurement
import com.fdsystem.fdserver.domain.logicentities.DSMeasurementList
import com.fdsystem.fdserver.domain.response.ResponseMessage
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.mockito.Mockito
import org.springframework.http.HttpStatus
import java.lang.RuntimeException
import java.time.Instant

internal class DataControllerTest
{
    private val dataServiceMock = Mockito.mock(DataService::class.java)
    private val jwtTokenUtilMock = Mockito.mock(JwtTokenUtil::class.java)

    private val controllerToTest = DataController(
        dataServiceMock,
        jwtTokenUtilMock
    )

    private data class MockExpectations(
        val pulseAndArterialList: List<DSMeasurementList> =
            listOf(
                DSMeasurementList(
                    "pulse", listOf(
                        DSMeasurement("pulse", "30", Instant.MIN)
                    )
                ), DSMeasurementList(
                    "arterialpressure", listOf(
                        DSMeasurement("arterialpressure", "90", Instant.MIN)
                    )
                )
            )
    )

    private val mockExpectations = MockExpectations()

    private data class MockParameters(
        val claimsWithNormalToken: Claims = Jwts.claims(
            mapOf<String, Any>("DBToken" to "normTok")
        ),

        val claimsForServerCheck: Claims = Jwts.claims(
            mapOf<String, Any>("DBToken" to "serverExcCheck")
        )
    )

    private val mockParameters = MockParameters()

    init
    {
        // For normal token
        Mockito.`when`(
            jwtTokenUtilMock.getUsernameFromToken("normTok")
        ).thenReturn("normTok")

        Mockito.`when`(
            jwtTokenUtilMock.getAllClaimsFromToken("normTok")
        ).thenReturn(mockParameters.claimsWithNormalToken)

        Mockito.`when`(
            dataServiceMock.getMeasurements(
                "normTok", "normTok", listOf("pulse", "arterialpressure")
            )
        ).thenReturn(mockExpectations.pulseAndArterialList)

        // For dead server
        Mockito.`when`(
            jwtTokenUtilMock.getUsernameFromToken("serverExcCheck")
        ).thenReturn("serverExcCheck")

        Mockito.`when`(
            jwtTokenUtilMock.getAllClaimsFromToken("serverExcCheck")
        ).thenReturn(mockParameters.claimsForServerCheck)

        Mockito.`when`(
            dataServiceMock.getMeasurements(
                "serverExcCheck", "serverExcCheck", listOf()
            )
        ).thenThrow(RuntimeException())
    }

    @Test
    fun getDataTestSuccessWithPassedAuthToken()
    {
        // Arrange
        val measurementsNames = listOf("pulse", "arterialpressure")
        val jwtToken = "Bearer normTok"

        // Act
        val response = controllerToTest.getData(measurementsNames, jwtToken)

        // Assert
        assert(
            (response.body as ResponseMeasurementsDTO) == ResponseMeasurementsDTO(
                listOf(
                    MeasurementDTO(
                        "pulse",
                        listOf(MeasurementData("30", Instant.MIN))
                    ), MeasurementDTO(
                        "arterialpressure",
                        listOf(MeasurementData("90", Instant.MIN))
                    )
                )
            )
        )

    }

    // This test is created for fun only. There is no way to reproduce it
    // in production
    @Test
    fun getDataTestFailureOnNoTokenProvidedOrInvalidForm()
    {
        // Arrange
        val measurementsNames = listOf<String>()
        val jwtToken = "lol"

        // Act
        val output = try
        {
            controllerToTest.getData(measurementsNames, jwtToken)
        }
        catch (exc: Exception)
        {
            null
        }

        // Assert
        assert(output == null)
    }

    @Test
    fun getDataTestFailureOnDeadServer()
    {
        // Arrange
        val measurementsList = listOf<String>()
        val jwtToken = "Bearer serverExcCheck"

        // Act
        val response = controllerToTest.getData(measurementsList, jwtToken)

        // Assert
        assert((response.body as ResponseMessage).message == "Data server is dead :(")
    }

    @Test
    fun addData()
    {
    }
}