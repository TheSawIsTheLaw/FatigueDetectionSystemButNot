package com.fdsystem.fdserver.controllers.services

import com.fdsystem.fdserver.data.CharRepositoryImpl
import com.fdsystem.fdserver.data.UserRepositoryImpl
import com.fdsystem.fdserver.domain.logicentities.DSUserCredentials
import com.fdsystem.fdserver.domain.logicentities.TokenInformation
import com.fdsystem.fdserver.domain.logicentities.USCredentialsChangeInfo
import com.fdsystem.fdserver.domain.logicentities.USUserCredentials
import org.junit.jupiter.api.Assertions.*
import org.mockito.Mockito

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

        val successPasswordChangeUserInfo: USCredentialsChangeInfo =
            USCredentialsChangeInfo(
                "Username", "Username",
                "oldPas", "newPas"
            ),

        val failPasswordChangeUserInfo: USCredentialsChangeInfo =
            USCredentialsChangeInfo(
                "FailUsername", "FailUsername",
                "FailPas", "FailNewPas"
            ),

        val exceptionPasswordChangeUserInfo: USCredentialsChangeInfo =
            USCredentialsChangeInfo(
                "excUsername", "excUsername",
                "", ""
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
    }

    private fun prepareUserRepositoryMock()
    {
        Mockito.`when`(
            userRepositoryMock.registerUser(mockParameters.usUserCredentialsExampleRepeat)
        ).thenReturn(false)

        Mockito.`when`(
            userRepositoryMock.registerUser(
                mockParameters.usUserCredentialsExampleToCheckException
            )
        ).thenThrow(Exception("Internal server error"))

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
        ).thenThrow(Exception("Internal server error"))

        Mockito.`when`(
            userRepositoryMock.getUserByUsername(
                mockParameters.successGetUserByUsernameUsername
            )
        ).thenReturn(mockExpectations.failDSUserCredentials)
    }

    init
    {
        prepareCharRepositoryMock()
        prepareUserRepositoryMock()
    }

    private val mockExpectations: MockExpectations = MockExpectations()
    private val mockParameters: MockParameters = MockParameters()
}