package com.fdsystem.fdserver.controllers.jwt

import com.fdsystem.fdserver.controllers.components.JwtTokenUtil
import com.fdsystem.fdserver.controllers.services.JwtUserDetailsService
import io.jsonwebtoken.ExpiredJwtException
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.mockito.Mockito
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import java.lang.IllegalArgumentException

internal class JwtRequestFilterTest
{
    private val jwtUserDetailsServiceMock = Mockito.mock(
        JwtUserDetailsService::class.java
    )

    private val jwtTokenUtilMock = Mockito.mock(JwtTokenUtil::class.java)

    private data class MockExpectations(
        val int: Int = 1
    )

    private data class MockParameters(
        val int: Int = 1
    )

    private val mockExpectations = MockExpectations()

    private val mockParameters = MockParameters()

    init
    {
        Mockito.`when`(
            jwtUserDetailsServiceMock.loadUserByUsername("normal")
        ).thenReturn(User("normal", "pass", arrayListOf()))

        Mockito.`when`(
            jwtUserDetailsServiceMock.loadUserByUsername("notExisting")
        ).thenThrow(UsernameNotFoundException("User not found"))

        Mockito.`when`(
            jwtUserDetailsServiceMock.loadUserByUsername("itWontBeValidated")
        ).thenReturn(User("itWontBeValidated", "pass", arrayListOf()))

        Mockito.`when`(
            jwtTokenUtilMock.getUsernameFromToken("11111")
        ).thenReturn("normal")

        Mockito.`when`(
            jwtTokenUtilMock.getUsernameFromToken("22222")
        ).thenThrow(IllegalArgumentException(""))

        Mockito.`when`(
            jwtTokenUtilMock.getUsernameFromToken("33333")
        ).thenReturn("itWontBeValidated")

        Mockito.`when`(
            jwtTokenUtilMock.validateToken(
                "11111",
                User("11111user", "pass", arrayListOf())
            )
        ).thenReturn(true)

        Mockito.`when`(
            jwtTokenUtilMock.validateToken(
                "33333",
                User("itWontBeValidated", "pass", arrayListOf())
            )
        ).thenReturn(false)
    }

    // Request header'ы должны ещё в тестах то содержать Bearer, то не содержать

    @Test
    fun doFilterInternal()
    {
    }
}