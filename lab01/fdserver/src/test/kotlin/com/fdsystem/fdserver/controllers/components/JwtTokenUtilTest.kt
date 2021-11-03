package com.fdsystem.fdserver.controllers.components

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.apache.commons.logging.LogFactory
import org.junit.jupiter.api.Test
import org.junit.platform.commons.util.ReflectionUtils
import org.mockito.Mockito
import org.springframework.security.core.userdetails.User
import java.util.*

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

    @Test
    fun getAllClaimsFromTokenFailureOnStrangeTokenTest()
    {
        // Arrange
        val token = "lol, i'm a token uwu"

        // Action
        val claimsFromToken = try
        {
            jwtTokenUtilToTest.getAllClaimsFromToken(token)
        }
        catch (exc: Exception)
        {
            null
        }

        // Assert
        assert(claimsFromToken == null)
    }

    @Test
    fun getClaimFromTokenSuccessTest()
    {
        // Arrange
        val username = "user"
        val password = "password"
        val dbToken = "123456"

        val jwtToken = jwtTokenUtilToTest.generateToken(
            User(username, password, arrayListOf()), dbToken
        )

        // Action
        val subject = jwtTokenUtilToTest.getClaimFromToken(
            jwtToken,
            Claims::getSubject
        )

        // Assert
        assert(subject == username)
    }

    @Test
    fun getClaimFromTokenFailureOnStrangeTokenTest()
    {
        // Arrange
        val jwtToken = "what am i doing?"

        // Action
        val expirationTime = try
        {
            jwtTokenUtilToTest.getClaimFromToken(
                jwtToken,
                Claims::getExpiration
            )
        }
        catch (exc: Exception)
        {
            null
        }

        // Assert
        assert(expirationTime == null)
    }

    @Test
    fun getUsernameFromTokenSuccessTest()
    {
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
        assert(subject == username)
    }

    @Test
    fun getUsernameFromTokenFailureOnStrangeTokenTest()
    {
        // Arrange
        val jwtToken = "what am i doing?"

        // Action
        val subject = try
        {
            jwtTokenUtilToTest.getUsernameFromToken(jwtToken)
        }
        catch (exc: Exception)
        {
            null
        }

        // Assert
        assert(subject == null)
    }

    @Test
    fun getExpirationTimeFromTokenSuccessTest()
    {
        // Arrange
        val username = "user"
        val password = "password"
        val dbToken = "123456"

        val jwtToken = jwtTokenUtilToTest.generateToken(
            User(username, password, arrayListOf()), dbToken
        )

        // Action
        jwtTokenUtilToTest.getExpirationDateFromToken(jwtToken)

        // Assert
        // If there is no exception then it's ok
    }

    @Test
    fun getExpirationTimeFailureOnStrangeToken()
    {
        // Arrange
        val jwtToken = "WE ARE THE SALT OF THE EARTH"

        // Action
        val expirationTime = try
        {
            jwtTokenUtilToTest.getExpirationDateFromToken(jwtToken)
        }
        catch (exc: Exception)
        {
            null
        }

        // Assert
        assert(expirationTime == null)
    }

    @Test
    fun isTokenExpiredWithNotExpiredTokenTest()
    {
        // Arrange
        val username = "user"
        val password = "password"
        val dbToken = "123456"

        val jwtToken = jwtTokenUtilToTest.generateToken(
            User(username, password, arrayListOf()), dbToken
        )

        // Set private method public
        val requiredPrivateMethod =
            jwtTokenUtilToTest.javaClass.getDeclaredMethod(
                "isTokenExpired", String::class.java
            )
        requiredPrivateMethod.isAccessible = true

        // Prepare method parameters
        val requiredMethodParameters = arrayOfNulls<Any>(1)
        requiredMethodParameters[0] = jwtToken

        // Action
        val isExpired = requiredPrivateMethod.invoke(
            jwtTokenUtilToTest,
            *requiredMethodParameters
        ) as Boolean

        // Assert
        assert(!isExpired)
    }

    @Test
    fun isTokenExpiredWithExpiredTokenTest()
    {
        // Arrange
        val claims = hashMapOf("dbToken" to "1222")
        val subject = "what"
        val secret = "LZKPDJMW7MSY273666MZNAAAUCJEBASUKOXK666777A"

        val jwtToken = Jwts.builder().setClaims(claims).setSubject(subject)
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis()))
            .signWith(SignatureAlgorithm.HS256, secret).compact()

        // Set private method public
        val requiredPrivateMethod =
            jwtTokenUtilToTest.javaClass.getDeclaredMethod(
                "isTokenExpired", String::class.java
            )
        requiredPrivateMethod.isAccessible = true

        // Prepare method parameters
        val requiredMethodParameters = arrayOfNulls<Any>(1)
        requiredMethodParameters[0] = jwtToken

        // Action
        // Thank you very much, mate, for this 'magic' function.
        val expiration = try
        {
            requiredPrivateMethod.invoke(
                jwtTokenUtilToTest, *requiredMethodParameters
            ) as Boolean
        }
        catch (exc: Exception)
        {
            null
        }

        // Assert
        assert(expiration == null)
    }

    @Test
    fun validateTokenSuccessTest()
    {
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
        assert(isTokenValid)
    }

    @Test
    fun validateTokenFailureOnUsernameTest()
    {
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
        assert(!isTokenValid)
    }

    @Test
    fun validateTokenFailureOnExpiredTokenTest()
    {
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
        val validity = try
        {
            jwtTokenUtilToTest.validateToken(jwtToken, userDetails)
        }
        catch (exc: Exception)
        {
            null
        }

        // Assert
        assert(validity == null)
    }
}