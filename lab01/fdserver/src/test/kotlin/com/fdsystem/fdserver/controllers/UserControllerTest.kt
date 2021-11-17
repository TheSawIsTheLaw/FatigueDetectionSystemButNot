package com.fdsystem.fdserver.controllers

import com.fdsystem.fdserver.controllers.components.JwtTokenUtil
import com.fdsystem.fdserver.controllers.jwt.JwtResponse
import com.fdsystem.fdserver.controllers.services.JwtUserDetailsService
import com.fdsystem.fdserver.controllers.services.UserAuthService
import com.fdsystem.fdserver.domain.dtos.NewPasswordDTO
import com.fdsystem.fdserver.domain.dtos.NewPasswordDTOWithUsername
import com.fdsystem.fdserver.domain.dtos.UserCredentialsDTO
import com.fdsystem.fdserver.domain.logicentities.DSUserCredentials
import com.fdsystem.fdserver.domain.response.ResponseMessage
import org.jetbrains.exposed.sql.Except
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
            "pass",
            ""
        ),

        val internalErrorUser: UserCredentialsDTO = UserCredentialsDTO(
            "internalServerErrorUser",
            "pass",
            ""
        ),

        val alreadyExistsUser: UserCredentialsDTO = UserCredentialsDTO(
            "alreadyExistsUser",
            "pass",
            ""
        ),

        val internalErrorPasswords: NewPasswordDTOWithUsername =
            NewPasswordDTOWithUsername(
                "internalServerErrorUser",
                "oldPas",
                "newPas"
            ),

        val incorrectPasswords: NewPasswordDTOWithUsername =
            NewPasswordDTOWithUsername(
                "incorrectPasUser",
                "incorrectOldPas",
                "newPas"
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
        ).thenReturn(UserCredentialsDTO("successUser", "pass", "successToken"))

        Mockito.`when`(
            userServiceMock.register(mockParameters.successUser)
        ).thenReturn("Success!")

        Mockito.`when`(jwtTokenUtilMock.getUsernameFromToken("totototo"))
            .thenReturn("successChangePasUser")

        Mockito.`when`(
            userServiceMock.changeUserInfo
                (
                NewPasswordDTOWithUsername(
                    "successChangePasUser",
                    "oldPas", "newPas"
                )
            )
        ).thenReturn(true)

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

        // Invalid password test
        Mockito.`when`(
            jwtTokenUtilMock.getUsernameFromToken("incorrectPasUser")
        ).thenReturn("incorrectPasUser")

        Mockito.`when`(
            userServiceMock.changeUserInfo(mockParameters.incorrectPasswords)
        ).thenReturn(false)

        // Internal server error test
        Mockito.`when`(
            userDetailsServiceMock.loadUserByUsername("internalServerErrorUser")
        ).thenThrow(RuntimeException())

        Mockito.`when`(
            userServiceMock.register(mockParameters.internalErrorUser)
        ).thenThrow(RuntimeException())

        Mockito.`when`(
            jwtTokenUtilMock.getUsernameFromToken("serverFail")
        ).thenReturn("internalServerErrorUser")

        Mockito.`when`(
            userServiceMock.changeUserInfo(
                mockParameters.internalErrorPasswords
            )
        ).thenThrow(RuntimeException())
    }

    @Test
    fun loginTestSuccess()
    {
        // Arrange
        val authenticationRequest = UserCredentialsDTO(
            "successUser",
            "pass",
            ""
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
            "pass",
            ""
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
            "pass",
            ""
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
            "InvalidPass",
            ""
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
            "pass",
            ""
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
            "pass",
            ""
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
            "pass",
            ""
        )

        // Act
        val response = controllerToTest.register(user)

        // Assert
        assert((response.body as ResponseMessage).message == "User already exists")
    }

    @Test
    fun changePasswordTestSuccess()
    {
        // Arrange
        val passwords = NewPasswordDTO(
            "oldPas", "newPas"
        )
        val jwtToken = "Bearer totototo"

        // Act
        val response = controllerToTest.changePassword(passwords, jwtToken)

        // Assert
        assert(
            (response.body as ResponseMessage).message == "Password " +
                    "changed successfully"
        )
    }

    // This test is created for fun only. There is no way to reproduce it
    // in production
    @Test
    fun changePasswordTestFailureOnInvalidJwtToken()
    {
        // Arrange
        val passwords = NewPasswordDTO("oldPas", "newPas")
        val jwtToken = "totototo"

        // Act
        val response = try
        {
            controllerToTest.changePassword(passwords, jwtToken)
        }
        catch (exc: Exception)
        {
            null
        }

        // Assert
        assert(response == null)
    }

    @Test
    fun changePasswordTestFailureOnInternalServerError()
    {
        // Arrange
        val passwords = NewPasswordDTO("oldPas", "newPas")
        val jwtToken = "Bearer serverFail"

        // Act
        val response = controllerToTest.changePassword(passwords, jwtToken)

        // Assert
        assert(
            (response.body as ResponseMessage).message == "Auth server is " +
                    "dead :("
        )
    }

    @Test
    fun changePasswordTestFailureOnNonMatchingPasswords()
    {
        // Arrange
        val passwords = NewPasswordDTO("IncorrectOldPas", "newPas")
        val jwtToken = "Bearer incorrectPasUser"

        // Act
        val response = controllerToTest.changePassword(passwords, jwtToken)

        // Assert
        assert(
            (response.body as ResponseMessage).message ==
                    "Password wasn't changed"
        )
    }
}