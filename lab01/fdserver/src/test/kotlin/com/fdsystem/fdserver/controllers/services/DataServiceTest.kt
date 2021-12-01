package com.fdsystem.fdserver.controllers.services

import com.fdsystem.fdserver.data.CharRepositoryImpl
import com.fdsystem.fdserver.domain.dtos.*
import com.fdsystem.fdserver.domain.logicentities.DSDataAccessInfo
import com.fdsystem.fdserver.domain.logicentities.DSDataAddInfo
import com.fdsystem.fdserver.domain.logicentities.DSMeasurement
import com.fdsystem.fdserver.domain.logicentities.DSMeasurementList
import com.fdsystem.fdserver.expects.DataServiceExpectations
import com.fdsystem.fdserver.expects.mocks.DataServiceMocksExpectations
import com.fdsystem.fdserver.mothers.DataServiceOMother
import org.apache.commons.logging.LogFactory
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThatNoException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

import org.mockito.Mockito
import java.time.Instant

internal class DataServiceTest {
    private val oMother = DataServiceOMother()
    private val expectations = DataServiceExpectations()
    private val mockExpectations = DataServiceMocksExpectations()

    private val charRepositoryMock: CharRepositoryImpl =
        Mockito.mock(CharRepositoryImpl::class.java)

    private val serviceToTest = DataService(charRepositoryMock)

    private fun pulseGetMeasurementPreparationFixture() {
        Mockito.`when`(
            charRepositoryMock.get(oMother.pulseAccessInfo)
        ).thenReturn(mockExpectations.charGetPulseExample)
    }

    @Test
    fun getMeasurementsWithSeveralElementsReturned() {
        // Arrange
        // Prepare parameters
        val token = oMother.defaultToken
        val bucketName = oMother.defaultBucket
        val requiredNames = oMother.defaultRequiredNames

        pulseGetMeasurementPreparationFixture()

        Mockito.`when`(
            charRepositoryMock.get(oMother.arterialAccessInfo)
        ).thenReturn(mockExpectations.charGetArterialExample)

        // Act
        val returnedMeasurements =
            serviceToTest.getMeasurements(token, bucketName, requiredNames)

        // Assert
        assertEquals(
            listOf(expectations.pulseMeasurementDTO, expectations.arterialMeasurementDTO),
            returnedMeasurements
        )
    }

    @Test
    fun getMeasurementsWithOneElementReturned() {
        // Arrange
        // Prepare parameters
        val token = oMother.defaultToken
        val bucketName = oMother.defaultBucket
        val requiredNames = listOf(oMother.defaultRequiredNames[0])

        pulseGetMeasurementPreparationFixture()

        // Act
        val returnedMeasurements =
            serviceToTest.getMeasurements(token, bucketName, requiredNames)

        // Assert
        assertEquals(listOf(expectations.pulseMeasurementDTO), returnedMeasurements)
    }

    @Test
    fun getMeasurementsWithNoElementsReturned() {
        // Arrange
        // Prepare parameters
        val token = ""
        val bucketName = ""
        val requiredNames = listOf<String>()

        Mockito.`when`(
            charRepositoryMock.get(
                DSDataAccessInfo(
                    "",
                    "",
                    Pair(0, 0),
                    ""
                )
            )
        ).thenReturn(listOf())

        // Act
        val returnedMeasurements =
            serviceToTest.getMeasurements(token, bucketName, requiredNames)

        // Assert
        assertEquals(listOf<DSMeasurementList>(), returnedMeasurements)
    }

    @Test
    fun sendMeasurementsTestToCheckNoException() {
        // Arrange
        val token = oMother.defaultToken
        val bucketName = oMother.defaultBucket
        val measurementList = AcceptMeasurementsListDTO(
            listOf(oMother.acceptPulseMeasurements, oMother.acceptArterialMeasurements)
        )

        Mockito.doNothing().`when`(charRepositoryMock).add(oMother.arterialDataAddExample)

        Mockito.doNothing().`when`(charRepositoryMock).add(oMother.pulseDataAddExample)

        // Act
        serviceToTest.sendMeasurements(token, bucketName, measurementList)

        // Assert
        assertThatNoException()
    }
}