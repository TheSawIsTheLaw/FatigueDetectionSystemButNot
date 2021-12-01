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
import com.fdsystem.fdserver.expects.UserControllerExpectations
import com.fdsystem.fdserver.mothers.UserControllerOMother
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.jetbrains.exposed.sql.Except
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import kotlin.RuntimeException

internal class UserControllerTest {
    private val oMother = UserControllerOMother()
    private val expectations = UserControllerExpectations()

    private val userServiceMock = Mockito.mock(UserAuthService::class.java)

    private val jwtTokenUtilMock = Mockito.mock(JwtTokenUtil::class.java)

    private val userDetailsServiceMock =
        Mockito.mock(JwtUserDetailsService::class.java)

    private val controllerToTest = UserController(
        userServiceMock,
        jwtTokenUtilMock,
        userDetailsServiceMock
    )

    private fun successUserValidationFixture() {
        val successUser = User(
            oMother.successUserCredentials.username,
            oMother.successUserCredentials.password,
            arrayListOf()
        )

        Mockito.`when`(userDetailsServiceMock.loadUserByUsername("successUser"))
            .thenReturn(successUser)

        Mockito.`when`(
            userServiceMock.getUserByUsername("successUser")
        ).thenReturn(UserCredentialsDTO("successUser", "pass", "successToken"))

        Mockito.`when`(
            jwtTokenUtilMock.generateToken(successUser, "successToken")
        ).thenReturn("successToken")
    }

    @Test
    fun loginTestSuccess() {
        // Arrange
        val authenticationRequest = oMother.successUserCredentials

        successUserValidationFixture()

        // Act
        val response = controllerToTest.login(authenticationRequest)

        // Assert
        assertEquals("successToken", (response.body as JwtResponse).token)
    }

    @Test
    fun loginTestFailureOnUsernameNotFound() {
        // Arrange
        val authenticationRequest = oMother.notFoundUserCredentials

        Mockito.`when`(
            userDetailsServiceMock.loadUserByUsername("notFoundUser")
        ).thenThrow(
            UsernameNotFoundException("There is no user like this one...")
        )

        // Act
        val response = controllerToTest.login(authenticationRequest)

        // Assert
        assertEquals("User not found", (response.body as ResponseMessage).message)
    }

    @Test
    fun loginTestFailureOnInternalServerError() {
        // Arrange
        val authenticationRequest = oMother.internalServerErrorUserCredentials

        Mockito.`when`(
            userDetailsServiceMock.loadUserByUsername("internalServerErrorUser")
        ).thenThrow(RuntimeException())

        // Act
        val response = controllerToTest.login(authenticationRequest)

        // Assert
        assertEquals("Something terrible with Postgres...", (response.body as ResponseMessage).message)
    }

    @Test
    fun loginTestFailureOnInvalidPassword() {
        // Arrange
        val authenticationRequest = oMother.invalidPasswordUserCredentials

        successUserValidationFixture()

        // Act
        val response = controllerToTest.login(authenticationRequest)

        // Assert
        assertEquals("User not found or invalid password", (response.body as ResponseMessage).message)
    }

    @Test
    fun registerTestSuccess() {
        // Arrange
        val user = oMother.successUserCredentials

        Mockito.`when`(
            userServiceMock.register(user)
        ).thenReturn("Success!")

        // Act
        val response = controllerToTest.register(user)

        // Assert
        assertEquals("Success!", (response.body as ResponseMessage).message)
    }

    @Test
    fun registerTestFailureOnInternalServerError() {
        // Arrange
        val user = oMother.internalServerErrorUserCredentials

        Mockito.`when`(
            userServiceMock.register(oMother.internalServerErrorUserCredentials)
        ).thenThrow(RuntimeException())

        // Act
        val response = controllerToTest.register(user)

        // Assert
        assertEquals(expectations.serverDeathMessage, (response.body as ResponseMessage).message)
    }

    @Test
    fun registerTestFailureOnAlreadyExistingUser() {
        // Arrange
        val user = oMother.alreadyExistUserCredentials

        Mockito.`when`(
            userServiceMock.register(user)
        ).thenReturn("User already exists")

        // Act
        val response = controllerToTest.register(user)

        // Assert
        assertEquals("User already exists", (response.body as ResponseMessage).message)
    }

    @Test
    fun changePasswordTestSuccess() {
        // Arrange
        val passwords = oMother.correctNewPasswordDTO
        val jwtToken = oMother.correctJWT

        val username = "successChangePasUser"

        Mockito.`when`(jwtTokenUtilMock.getUsernameFromToken(oMother.correctJWT.split(" ")[1].trim()))
            .thenReturn(username)

        Mockito.`when`(
            userServiceMock.changeUserInfo(
                NewPasswordDTOWithUsername(
                    username,
                    passwords.oldPassword,
                    passwords.newPassword
                )
            )
        ).thenReturn(true)

        // Act
        val response = controllerToTest.changePassword(passwords, jwtToken)

        // Assert
        assertEquals("Password changed successfully", (response.body as ResponseMessage).message)
    }

    // This test is created for fun only. There is no way to reproduce it
    // in production
    @Test
    fun changePasswordTestFailureOnInvalidJwtToken() {
        // Arrange
        val passwords = oMother.correctNewPasswordDTO
        val jwtToken = oMother.incorrectJWT

        // Act

        // Assert
        assertThatExceptionOfType(RuntimeException::class.java).isThrownBy {
            controllerToTest.changePassword(
                passwords,
                jwtToken
            )
        }
    }

    @Test
    fun changePasswordTestFailureOnInternalServerError() {
        // Arrange
        val passwords = oMother.correctNewPasswordDTO
        val jwtToken = oMother.serverFailJWT

        val username = oMother.internalServerErrorUserCredentials.username

        Mockito.`when`(
            jwtTokenUtilMock.getUsernameFromToken(jwtToken)
        ).thenReturn(username)

        Mockito.`when`(
            userServiceMock.changeUserInfo(
                NewPasswordDTOWithUsername(username, passwords.oldPassword, passwords.newPassword)
            )
        ).thenThrow(RuntimeException())

        // Act
        val response = controllerToTest.changePassword(passwords, jwtToken)

        // Assert
        assertEquals(expectations.serverDeathMessage, (response.body as ResponseMessage).message)
    }

    @Test
    fun changePasswordTestFailureOnNonMatchingPasswords() {
        // Arrange
        val passwords = oMother.incorrectNewPasswordDTO
        val jwtToken = oMother.incorrectPasswordJWT

        val username = "incorrectPasUser"

        Mockito.`when`(
            jwtTokenUtilMock.getUsernameFromToken(username)
        ).thenReturn(username)

        Mockito.`when`(
            userServiceMock.changeUserInfo(
                NewPasswordDTOWithUsername(
                    username,
                    passwords.oldPassword,
                    passwords.newPassword
                )
            )
        ).thenReturn(false)

        // Act
        val response = controllerToTest.changePassword(passwords, jwtToken)

        // Assert
        assertEquals("Password wasn't changed", (response.body as ResponseMessage).message)
    }
}