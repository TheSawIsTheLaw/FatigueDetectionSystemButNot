package com.fdsystem.fdserver.data

import com.fdsystem.fdserver.factories.USUserCredentialsFactory
import com.fdsystem.fdserver.factories.UserRepositoryFactory
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class UserRepoAndDAOIntegrationTest {
    private val repoFactory = UserRepositoryFactory()
    private val usUserCredentialsFactory = USUserCredentialsFactory()

    private val repository = repoFactory.getUserRepository()

    @Test
    fun getUserByUsernameSuccessTest() {
        // Arrange
        val existingUser = usUserCredentialsFactory.getExistingUser()

        // Act
        val foundUser = repository.getUserByUsername(existingUser)

        // Assert
        assertEquals(existingUser, foundUser)
    }

    @Test
    fun getUserByUsernameFailureTest() {
        // Arrange
        val nonExistingUser = usUserCredentialsFactory.createAnyWithoutToken()

        // Act
        val foundUser = repository.getUserByUsername(nonExistingUser)

        // Assert
        assertNotEquals(nonExistingUser, foundUser)
    }

    @Test
    fun userExistsSuccessTest() {
        // Arrange
        val existingUser = usUserCredentialsFactory.getExistingUser()

        // Act
        val exists = repository.userExists(existingUser.username)

        // Assert
        assertTrue(exists)
    }

    @Test
    fun userExistsFailureTest() {
        // Arrange
        val nonExistingUser = usUserCredentialsFactory.createAnyWithoutToken()

        // Act
        val exists = repository.userExists(nonExistingUser.username)

        // Assert
        assertFalse(exists)
    }

    @Test
    fun checkPasswordSuccessTest() {
        // Arrange
        val existingUser = usUserCredentialsFactory.getExistingUser()

        // Act
        val isCorrect = repository.checkPassword(existingUser)

        // Assert
        assertTrue(isCorrect)
    }

    @Test
    fun checkPasswordFailureTest() {
        // Arrange
        val existingUserWithInvalidPassword = usUserCredentialsFactory.getExistingUserWithInvalidPassword()

        // Act
        val isCorrect = repository.checkPassword(existingUserWithInvalidPassword)

        // Assert
        assertFalse(isCorrect)
    }

    @Test
    fun getUserTokenSuccessTest() {
        // Arrange
        val existingUser = usUserCredentialsFactory.getExistingUser()

        // Act
        val gotUser = repository.getUserToken(existingUser)

        // Assert
        assertEquals(gotUser, existingUser)
    }

    @Test
    fun getUserTokenFailureOnInvalidPasswordTest() {
        // Arrange
        val existingUserWithInvalidPassword = usUserCredentialsFactory.getExistingUserWithInvalidPassword()
        val emptyUser = usUserCredentialsFactory.createEmptyUser()

        // Act
        val gotUser = repository.getUserToken(existingUserWithInvalidPassword)

        // Assert
        assertEquals(gotUser, emptyUser)
    }

    @Test
    fun getUserTokenFailureOnNonExistingUserTest() {
        // Arrange
        val nonExistingUser = usUserCredentialsFactory.createNonExistingUser()
        val emptyUser = usUserCredentialsFactory.createEmptyUser()

        // Act
        val gotUser = repository.getUserToken(nonExistingUser)

        // Assert
        assertEquals(gotUser, emptyUser)
    }

    @Test
    fun registerUserSuccessTest() {
        // Arrange
        val newUser = usUserCredentialsFactory.createNewUser()

        // Act
        val userWasRegistered = repository.registerUser(newUser)

        // Assert
        assertTrue(userWasRegistered)
        assertEquals(newUser, repository.getUserByUsername(newUser))
    }

    @Test
    fun registerUserFailureOnExistingUserTest() {
        // Arrange
        val existingUser = usUserCredentialsFactory.getExistingUser()

        // Act
        val userWasRegistered = repository.registerUser(existingUser)

        // Assert
        assertFalse(userWasRegistered)
    }

    @Test
    fun changePasswordAndUsernameSuccessTest() {
        // Arrange
        val newUser = usUserCredentialsFactory.createNewUserForPasswordChange()
        repository.registerUser(newUser)

        val credentialsForChange = usUserCredentialsFactory.getCredentialsForChange()

        val changedUser = usUserCredentialsFactory.getChangedUser()

        // Act
        repository.changePasswordAndUsername(credentialsForChange)

        // Assert
        val currentUser = repository.getUserByUsername(changedUser)

        assertEquals(changedUser, currentUser)
    }

    @Test
    fun changePasswordAndUsernameFailureTest() {
        // Arrange
        val existingUser = usUserCredentialsFactory.getExistingUser()

        val credentialsForChange = usUserCredentialsFactory.getCredentialsForFailureChangeExistingUser()

        // Act
        repository.changePasswordAndUsername(credentialsForChange)

        // Assert
        val currentUser = repository.getUserByUsername(existingUser)

        assertEquals(existingUser, currentUser)
    }
}