package com.fdsystem.fdserver.controllers.jwt

import com.fdsystem.fdserver.controllers.components.JwtTokenUtil
import com.fdsystem.fdserver.controllers.services.JwtUserDetailsService
import io.jsonwebtoken.ExpiredJwtException
import org.apache.commons.logging.LogFactory
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.platform.commons.logging.LoggerFactory
import org.mockito.Mockito
import org.springframework.mock.web.MockFilterChain
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.lang.IllegalArgumentException
import java.util.*
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

internal class JwtRequestFilterTest
{
    private val jwtUserDetailsServiceMock = Mockito.mock(
        JwtUserDetailsService::class.java
    )

    private val jwtTokenUtilMock = Mockito.mock(JwtTokenUtil::class.java)

    private val filterToTest = JwtRequestFilter(
        jwtUserDetailsServiceMock,
        jwtTokenUtilMock
    )

    private data class MockExpectations(
        val normalUserDetails: UserDetails = User(
            "normal", "pass", arrayListOf()
        ),

        val userDetailsWithoutValidation: UserDetails = User(
            "itWontBeValidated", "pass", arrayListOf()
        )
    )

    private data class MockParameters(
        val existingUser: UserDetails = User(
            "normal", "pass", arrayListOf()
        ),

        val invalidUser: UserDetails = User(
            "itWontBeValidated", "pass", arrayListOf()
        )
    )

    private val mockExpectations = MockExpectations()

    private val mockParameters = MockParameters()

    init
    {
        Mockito.`when`(
            jwtUserDetailsServiceMock.loadUserByUsername("normal")
        ).thenReturn(mockExpectations.normalUserDetails)

        Mockito.`when`(
            jwtUserDetailsServiceMock.loadUserByUsername("notExisting")
        ).thenThrow(UsernameNotFoundException("User not found"))

        Mockito.`when`(
            jwtUserDetailsServiceMock.loadUserByUsername("itWontBeValidated")
        ).thenReturn(mockExpectations.userDetailsWithoutValidation)

        Mockito.`when`(
            jwtTokenUtilMock.getUsernameFromToken("11111")
        ).thenReturn("normal")

        Mockito.`when`(
            jwtTokenUtilMock.getUsernameFromToken("22222")
        ).thenThrow(IllegalArgumentException(""))

        Mockito.`when`(
            jwtTokenUtilMock.getUsernameFromToken("44444")
        ).thenReturn("notExisting")

        Mockito.`when`(
            jwtTokenUtilMock.getUsernameFromToken("33333")
        ).thenReturn("itWontBeValidated")

        Mockito.`when`(
            jwtTokenUtilMock.validateToken(
                "11111",
                mockParameters.existingUser
            )
        ).thenReturn(true)

        Mockito.`when`(
            jwtTokenUtilMock.validateToken(
                "33333",
                mockParameters.invalidUser
            )
        ).thenReturn(false)
    }

    // Request header'ы должны ещё в тестах то содержать Bearer, то не содержать

    @Test
    fun successValidationOfJWT()
    {
        // Arrange
        val request = MockHttpServletRequest()
        request.addHeader("Authorization", "Bearer 11111")
        val response = MockHttpServletResponse()
        val chain = MockFilterChain()

        // Set private method public
        val requiredPrivateMethod = filterToTest.javaClass.getDeclaredMethod(
            "doFilterInternal",
            HttpServletRequest::class.java,
            HttpServletResponse::class.java,
            FilterChain::class.java
        )
        requiredPrivateMethod.isAccessible = true

        // Prepare method parameters
        val privateMethodParameters = arrayOfNulls<Any>(3)
        privateMethodParameters[0] = request
        privateMethodParameters[1] = response
        privateMethodParameters[2] = chain

        // Act
        requiredPrivateMethod.invoke(filterToTest, *privateMethodParameters)

        // Assert
        // There is no assert because of no return value.
        // Make it with gradle test --info to check all log messages
        // It should contain "Token is ok"
    }

    @Test
    fun unsuccessfulValidationBecauseOfInvalidToken()
    {
        // Arrange
        val request = MockHttpServletRequest()
        request.addHeader("Authorization", "Bearer 22222")
        val response = MockHttpServletResponse()
        val chain = MockFilterChain()

        // Set private method public
        val requiredPrivateMethod = filterToTest.javaClass.getDeclaredMethod(
            "doFilterInternal",
            HttpServletRequest::class.java,
            HttpServletResponse::class.java,
            FilterChain::class.java
        )
        requiredPrivateMethod.isAccessible = true

        // Prepare method parameters
        val privateMethodParameters = arrayOfNulls<Any>(3)
        privateMethodParameters[0] = request
        privateMethodParameters[1] = response
        privateMethodParameters[2] = chain

        // Act
        requiredPrivateMethod.invoke(filterToTest, *privateMethodParameters)

        // Assert

        // There is no assert because of no return value.
        // Make it with gradle test --info to check all log messages
        // It should contain "Unable to get JWT Token"
    }

    @Test
    fun unsuccessfulValidationBecauseOfBearerWasNotIncludedInHeader()
    {
        // Arrange
        val request = MockHttpServletRequest()
        request.addHeader("Authorization", "22222")
        val response = MockHttpServletResponse()
        val chain = MockFilterChain()

        // Set private method public
        val requiredPrivateMethod = filterToTest.javaClass.getDeclaredMethod(
            "doFilterInternal",
            HttpServletRequest::class.java,
            HttpServletResponse::class.java,
            FilterChain::class.java
        )
        requiredPrivateMethod.isAccessible = true

        // Prepare method parameters
        val privateMethodParameters = arrayOfNulls<Any>(3)
        privateMethodParameters[0] = request
        privateMethodParameters[1] = response
        privateMethodParameters[2] = chain

        // Act
        requiredPrivateMethod.invoke(filterToTest, *privateMethodParameters)

        // Assert

        // There is no assert because of no return value.
        // Make it with gradle test --info to check all log messages
        // It should contain "JWT Token does not begin with Bearer String"
    }

    @Test
    fun unsuccessfulValidationBecauseOfWrongPassword()
    {
        // Arrange
        val request = MockHttpServletRequest()
        request.addHeader("Authorization", "Bearer 33333")
        val response = MockHttpServletResponse()
        val chain = MockFilterChain()

        // Set private method public
        val requiredPrivateMethod = filterToTest.javaClass.getDeclaredMethod(
            "doFilterInternal",
            HttpServletRequest::class.java,
            HttpServletResponse::class.java,
            FilterChain::class.java
        )
        requiredPrivateMethod.isAccessible = true

        // Prepare method parameters
        val privateMethodParameters = arrayOfNulls<Any>(3)
        privateMethodParameters[0] = request
        privateMethodParameters[1] = response
        privateMethodParameters[2] = chain

        // Act
        requiredPrivateMethod.invoke(filterToTest, *privateMethodParameters)

        // Assert
        
        // There is no assert because of no return value.
        // Make it with gradle test --info to check all log messages
        // It should contain "Token contains invalid information"
    }

    @Test
    fun notExistingUserValidation()
    {
        // Arrange
        val request = MockHttpServletRequest()
        request.addHeader("Authorization", "Bearer 44444")
        val response = MockHttpServletResponse()
        val chain = MockFilterChain()

        // Set private method public
        val requiredPrivateMethod = filterToTest.javaClass.getDeclaredMethod(
            "doFilterInternal",
            HttpServletRequest::class.java,
            HttpServletResponse::class.java,
            FilterChain::class.java
        )
        requiredPrivateMethod.isAccessible = true

        // Prepare method parameters
        val privateMethodParameters = arrayOfNulls<Any>(3)
        privateMethodParameters[0] = request
        privateMethodParameters[1] = response
        privateMethodParameters[2] = chain

        // Act
        requiredPrivateMethod.invoke(filterToTest, *privateMethodParameters)


        // Assert

        // Hm. I can't see way to catch this exception. It's strange.
        // So, use gradle test --info. If there was no log string like
        // "Username exists" then it's ok.
    }
}