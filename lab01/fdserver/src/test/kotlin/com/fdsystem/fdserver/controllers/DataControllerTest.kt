package com.fdsystem.fdserver.controllers

import com.fdsystem.fdserver.controllers.components.JwtTokenUtil
import com.fdsystem.fdserver.controllers.services.DataService
import com.fdsystem.fdserver.domain.dtos.AcceptMeasurementsListDTO
import com.fdsystem.fdserver.domain.dtos.ResponseMeasurementsDTO
import com.fdsystem.fdserver.domain.response.ResponseMessage
import com.fdsystem.fdserver.expects.DataControllerExpectations
import com.fdsystem.fdserver.mothers.DataControllerOMother
import io.jsonwebtoken.Jwts
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito

internal class DataControllerTest {
    private val oMother = DataControllerOMother()
    private val expectations = DataControllerExpectations()

    private val dataServiceMock = Mockito.mock(DataService::class.java)
    private val jwtTokenUtilMock = Mockito.mock(JwtTokenUtil::class.java)

    private val controllerToTest = DataController(
        dataServiceMock,
        jwtTokenUtilMock
    )

    private fun prepareSuccessTokenValidationFixture() {
        Mockito.`when`(
            jwtTokenUtilMock.getUsernameFromToken(oMother.successToken)
        ).thenReturn(oMother.successToken)

        Mockito.`when`(
            jwtTokenUtilMock.getAllClaimsFromToken(oMother.successToken)
        ).thenReturn(Jwts.claims(mapOf<String, Any>("DBToken" to oMother.successToken)))
    }

    private fun prepareFailureInvalidTokenValidationFixture() {
        Mockito.`when`(
            jwtTokenUtilMock.getUsernameFromToken("serverExcCheck")
        ).thenReturn("serverExcCheck")

        Mockito.`when`(
            jwtTokenUtilMock.getAllClaimsFromToken("serverExcCheck")
        ).thenReturn(Jwts.claims(mapOf<String, Any>("DBToken" to oMother.exceptionToken)))
    }

    @Test
    fun getDataTestSuccessWithPassedAuthToken() {
        // Arrange
        val measurementsNames = oMother.pulseAndArterialMeasurementsList
        val jwtToken = "Bearer " + oMother.successToken

        prepareSuccessTokenValidationFixture()

        Mockito.`when`(
            dataServiceMock.getMeasurements(
                oMother.successToken, oMother.successToken, oMother.pulseAndArterialMeasurementsList
            )
        ).thenReturn(listOf(expectations.successGotPulseMeasurement, expectations.successGotArterialMeasurement))

        // Act
        val responseBody = controllerToTest.getData(measurementsNames, jwtToken).body as ResponseMeasurementsDTO

        // Assert
        assertEquals(
            expectations.successGotPulseMeasurement,
            responseBody.measurementsList[0]
        )
        assertEquals(
            expectations.successGotArterialMeasurement,
            responseBody.measurementsList[1]
        )
    }

    // This test is created for fun only. There is no way to reproduce it
    // in production
    @Test
    fun getDataTestFailureOnNoTokenProvidedOrInvalidForm() {
        // Arrange
        val measurementsNames = listOf<String>()
        val jwtToken = oMother.invalidToken

        prepareFailureInvalidTokenValidationFixture()

        Mockito.`when`(
            dataServiceMock.getMeasurements(
                oMother.invalidToken, oMother.invalidToken, listOf()
            )
        ).thenThrow(RuntimeException())

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
        val jwtToken = "Bearer " + oMother.exceptionToken

        prepareFailureInvalidTokenValidationFixture()

        Mockito.`when`(
            dataServiceMock.getMeasurements(oMother.exceptionToken, oMother.exceptionToken, measurementsList)
        ).thenThrow(RuntimeException())

        // Act
        val response = controllerToTest.getData(measurementsList, jwtToken)

        // Assert
        assertEquals(expectations.dataServerIsDeadMessage, (response.body as ResponseMessage).message)
    }

    @Test
    fun addDataTestSuccess() {
        // Arrange
        val measurementsList = oMother.addDataSuccessList
        val jwtToken = "Bearer " + oMother.successToken

        prepareSuccessTokenValidationFixture()

        Mockito.doNothing().`when`(dataServiceMock)
            .sendMeasurements(oMother.successToken, oMother.successToken, oMother.addDataSuccessList)

        // Act
        val response = controllerToTest.addData(measurementsList, jwtToken)

        // Assert
        assertEquals(expectations.measurementsWereCarefullySentMessage, (response.body as ResponseMessage).message)
    }

    // This test is created for fun only. There is no way to reproduce it
    // in production
    @Test
    fun addDataTestFailureOnNoTokenProvidedOrInvalidForm() {
        // Arrange
        val measurementsList = AcceptMeasurementsListDTO(listOf())
        val jwtToken = oMother.invalidToken

        // Act

        // Assert
        assertThatExceptionOfType(RuntimeException::class.java).isThrownBy {
            controllerToTest.addData(measurementsList, jwtToken)
        }
    }

    @Test
    fun addDataTestFailureOnDeadServer() {
        // Arrange
        val measurementsList = AcceptMeasurementsListDTO(listOf())
        val jwtToken = "Bearer " + oMother.exceptionToken

        prepareFailureInvalidTokenValidationFixture()

        Mockito.`when`(
            dataServiceMock.sendMeasurements(
                oMother.exceptionToken, oMother.exceptionToken,
                measurementsList
            )
        ).thenThrow(RuntimeException())

        // Act
        val response = controllerToTest.addData(measurementsList, jwtToken)

        // Assert
        assertEquals(expectations.dataServerIsDeadMessage, (response.body as ResponseMessage).message)
    }
}