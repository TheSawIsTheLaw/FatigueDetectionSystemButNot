package com.fdsystem.fdserver.controller

import com.fdsystem.fdserver.domain.dtos.ResponseMeasurementsDTO
import com.fdsystem.fdserver.expectations.DataControllerExpectations
import com.fdsystem.fdserver.factories.DataControllerFactory
import com.fdsystem.fdserver.factories.JwtTokenFactory
import com.fdsystem.fdserver.factories.MeasurementsListFactory
import com.fdsystem.fdserver.factories.USUserCredentialsFactory
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class DataControllerIntegrationTest {
    private val controllerFactory = DataControllerFactory()
    private val measurementsListFactory = MeasurementsListFactory()
    private val jwtTokenFactory = JwtTokenFactory()
    private val usUserCredentialsFactory = USUserCredentialsFactory()

    private val expectations = DataControllerExpectations()

    private val controller = controllerFactory.getDataController()

    @Test
    fun getDataTest() {
        // Arrange
        val measurementsList = measurementsListFactory.getMeasurementsListWithBotArterialPressure()
        val userJwtToken = jwtTokenFactory.createTokenFromUser(usUserCredentialsFactory.getExistingUser())

        val responseMeasurementToCheck = expectations.responseMeasurementsDTOWithArterial

        // Act
        val gotMeasurements = (controller.getData(
            measurementsList,
            userJwtToken
        ).body as ResponseMeasurementsDTO).measurementsList.first()

        // Assert
        assertEquals(
            gotMeasurements.measurement,
            responseMeasurementToCheck.measurementsList.first().measurement
        )

        for (i in gotMeasurements.values.indices) {
            assertEquals(
                gotMeasurements.values[i].value,
                responseMeasurementToCheck.measurementsList.first().values[i].value
            )
        }
    }
}