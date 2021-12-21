package com.fdsystem.fdserver.data

import com.fdsystem.fdserver.domain.logicentities.TokenInformation
import com.fdsystem.fdserver.domain.logicentities.USUserCredentials
import com.fdsystem.fdserver.expectations.CharRepoAndDAOExpectations
import com.fdsystem.fdserver.factories.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*

internal class CharRepoAndDAOIntegrationTest {
    private val repoFactory = CharRepositoryFactory()
    private val dsDataAccessInfoFactory = DSDataAccessInfoFactory()
    private val expectations = CharRepoAndDAOExpectations()
    private val dsDataAddInfoFactory = DSDataAddInfoFactory()
    private val usUserCredentialsFactory = USUserCredentialsFactory()
    private val tokenInformationFactory = TokenInformationFactory()

    private val repository = repoFactory.getCharRepository()

    @Test
    fun getTest() {
        // Arrange
        val expectedGotMeasurements = expectations.pulseListAtCreation
        val requiredMeasurement = dsDataAccessInfoFactory.createDSDataAccessInfoByMeasurementName("pulse")

        // Act
        val gotMeasurements = repository.get(requiredMeasurement)

        // Assert
        for (i in gotMeasurements.indices) {
            assertEquals(gotMeasurements[i].name, expectedGotMeasurements[i].name)
            assertEquals(gotMeasurements[i].value, expectedGotMeasurements[i].value)
        }
    }

    @Test
    fun sendTest() {
        // Arrange
        val expectedGotMeasurements = expectations.pulseListWithAddedValue
        val newValue = dsDataAddInfoFactory.createDataAddInfoWithNewPulseZero()

        // For check in assert
        val requiredMeasurement = dsDataAccessInfoFactory.createDSDataAccessInfoByMeasurementName("pulse")

        // Act
        repository.add(newValue)

        // Assert
        val gotMeasurements = repository.get(requiredMeasurement)
        for (i in gotMeasurements.indices) {
            assertEquals(gotMeasurements[i].name, expectedGotMeasurements[i].name)
            assertEquals(gotMeasurements[i].value, expectedGotMeasurements[i].value)
        }
    }

    // https://youtrack.jetbrains.com/issue/KT-17630
    // Пришлось сделать метод публичным, штош.
    // https://youtrack.jetbrains.com/issue/KTIJ-17598
    @Test
    fun createNewUserTokenTest() {
        // Arrange
        val userCredentials = usUserCredentialsFactory.createAnyWithoutToken()

        // Act
        val newToken = repository.getNewTokenForUser(userCredentials)

        // Assert
        assertFalse(repository.deleteToken(TokenInformation(newToken.token, newToken.tokenID)))
    }

    @Test
    fun deleteTokenTestSuccess() {
        // Arrange
        val userTokenInfo = repository.getNewTokenForUser(usUserCredentialsFactory.createAnyWithoutToken())

        // Act
        val notDeleted = repository.deleteToken(userTokenInfo)

        // Assert
        assertFalse(notDeleted)
    }

    @Test
    fun deleteTokenTestFailure() {
        // Arrange
        val nonExistingUserTokenInfo = tokenInformationFactory.createNonExistingTokenInformation()

        // Act
        val notDeleted = repository.deleteToken(nonExistingUserTokenInfo)

        // Assert
        assertTrue(notDeleted)
    }
}