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
import org.apache.commons.logging.LogFactory
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.platform.commons.logging.LoggerFactory
import org.mockito.Mockito
import java.lang.RuntimeException

internal class UserAuthServiceTest
{
    private val charRepositoryMock: CharRepositoryImpl =
        Mockito.mock(CharRepositoryImpl::class.java)

    private val userRepositoryMock: UserRepositoryImpl =
        Mockito.mock(UserRepositoryImpl::class.java)

    private val serviceToTest = UserAuthService(
        userRepositoryMock,
        charRepositoryMock
    )

    private data class MockExpectations(
        val successTokenInformation: TokenInformation = TokenInformation(
            "ololo", "30"
        ),

        val failTokenInformation: TokenInformation = TokenInformation(
            "", ""
        ),

        val successDSUserCredentials: DSUserCredentials = DSUserCredentials(
            "a", "ololo", "123"
        ),

        val failDSUserCredentials: USUserCredentials = USUserCredentials(
            "", "", ""
        )
    )

    private data class MockParameters(
        val usUserCredentialsExample: USUserCredentials = USUserCredentials(
            "Username",
            "Password",
            ""
        ),

        val usUserCredentialsExampleWithToken: USUserCredentials = USUserCredentials(
            usUserCredentialsExample.username,
            usUserCredentialsExample.password,
            "ololo"
        ),

        val usUserCredentialsExampleToCheckException: USUserCredentials =
            USUserCredentials(
                "",
                "",
                ""
            ),

        val usUserCredentialsExampleRepeat: USUserCredentials = USUserCredentials(
            "Username",
            "AnotherPasswordToCheckRetryOfRegistration",
            ""
        ),

        val successNewPasswordDTOWithUsername: NewPasswordDTOWithUsername =
            NewPasswordDTOWithUsername("Username", "oldPas", "newPas"),

        val successPasswordChangeUserInfo: USCredentialsChangeInfo =
            USCredentialsChangeInfo(
                successNewPasswordDTOWithUsername.username,
                successNewPasswordDTOWithUsername.username,
                successNewPasswordDTOWithUsername.oldPassword,
                successNewPasswordDTOWithUsername.newPassword
            ),

        val failNewPasswordDTOWithUsername: NewPasswordDTOWithUsername =
            NewPasswordDTOWithUsername(
                "FailUsername", "FailPas", "FailNewPas"
            ),

        val failPasswordChangeUserInfo: USCredentialsChangeInfo =
            USCredentialsChangeInfo(
                failNewPasswordDTOWithUsername.username,
                failNewPasswordDTOWithUsername.username,
                failNewPasswordDTOWithUsername.oldPassword,
                failNewPasswordDTOWithUsername.newPassword
            ),

        val exceptionNewPasswordDTOWithUsername: NewPasswordDTOWithUsername =
            NewPasswordDTOWithUsername(
                "excUsername", "", ""
            ),

        val exceptionPasswordChangeUserInfo: USCredentialsChangeInfo =
            USCredentialsChangeInfo(
                exceptionNewPasswordDTOWithUsername.username,
                exceptionNewPasswordDTOWithUsername.username,
                exceptionNewPasswordDTOWithUsername.oldPassword,
                exceptionNewPasswordDTOWithUsername.newPassword
            ),

        val successGetUserByUsernameUsername: USUserCredentials =
            USUserCredentials("a", "", ""),

        val failGetUserByUsernameUsername: USUserCredentials =
            USUserCredentials("b", "", ""),

        val exceptionGetUserByUsernameUsername: USUserCredentials =
            USUserCredentials("c", "", "")
    )

    private fun prepareCharRepositoryMock()
    {
        Mockito.`when`(
            charRepositoryMock.getNewTokenForUser(mockParameters.usUserCredentialsExample)
        ).thenReturn(mockExpectations.successTokenInformation)

        Mockito.`when`(
            charRepositoryMock.getNewTokenForUser(mockParameters.usUserCredentialsExampleRepeat)
        ).thenReturn(mockExpectations.failTokenInformation)

        Mockito.`when`(
            charRepositoryMock.getNewTokenForUser(mockParameters.usUserCredentialsExampleToCheckException)
        ).thenReturn(mockExpectations.failTokenInformation)
    }

    private fun prepareUserRepositoryMock()
    {
        Mockito.`when`(
            userRepositoryMock.registerUser(
                mockParameters.usUserCredentialsExampleWithToken
            )
        ).thenReturn(true)

        Mockito.`when`(
            userRepositoryMock.registerUser(mockParameters.usUserCredentialsExampleRepeat)
        ).thenReturn(false)

        Mockito.`when`(
            userRepositoryMock.registerUser(
                mockParameters.usUserCredentialsExampleToCheckException
            )
        ).thenThrow(RuntimeException("Internal server error"))

        Mockito.`when`(
            userRepositoryMock.changePasswordAndUsername(
                mockParameters.successPasswordChangeUserInfo
            )
        ).thenReturn(true)

        Mockito.`when`(
            userRepositoryMock.changePasswordAndUsername(
                mockParameters.failPasswordChangeUserInfo
            )
        ).thenReturn(false)

        Mockito.`when`(
            userRepositoryMock.changePasswordAndUsername(
                mockParameters.exceptionPasswordChangeUserInfo
            )
        ).thenThrow(RuntimeException("Internal server error"))

        Mockito.`when`(
            userRepositoryMock.getUserByUsername(
                mockParameters.successGetUserByUsernameUsername
            )
        ).thenReturn(mockExpectations.failDSUserCredentials)
    }

    private val mockExpectations: MockExpectations = MockExpectations()
    private val mockParameters: MockParameters = MockParameters()

    init
    {
        prepareCharRepositoryMock()
        prepareUserRepositoryMock()
    }

    @Test
    fun registerWithSuccessRegistration()
    {
        // Arrange
        val user = UserCredentialsDTO(
            "Username", "Password"
        )

        // Act
        val returnedRegistrationStatus =
            serviceToTest.register(user)

        // Assert
        assert(returnedRegistrationStatus == "Success")
    }

    @Test
    fun registerWithAlreadyExistsFailure()
    {
        // Arrange
        val user = UserCredentialsDTO(
            "Username", "AnotherPasswordToCheckRetryOfRegistration"
        )

        // Act
        val returnedRegistrationStatus =
            serviceToTest.register(user)

        // Assert
        LogFactory.getLog(javaClass).debug(returnedRegistrationStatus)
        assert(returnedRegistrationStatus == "User already exists")
    }

    @Test
    fun registerWithInternalErrorException()
    {
        // Arrange
        val user = UserCredentialsDTO(
            "", ""
        )

        // Act
        val returnedRegistrationStatus = try
        {
            serviceToTest.register(user)
        }
        catch (exc: Exception)
        {
            null
        }

        // Assert
        assert(returnedRegistrationStatus == null)
    }

    @Test
    fun changeUserInfoWithSuccess()
    {
        // Arrange
        val userInfo = NewPasswordDTOWithUsername(
            "Username", "oldPas", "newPas"
        )

        // Act
        val returnedStatus = serviceToTest.changeUserInfo(userInfo)

        // Assert
        assert(returnedStatus)
    }

    @Test
    fun changeUserInfoWithFailurePassword()
    {
        // Arrange
        val userInfo = NewPasswordDTOWithUsername(
            "FailUsername", "FailOldPas", "FailNewPas"
        )

        // Act
        val returnedStatus = serviceToTest.changeUserInfo(userInfo)

        // Assert
        assert(!returnedStatus)
    }

    @Test
    fun changeUserInfoWithExceptrion()
    {
        // Arrange
        val userInfo = NewPasswordDTOWithUsername(
            "excUsername", "", ""
        )

        // Act
        val returnedStatus = try
        {
            serviceToTest.changeUserInfo(userInfo)
        }
        catch (exc: Exception)
        {
            null
        }

        // Assert
        assert(returnedStatus == null)
    }
}