package com.fdsystem.fdserver.controllers.components

import org.apache.commons.logging.LogFactory
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
    fun tokenGeneratorTestWithSuccess()
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

    @Test
    fun doGenerateTokenTestWithSuccess()
    {
        // Arrange
        val username = "user"
        val userDBToken = "tttkkkeeen"
        val claims = hashMapOf("DBToken" to userDBToken)

        // Set private method public
        val requiredPrivateMethod =
            jwtTokenUtilToTest.javaClass.getDeclaredMethod(
                "doGenerateToken", java.util.Map::class.java,
                String::class.java
            )
        requiredPrivateMethod.isAccessible = true

        // Prepare method parameters
        val privateMethodParameters = arrayOfNulls<Any>(2)
        privateMethodParameters[0] = claims
        privateMethodParameters[1] = username

        // Act
        val jwtToken = requiredPrivateMethod.invoke(
            jwtTokenUtilToTest,
            *privateMethodParameters
        ).toString()

        // Assert
        val returnedClaims = jwtTokenUtilToTest.getAllClaimsFromToken(jwtToken)

        assert(
            returnedClaims.subject == username &&
                    returnedClaims["DBToken"] == userDBToken
        )
    }

    @Test
    fun getAllClaimsFromTokenSuccessTest()
    {
        // Arrange
        val username = "user"
        val password = "password"
        val dbToken = "123456"
        val token = jwtTokenUtilToTest.generateToken(
            User(username, password, arrayListOf()), dbToken
        )

        // Action
        val claimsFromToken = jwtTokenUtilToTest.getAllClaimsFromToken(token)

        // Assert
        assert(
            claimsFromToken.subject == username &&
                    claimsFromToken["DBToken"] == dbToken
        )
    }
}