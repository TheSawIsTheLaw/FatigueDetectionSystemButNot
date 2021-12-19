package com.fdsystem.fdserver.data

import com.fdsystem.fdserver.domain.logicentities.DSDataAccessInfo
import com.fdsystem.fdserver.expectations.CharRepoAndDAOExpectations
import com.fdsystem.fdserver.factories.CharRepositoryFactory
import org.apache.commons.logging.LogFactory
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class CharRepoAndDAOIntegrationTest {
    val repoFactory = CharRepositoryFactory()
    val expectations = CharRepoAndDAOExpectations()

//    val repositoryToTest = CharRepositoryImpl(configuration)

    @Test
    fun getTestSuccess() {
        // Arrange
        val repository = repoFactory.getCharRepository()
        val expectedGotMeasurements = expectations.pulseListAtCreation

        // Act
        val gotMeasurements = repository.get(
            DSDataAccessInfo(
                "HsJBf0sINtvxedXJio2Lg7iskJgLcR5q8a0MZtqoiWZt66pBEQ0LUz0IPEe5ooD2GqaxQoGxzqoIi-U1CLINow==",
                "testUser",
                Pair(0, 0),
                "pulse"
            )
        )

        // Assert
        for (i in gotMeasurements.indices) {
            assertEquals(gotMeasurements[i].name, expectedGotMeasurements[i].name)
            assertEquals(gotMeasurements[i].value, expectedGotMeasurements[i].value)
        }
    }
}