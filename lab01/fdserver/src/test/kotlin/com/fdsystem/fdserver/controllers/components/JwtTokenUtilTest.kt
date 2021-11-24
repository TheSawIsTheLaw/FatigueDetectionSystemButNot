package com.fdsystem.fdserver.controllers.components

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.apache.commons.logging.LogFactory
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.platform.commons.util.ReflectionUtils
import org.mockito.Mockito
import org.springframework.security.core.userdetails.User
import java.util.*

internal class JwtTokenUtilTest {
    private val jwtTokenUtilToTest: JwtTokenUtil = JwtTokenUtil()

    init {
        org.springframework.test.util.ReflectionTestUtils.setField(
            jwtTokenUtilToTest,
            "secret",
            "LZKPDJMW7MSY273666MZNAAAUCJEBASUKOXK666777A"
        )
    }

    @Test
    fun tokenGeneratorWithSuccessTest() {
        // Arrange
        val username = "user"
        val password = "password"
        val userDetails = User(username, password, arrayListOf())
        val dbToken = "123"

        // Action
        val token = jwtTokenUtilToTest.generateToken(userDetails, dbToken)

        // Assert
        val claims = jwtTokenUtilToTest.getAllClaimsFromToken(token)

        assertEquals(username, claims.subject)
        assertEquals(dbToken, claims["DBToken"])
    }

    @Test
    fun getAllClaimsFromTokenSuccessTest() {
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
        assertEquals(username, claimsFromToken.subject)
        assertEquals(dbToken, claimsFromToken["DBToken"])
    }

    @Test
    fun getAllClaimsFromTokenFailureOnStrangeTokenTest() {
        // Arrange
        val token = "lol, i'm a token uwu"

        // Action
        val claimsFromToken = try {
            jwtTokenUtilToTest.getAllClaimsFromToken(token)
        } catch (exc: Exception) {
            null
        }

        // Assert
        assertNull(claimsFromToken)
    }

    @Test
    fun getUsernameFromTokenSuccessTest() {
        // Arrange
        val username = "user"
        val password = "password"
        val dbToken = "123456"

        val jwtToken = jwtTokenUtilToTest.generateToken(
            User(username, password, arrayListOf()), dbToken
        )

        // Action
        val subject = jwtTokenUtilToTest.getUsernameFromToken(jwtToken)

        // Assert
        assertEquals(subject, username)
    }

    @Test
    fun getUsernameFromTokenFailureOnStrangeTokenTest() {
        // Arrange
        val jwtToken = "what am i doing?"

        // Action
        val subject = try {
            jwtTokenUtilToTest.getUsernameFromToken(jwtToken)
        } catch (exc: Exception) {
            null
        }

        // Assert
        assertNull(subject)
    }

    @Test
    fun validateTokenSuccessTest() {
        // Arrange
        val username = "user"
        val password = "password"
        val dbToken = "122112"

        val userDetails = User(username, password, arrayListOf())
        val jwtToken = jwtTokenUtilToTest.generateToken(userDetails, dbToken)

        // Act
        val isTokenValid =
            jwtTokenUtilToTest.validateToken(jwtToken, userDetails)

        // Assert
        assertTrue(isTokenValid)
    }

    @Test
    fun validateTokenFailureOnUsernameTest() {
        // Arrange
        val username = "user"
        val password = "password"
        val dbToken = "122112"

        val userDetails = User(username, password, arrayListOf())
        val jwtToken = jwtTokenUtilToTest.generateToken(userDetails, dbToken)

        val wrongUsername = "lox"
        val wrongUserDetails = User(wrongUsername, password, arrayListOf())

        // Act
        val isTokenValid =
            jwtTokenUtilToTest.validateToken(jwtToken, wrongUserDetails)

        // Assert
        assertFalse(isTokenValid)
    }

    @Test
    fun validateTokenFailureOnExpiredTokenTest() {
        // Arrange
        val claims = hashMapOf("dbToken" to "1222")
        val subject = "what"
        val password = "..."
        val secret = "LZKPDJMW7MSY273666MZNAAAUCJEBASUKOXK666777A"

        val jwtToken = Jwts.builder().setClaims(claims).setSubject(subject)
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis()))
            .signWith(SignatureAlgorithm.HS256, secret).compact()

        val userDetails = User(subject, password, arrayListOf())

        // Act
        val validity = try {
            jwtTokenUtilToTest.validateToken(jwtToken, userDetails)
        } catch (exc: Exception) {
            null
        }

        // Assert
        assertNull(validity)
    }
}