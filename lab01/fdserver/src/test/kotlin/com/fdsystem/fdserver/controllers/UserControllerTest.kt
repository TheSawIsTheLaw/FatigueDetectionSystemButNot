package com.fdsystem.fdserver.controllers

import com.fdsystem.fdserver.controllers.components.JwtTokenUtil
import com.fdsystem.fdserver.controllers.jwt.JwtResponse
import com.fdsystem.fdserver.controllers.services.JwtUserDetailsService
import com.fdsystem.fdserver.controllers.services.UserAuthService
import com.fdsystem.fdserver.domain.dtos.UserCredentialsDTO
import com.fdsystem.fdserver.domain.logicentities.DSUserCredentials
import com.fdsystem.fdserver.domain.response.ResponseMessage
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import java.lang.RuntimeException

internal class UserControllerTest
{
    private val userServiceMock = Mockito.mock(UserAuthService::class.java)

    private val jwtTokenUtilMock = Mockito.mock(JwtTokenUtil::class.java)

    private val userDetailsServiceMock =
        Mockito.mock(JwtUserDetailsService::class.java)

    private val controllerToTest = UserController(
        userServiceMock,
        jwtTokenUtilMock,
        userDetailsServiceMock
    )

    private data class MockExpectations(
        val successUser: UserDetails = User(
            "successUser",
            "pass",
            arrayListOf()
        )
    )

    private val mockExpectations = MockExpectations()

    private data class MockParameters(
        val successUser: UserCredentialsDTO = UserCredentialsDTO(
            "successUser",
            "pass"
        ),

        val internalErrorUser: UserCredentialsDTO = UserCredentialsDTO(
            "internalServerErrorUser",
            "pass"
        ),

        val alreadyExistsUser: UserCredentialsDTO = UserCredentialsDTO(
            "alreadyExistsUser",
            "pass"
        )
    )

    private val mockParameters = MockParameters()

    init
    {
        // Success test
        Mockito.`when`(userDetailsServiceMock.loadUserByUsername("successUser"))
            .thenReturn(mockExpectations.successUser)

        Mockito.`when`(
            jwtTokenUtilMock.generateToken(
                mockExpectations.successUser, "successToken"
            )
        ).thenReturn("successToken")

        Mockito.`when`(
            userServiceMock.getUserByUsername("successUser")
        ).thenReturn(DSUserCredentials("successUser", "pass", "successToken"))

        Mockito.`when`(
            userServiceMock.register(mockParameters.successUser)
        ).thenReturn("Success!")

        // Not found test
        Mockito.`when`(
            userDetailsServiceMock.loadUserByUsername("notFoundUser")
        ).thenThrow(
            UsernameNotFoundException("There is no user like this one...")
        )

        // Already exists
        Mockito.`when`(
            userServiceMock.register(mockParameters.alreadyExistsUser)
        ).thenReturn("User already exists")

        // Internal server error test
        Mockito.`when`(
            userDetailsServiceMock.loadUserByUsername("internalServerErrorUser")
        ).thenThrow(RuntimeException())

        Mockito.`when`(
            userServiceMock.register(mockParameters.internalErrorUser)
        ).thenThrow(RuntimeException())
    }

    @Test
    fun loginTestSuccess()
    {
        // Arrange
        val authenticationRequest = UserCredentialsDTO(
            "successUser",
            "pass"
        )

        // Act
        val response = controllerToTest.login(authenticationRequest)

        // Assert
        assert((response.body as JwtResponse).token == "successToken")
    }

    @Test
    fun loginTestFailureOnUsernameNotFound()
    {
        // Arrange
        val authenticationRequest = UserCredentialsDTO(
            "notFoundUser",
            "pass"
        )

        // Act
        val response = controllerToTest.login(authenticationRequest)

        // Assert
        assert((response.body as ResponseMessage).message == "User not found")
    }

    @Test
    fun loginTestFailureOnInternalServerError()
    {
        // Arrange
        val authenticationRequest = UserCredentialsDTO(
            "internalServerErrorUser",
            "pass"
        )

        // Act
        val response = controllerToTest.login(authenticationRequest)

        // Assert
        assert(
            (response.body as ResponseMessage).message == "Something " +
                    "terrible with Postgres..."
        )
    }

    @Test
    fun loginTestFailureOnInvalidPassword()
    {
        // Arrange
        val authenticationRequest = UserCredentialsDTO(
            "successUser",
            "InvalidPass"
        )

        // Act
        val response = controllerToTest.login(authenticationRequest)

        // Assert
        assert(
            (response.body as ResponseMessage).message ==
                    "User not found or invalid password"
        )
    }

    @Test
    fun registerTestSuccess()
    {
        // Arrange
        val user = UserCredentialsDTO(
            "successUser",
            "pass"
        )

        // Act
        val response = controllerToTest.register(user)

        // Assert
        assert((response.body as ResponseMessage).message == "Success!")
    }

    @Test
    fun registerTestFailureOnInternalServerError()
    {
        // Arrange
        val user = UserCredentialsDTO(
            "internalServerErrorUser",
            "pass"
        )

        // Act
        val response = controllerToTest.register(user)

        // Assert
        assert(
            (response.body as ResponseMessage).message == "Auth server is " +
                    "dead :("
        )
    }

    @Test
    fun registerTestFailureOnAlreadyExistingUser()
    {
        // Arrange
        val user = UserCredentialsDTO(
            "alreadyExistsUser",
            "pass"
        )

        // Act
        val response = controllerToTest.register(user)

        // Assert
        assert((response.body as ResponseMessage).message == "User already exists")
    }
}