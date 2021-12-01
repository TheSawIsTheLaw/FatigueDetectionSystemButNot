package com.fdsystem.fdserver.controllers.services

import com.fdsystem.fdserver.data.UserRepositoryImpl
import com.fdsystem.fdserver.domain.logicentities.USUserCredentials
import org.apache.commons.logging.LogFactory
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.platform.commons.logging.LoggerFactory
import org.mockito.Mockito
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UsernameNotFoundException
import java.lang.RuntimeException

internal class JwtUserDetailsServiceTest {
    private val userRepositoryImpl = Mockito.mock(
        UserRepositoryImpl::class.java
    )

    private val serviceToTest = JwtUserDetailsService(userRepositoryImpl)

    @Test
    fun successLoadForUser() {
        // Arrange
        val username = "existingUser"
        val password = "pass"

        Mockito.`when`(
            userRepositoryImpl.userExists(username)
        ).thenReturn(true)

        Mockito.`when`(
            userRepositoryImpl.getUserByUsername(USUserCredentials(username, "", ""))
        ).thenReturn(USUserCredentials(username, password, "123"))

        // Action
        val returnedUser = serviceToTest.loadUserByUsername(username)

        // Assert
        assertEquals(User(username, password, arrayListOf()), returnedUser)
    }

    @Test
    fun failureUserNotFound() {
        // Arrange
        val username = "notExistingUser"

        Mockito.`when`(
            userRepositoryImpl.userExists(username)
        ).thenReturn(false)

        // Action
        // Throw is checked in assert
        // Is this a good way?..

        // Assert
        assertThatExceptionOfType(UsernameNotFoundException::class.java)
            .isThrownBy { serviceToTest.loadUserByUsername(username) }
    }

    @Test
    fun failureInternalError() {
        // Arrange
        val username = "ecxUser"

        Mockito.`when`(
            userRepositoryImpl.getUserByUsername(USUserCredentials(username, "", ""))
        ).thenThrow(RuntimeException("Internal server error"))

        // Action
        // Throw is checked in assert

        // Assert
        assertThatExceptionOfType(UsernameNotFoundException::class.java)
            .isThrownBy { serviceToTest.loadUserByUsername(username) }
    }
}