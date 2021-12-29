package com.fdsystem.fdserver.controllers.components

import com.fdsystem.fdserver.mothers.JwtTokenUtilOMother
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.security.core.userdetails.User
import java.util.*

internal class JwtTokenUtilTest {
    private val oMother = JwtTokenUtilOMother()

    private val jwtTokenUtilToTest: JwtTokenUtil = JwtTokenUtil()

    init {
        org.springframework.test.util.ReflectionTestUtils.setField(
            jwtTokenUtilToTest,
            "secret",
            oMother.defaultSecretKey
        )
    }

    @Test
    fun tokenGeneratorWithSuccessTest() {
        // Arrange
        val username = oMother.defaultUser
        val password = oMother.defaultPassword
        val dbToken = oMother.defaultDBToken
        val userDetails = User(username, password, arrayListOf())

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
        val username = oMother.defaultUser
        val password = oMother.defaultPassword
        val dbToken = oMother.defaultDBToken
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
        val token = oMother.invalidToken

        // Action

        // Assert
        assertThatExceptionOfType(Exception::class.java)
            .isThrownBy { jwtTokenUtilToTest.getAllClaimsFromToken(token) }
    }

    @Test
    fun getUsernameFromTokenSuccessTest() {
        // Arrange
        val username = oMother.defaultUser
        val password = oMother.defaultPassword
        val dbToken = oMother.defaultDBToken

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
        val jwtToken = oMother.invalidToken

        // Action

        // Assert
        assertThatExceptionOfType(Exception::class.java)
            .isThrownBy { jwtTokenUtilToTest.getUsernameFromToken(jwtToken) }
    }

    @Test
    fun validateTokenSuccessTest() {
        // Arrange
        val username = oMother.defaultUser
        val password = oMother.defaultPassword
        val dbToken = oMother.defaultDBToken

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
        val username = oMother.defaultUser
        val password = oMother.defaultPassword
        val dbToken = oMother.defaultDBToken

        val userDetails = User(username, password, arrayListOf())
        val jwtToken = jwtTokenUtilToTest.generateToken(userDetails, dbToken)

        val invalidUsername = oMother.invalidUsername
        val wrongUserDetails = User(invalidUsername, password, arrayListOf())

        // Act
        val isTokenValid =
            jwtTokenUtilToTest.validateToken(jwtToken, wrongUserDetails)

        // Assert
        assertFalse(isTokenValid)
    }

    @Test
    fun validateTokenFailureOnExpiredTokenTest() {
        // Arrange
        val claims = oMother.defaultClaims
        val subject = oMother.defaultUser
        val password = oMother.defaultPassword
        val secret = oMother.defaultSecretKey

        val jwtToken = Jwts.builder().setClaims(claims).setSubject(subject)
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis()))
            .signWith(SignatureAlgorithm.HS256, secret).compact()

        val userDetails = User(subject, password, arrayListOf())

        // Act

        // Assert
        assertThatExceptionOfType(Exception::class.java)
            .isThrownBy { jwtTokenUtilToTest.validateToken(jwtToken, userDetails) }
    }
}