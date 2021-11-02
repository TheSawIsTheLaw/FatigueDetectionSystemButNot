package com.fdsystem.fdserver.controllers.services

import com.fdsystem.fdserver.data.UserRepositoryImpl
import com.fdsystem.fdserver.domain.logicentities.USUserCredentials
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UsernameNotFoundException
import java.lang.RuntimeException

internal class JwtUserDetailsServiceTest
{
    private val userRepositoryImpl = Mockito.mock(
        UserRepositoryImpl::class.java
    )

    private val serviceToTest = JwtUserDetailsService(userRepositoryImpl)

    private data class MockExpectations(
        val existingUSUserCredentials: USUserCredentials =
            USUserCredentials("existingUser", "pass", "123")
    )

    private data class MockParameters(
        val existingUser: String = "existingUser",

        val notExistingUser: String = "notExistingUser",

        val exceptionUsername: String = "ecxUser",

        val usUserCredentialsForExistingUser: USUserCredentials =
            USUserCredentials(existingUser, "", ""),

        val usUserCredentialsForExceptionCheck: USUserCredentials =
            USUserCredentials(exceptionUsername, "", "")
    )

    private val mockExpectations = MockExpectations()
    private val mockParameters = MockParameters()

    init
    {
        Mockito.`when`(
            userRepositoryImpl.userExists(mockParameters.existingUser)
        ).thenReturn(true)

        Mockito.`when`(
            userRepositoryImpl.userExists(mockParameters.notExistingUser)
        ).thenReturn(false)

        Mockito.`when`(
            userRepositoryImpl.getUserByUsername(mockParameters.usUserCredentialsForExistingUser)
        ).thenReturn(mockExpectations.existingUSUserCredentials)

        Mockito.`when`(
            userRepositoryImpl.getUserByUsername(mockParameters.usUserCredentialsForExceptionCheck)
        ).thenThrow(RuntimeException("Internal server error"))
    }

    @Test
    fun successLoadForUser()
    {
        // Arrange
        val username = "existingUser"

        // Action
        val returnedUser = serviceToTest.loadUserByUsername(username)

        // Assert
        assert(returnedUser == User("existingUser", "pass", arrayListOf()))
    }

    @Test
    fun failureUserNotFound()
    {
        // Arrange
        val username = "notExistingUser"

        // Action
        val returnedUser = try
        {
            serviceToTest.loadUserByUsername(username)
        }
        catch (exc: UsernameNotFoundException)
        {
            null
        }

        // Assert
        assert(returnedUser == null)
    }

    @Test
    fun failureInternalError()
    {
        // Arrange
        val username = "ecxUser"

        // Action
        val returnedUser = try
        {
            serviceToTest.loadUserByUsername(username)
        }
        catch (exc: Exception)
        {
            null
        }

        // Assert
        assert(returnedUser == null)
    }
}