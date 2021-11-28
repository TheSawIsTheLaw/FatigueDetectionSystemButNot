package com.fdsystem.fdserver.controllers

import com.fdsystem.fdserver.controllers.components.JwtTokenUtil
import com.fdsystem.fdserver.controllers.services.DataService
import com.fdsystem.fdserver.domain.dtos.*
import com.fdsystem.fdserver.domain.response.ResponseMessage
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.mockito.Mockito
import java.time.Instant
import kotlin.RuntimeException

internal class DataControllerTest {
    private val dataServiceMock = Mockito.mock(DataService::class.java)
    private val jwtTokenUtilMock = Mockito.mock(JwtTokenUtil::class.java)

    private val controllerToTest = DataController(
        dataServiceMock,
        jwtTokenUtilMock
    )

    private data class MockExpectations(
        val pulseAndArterialList: List<MeasurementDTO> =
            listOf(
                MeasurementDTO(
                    "pulse", listOf(
                        MeasurementData("30", Instant.MIN)
                    )
                ), MeasurementDTO(
                    "arterialpressure", listOf(
                        MeasurementData("90", Instant.MIN)
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

    init {
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

        Mockito.doNothing().`when`(
            dataServiceMock
        ).sendMeasurements(
            "normTok", "normTok",
            AcceptMeasurementsListDTO(
                listOf(
                    AcceptMeasurementsDTO(
                        "pulse", listOf(
                            MeasurementDataWithoutTime("30")
                        )
                    ),
                    AcceptMeasurementsDTO(
                        "arterialpressure", listOf(
                            MeasurementDataWithoutTime("90")
                        )
                    )
                )
            )
        )

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

        Mockito.`when`(
            dataServiceMock.sendMeasurements(
                "serverExcCheck", "serverExcCheck",
                AcceptMeasurementsListDTO(
                    listOf()
                )
            )
        ).thenThrow(RuntimeException())
    }

    @Test
    fun getDataTestSuccessWithPassedAuthToken() {
        // Arrange
        val measurementsNames = listOf("pulse", "arterialpressure")
        val jwtToken = "Bearer normTok"

        // Act
        val responseBody = controllerToTest.getData(measurementsNames, jwtToken).body as ResponseMeasurementsDTO

        // Assert
        assertEquals(
            MeasurementDTO("pulse", listOf(MeasurementData("30", Instant.MIN))),
            responseBody.measurementsList[0]
        )
        assertEquals(
            MeasurementDTO("arterialpressure", listOf(MeasurementData("90", Instant.MIN))),
            responseBody.measurementsList[1]
        )
    }

    // This test is created for fun only. There is no way to reproduce it
    // in production
    @Test
    fun getDataTestFailureOnNoTokenProvidedOrInvalidForm() {
        // Arrange
        val measurementsNames = listOf<String>()
        val jwtToken = "lol"

        // Act

        // Assert
        assertThatExceptionOfType(RuntimeException::class.java).isThrownBy {
            controllerToTest.getData(
                measurementsNames,
                jwtToken
            )
        }
    }

    @Test
    fun getDataTestFailureOnDeadServer() {
        // Arrange
        val measurementsList = listOf<String>()
        val jwtToken = "Bearer serverExcCheck"

        // Act
        val response = controllerToTest.getData(measurementsList, jwtToken)

        // Assert
        assertEquals("Data server is dead :(", (response.body as ResponseMessage).message)
    }

    @Test
    fun addDataTestSuccess() {
        // Arrange
        val measurementsList = AcceptMeasurementsListDTO(
            listOf(
                AcceptMeasurementsDTO(
                    "pulse", listOf(
                        MeasurementDataWithoutTime("30")
                    )
                ),
                AcceptMeasurementsDTO(
                    "arterialpressure", listOf(
                        MeasurementDataWithoutTime("90")
                    )
                )
            )
        )
        val jwtToken = "Bearer normTok"

        // Act
        val response = controllerToTest.addData(measurementsList, jwtToken)

        // Assert
        assertEquals("Measurements were carefully sent", (response.body as ResponseMessage).message)
    }

    // This test is created for fun only. There is no way to reproduce it
    // in production
    @Test
    fun addDataTestFailureOnNoTokenProvidedOrInvalidForm() {
        // Arrange
        val measurementsList = AcceptMeasurementsListDTO(listOf())
        val jwtToken = "ololo"

        // Act

        // Assert
        assertThatExceptionOfType(RuntimeException::class.java).isThrownBy {
            controllerToTest.addData(
                measurementsList,
                jwtToken
            )
        }
    }

    @Test
    fun addDataTestFailureOnDeadServer() {
        // Arrange
        val measurementsList = AcceptMeasurementsListDTO(listOf())
        val jwtToken = "Bearer serverExcCheck"

        // Act
        val response = controllerToTest.addData(measurementsList, jwtToken)

        // Assert
        assertEquals("Data server is dead :(", (response.body as ResponseMessage).message)
    }
}