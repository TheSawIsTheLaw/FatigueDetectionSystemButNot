package com.fdsystem.fdserver.controllers.jwt

import com.fdsystem.fdserver.controllers.components.JwtTokenUtil
import com.fdsystem.fdserver.controllers.services.JwtUserDetailsService
import io.jsonwebtoken.ExpiredJwtException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@Component
class JwtRequestFilter(
    private val jwtUserDetailsService: JwtUserDetailsService,
    private val jwtTokenUtil: JwtTokenUtil
) : OncePerRequestFilter()
{
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain
    )
    {
        val requestTokenHeader = request.getHeader("Authorization")
        var username: String? = null
        var jwtToken: String? = null
        // JWT Token is in the form "Bearer token". Remove Bearer word and get
        // only the Token
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer "))
        {
            jwtToken = requestTokenHeader.split(" ")[1].trim()
            try
            {
                username = jwtTokenUtil.getUsernameFromToken(jwtToken)
                logger.debug("Token is ok, it's $username")
            }
            catch (e: IllegalArgumentException)
            {
                logger.debug("Unable to get JWT Token")
            }
            catch (e: ExpiredJwtException)
            {
                logger.debug("JWT Token has expired")
            }
        }
        else
        {
            logger.debug("JWT Token does not begin with Bearer String")
        }

        // Once we get the token validate it.
        if (username != null && SecurityContextHolder.getContext().authentication == null)
        {
            val userDetails =
                jwtUserDetailsService.loadUserByUsername(username)
            logger.debug("Username exists")

            // if token is valid configure Spring Security to manually set
            // authentication
            if (jwtTokenUtil.validateToken(jwtToken, userDetails))
            {
                logger.debug("Token contains valid information")
                val usernamePasswordAuthenticationToken =
                    UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.authorities
                    )
                usernamePasswordAuthenticationToken.details =
                    WebAuthenticationDetailsSource().buildDetails(request)
                // After setting the Authentication in the context, we specify
                // that the current user is authenticated. So it passes the
                // Spring Security Configurations successfully.
                SecurityContextHolder.getContext().authentication =
                    usernamePasswordAuthenticationToken
            }
            else
            {
                logger.debug("Token contains invalid information")
            }
        }
        chain.doFilter(request, response)
    }
}