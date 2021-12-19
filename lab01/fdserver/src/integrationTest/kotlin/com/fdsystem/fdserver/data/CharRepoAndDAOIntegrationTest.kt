package com.fdsystem.fdserver.data

import com.fdsystem.fdserver.expectations.CharRepoAndDAOExpectations
import com.fdsystem.fdserver.factories.CharRepositoryFactory
import com.fdsystem.fdserver.factories.DSDataAccessInfoFactory
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class CharRepoAndDAOIntegrationTest {
    private val repoFactory = CharRepositoryFactory()
    private val dsDataAccessInfoFactory = DSDataAccessInfoFactory()
    private val expectations = CharRepoAndDAOExpectations()

    @Test
    fun getTestSuccess() {
        // Arrange
        val repository = repoFactory.getCharRepository()
        val expectedGotMeasurements = expectations.pulseListAtCreation

        // Act
        val gotMeasurements = repository.get(dsDataAccessInfoFactory.createDSDataAccessInfoByMeasurementName("pulse"))

        // Assert
        for (i in gotMeasurements.indices) {
            assertEquals(gotMeasurements[i].name, expectedGotMeasurements[i].name)
            assertEquals(gotMeasurements[i].value, expectedGotMeasurements[i].value)
        }
    }
}