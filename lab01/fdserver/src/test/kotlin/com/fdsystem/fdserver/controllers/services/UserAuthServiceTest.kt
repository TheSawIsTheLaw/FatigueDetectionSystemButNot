package com.fdsystem.fdserver.controllers.services

import com.fdsystem.fdserver.data.CharRepositoryImpl
import com.fdsystem.fdserver.data.UserRepositoryImpl
import com.fdsystem.fdserver.domain.dtos.NewPasswordDTO
import com.fdsystem.fdserver.domain.dtos.NewPasswordDTOWithUsername
import com.fdsystem.fdserver.domain.dtos.UserCredentialsDTO
import com.fdsystem.fdserver.domain.logicentities.DSUserCredentials
import com.fdsystem.fdserver.domain.logicentities.TokenInformation
import com.fdsystem.fdserver.domain.logicentities.USCredentialsChangeInfo
import com.fdsystem.fdserver.domain.logicentities.USUserCredentials
import com.fdsystem.fdserver.expects.mocks.UserAuthServiceMocksExpectations
import com.fdsystem.fdserver.mothers.UserAuthServiceOMother
import org.apache.commons.logging.LogFactory
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.platform.commons.logging.LoggerFactory
import org.mockito.Mock
import org.mockito.Mockito
import org.springframework.security.core.userdetails.UsernameNotFoundException
import kotlin.RuntimeException

internal class UserAuthServiceTest {
    private val oMother = UserAuthServiceOMother()
    private val mocksExpectations = UserAuthServiceMocksExpectations()

    private val charRepositoryMock: CharRepositoryImpl =
        Mockito.mock(CharRepositoryImpl::class.java)

    private val userRepositoryMock: UserRepositoryImpl =
        Mockito.mock(UserRepositoryImpl::class.java)

    private val serviceToTest = UserAuthService(
        userRepositoryMock,
        charRepositoryMock
    )

    @Test
    fun registerWithSuccessRegistration() {
        // Arrange
        val user = oMother.successRegistrationUser

        Mockito.`when`(
            charRepositoryMock.getNewTokenForUser(USUserCredentials(user.username, user.password, user.dbToken))
        ).thenReturn(mocksExpectations.successTokenInformation)

        Mockito.`when`(
            userRepositoryMock.registerUser(
                USUserCredentials(user.username, user.password, mocksExpectations.successTokenInformation.token)
            )
        ).thenReturn(true)

        // Act
        val returnedRegistrationStatus = serviceToTest.register(user)

        // Assert
        assertEquals("Success", returnedRegistrationStatus)
    }

    @Test
    fun registerWithAlreadyExistsFailure() {
        // Arrange
        val user = oMother.alreadyExistUser

        Mockito.`when`(
            charRepositoryMock.getNewTokenForUser(USUserCredentials(user.username, user.password, user.dbToken))
        ).thenReturn(mocksExpectations.failTokenInformation)

        Mockito.`when`(
            userRepositoryMock.registerUser(
                USUserCredentials(
                    user.username,
                    user.password,
                    mocksExpectations.failTokenInformation.token
                )
            )
        ).thenReturn(false)

        // Act
        val returnedRegistrationStatus = serviceToTest.register(user)

        // Assert
        assertEquals("User already exists", returnedRegistrationStatus)
    }

    @Test
    fun registerWithInternalErrorException() {
        // Arrange
        val user = UserCredentialsDTO(
            "", "", ""
        )

        Mockito.`when`(
            charRepositoryMock.getNewTokenForUser(USUserCredentials("", "", ""))
        ).thenReturn(mocksExpectations.failTokenInformation)

        Mockito.`when`(
            userRepositoryMock.registerUser(
                USUserCredentials("", "", mocksExpectations.failTokenInformation.token)
            )
        ).thenThrow(RuntimeException("Internal server error"))

        // Act

        // Assert
        Assertions.assertThatExceptionOfType(RuntimeException::class.java)
            .isThrownBy { serviceToTest.register(user) }
    }

    @Test
    fun changeUserInfoWithSuccess() {
        // Arrange
        val userInfo = oMother.successChangeUserInfo

        Mockito.`when`(
            userRepositoryMock.changePasswordAndUsername(
                USCredentialsChangeInfo(
                    userInfo.username,
                    userInfo.username,
                    userInfo.oldPassword,
                    userInfo.newPassword
                )
            )
        ).thenReturn(true)

        // Act
        val returnedStatus = serviceToTest.changeUserInfo(userInfo)

        // Assert
        assertTrue(returnedStatus)
    }

    @Test
    fun changeUserInfoWithFailurePassword() {
        // Arrange
        val userInfo = oMother.failChangeUserInfo

        Mockito.`when`(
            userRepositoryMock.changePasswordAndUsername(
                USCredentialsChangeInfo(
                    userInfo.username,
                    userInfo.username,
                    userInfo.oldPassword,
                    userInfo.newPassword
                )
            )
        ).thenReturn(false)

        // Act
        val returnedStatus = serviceToTest.changeUserInfo(userInfo)

        // Assert
        assertFalse(returnedStatus)
    }

    @Test
    fun changeUserInfoWithException() {
        // Arrange
        val userInfo = oMother.exceptionChangeUserInfo

        Mockito.`when`(
            userRepositoryMock.changePasswordAndUsername(
                USCredentialsChangeInfo(
                    userInfo.username,
                    userInfo.username,
                    userInfo.oldPassword,
                    userInfo.newPassword
                )
            )
        ).thenThrow(RuntimeException("Internal server error"))

        // Act

        // Assert
        Assertions.assertThatExceptionOfType(RuntimeException::class.java)
            .isThrownBy { serviceToTest.changeUserInfo(userInfo) }
    }

    @Test
    fun getUserByUsernameSuccess() {
        // Arrange
        val username = oMother.successRegistrationUser.username

        Mockito.`when`(
            userRepositoryMock.getUserByUsername(USUserCredentials(username, "", ""))
        ).thenReturn(USUserCredentials(username, "1", "123"))

        // Act
        val returnedUser = serviceToTest.getUserByUsername(username)

        // Assert
        assertEquals(UserCredentialsDTO(username, "1", "123"), returnedUser)
    }

    @Test
    fun getUserByUsernameFailure() {
        // Arrange
        val username = oMother.successRegistrationUser.username

        Mockito.`when`(
            userRepositoryMock.getUserByUsername(
                USUserCredentials(username, "", "")
            )
        ).thenReturn(USUserCredentials("", "", ""))

        // Act
        val returnedUser = serviceToTest.getUserByUsername(username)

        // Assert
        assertEquals(UserCredentialsDTO("", "", ""), returnedUser)
    }

    @Test
    fun getUserByUsernameException() {
        // Arrange
        val username = oMother.exceptionChangeUserInfo.username

        Mockito.`when`(
            userRepositoryMock.getUserByUsername(
                USUserCredentials(username, "", "")
            )
        ).thenThrow(RuntimeException("Wow"))

        // Act

        // Assert
        Assertions.assertThatExceptionOfType(RuntimeException::class.java)
            .isThrownBy { serviceToTest.getUserByUsername(username) }
    }
}