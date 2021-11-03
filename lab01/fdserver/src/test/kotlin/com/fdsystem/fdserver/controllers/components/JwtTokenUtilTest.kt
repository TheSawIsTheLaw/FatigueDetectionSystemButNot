package com.fdsystem.fdserver.controllers.components

import org.junit.jupiter.api.Test
import org.junit.platform.commons.util.ReflectionUtils
import org.springframework.security.core.userdetails.User

internal class JwtTokenUtilTest
{
    private val jwtTokenUtilToTest: JwtTokenUtil = JwtTokenUtil()

    init
    {
        org.springframework.test.util.ReflectionTestUtils.setField(
            jwtTokenUtilToTest,
            "secret",
            "LZKPDJMW7MSY273666MZNAAAUCJEBASUKOXK666777A"
        )
    }

    @Test
    fun tokenGeneratorTest()
    {
        // Arrange
        val username = "user"
        val password = "password"
        val userDetails = User(username, password, arrayListOf())
        val dbToken = "123"

        // Action
        val token = jwtTokenUtilToTest.generateToken(userDetails, dbToken)

        // Assert
        val claims = jwtTokenUtilToTest.getAllClaimsFromToken(token)

        assert(claims.subject == username && claims["DBToken"] == dbToken)
    }
}