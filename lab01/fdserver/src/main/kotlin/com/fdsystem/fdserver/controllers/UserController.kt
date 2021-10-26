package com.fdsystem.fdserver.controllers

import com.fdsystem.fdserver.controllers.components.JwtTokenUtil
import com.fdsystem.fdserver.controllers.jwt.JwtRequest
import com.fdsystem.fdserver.controllers.jwt.JwtResponse
import com.fdsystem.fdserver.controllers.services.JwtUserDetailsService
import com.fdsystem.fdserver.controllers.services.UserAuthService
import com.fdsystem.fdserver.domain.PasswordChangeEntity
import com.fdsystem.fdserver.domain.UserCredentials
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.DisabledException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.*
import java.security.Principal
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletResponse


@RestController
@CrossOrigin
@RequestMapping("/user")
class UserController(
    val userService: UserAuthService,
    val authenticationManager: AuthenticationManager,
    val jwtTokenUtil: JwtTokenUtil,
    val userDetailsService: JwtUserDetailsService
) {
    @Throws(java.lang.Exception::class)
    private fun authenticate(username: String, password: String) {
        try {
            authenticationManager.authenticate(UsernamePasswordAuthenticationToken(username, password))
        } catch (e: DisabledException) {
            throw java.lang.Exception("USER_DISABLED", e)
        } catch (e: BadCredentialsException) {
            throw java.lang.Exception("INVALID_CREDENTIALS", e)
        }
    }

    @Operation(
        summary = "Logs in user",
        description = "Logs in user and uses his token for DB access",
        tags = ["Authorization"],
        responses = [
            io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "Successful operation"
            ),
            io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "Username not found or invalid password"
            ),
            io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "500",
                description = "Internal server error"
            )
        ]
    )
    @PostMapping("/login")
    fun login(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "User credentials", required = true, content = [
                Content(schema = Schema(implementation = UserCredentials::class))
            ]
        )
        @RequestBody authenticationRequest: JwtRequest
    ): ResponseEntity<*> {
        authenticate(authenticationRequest.username, authenticationRequest.password)
        val userDetails = userDetailsService
            .loadUserByUsername(authenticationRequest.username)
        val token = jwtTokenUtil.generateToken(userDetails)
        return ResponseEntity.ok<Any>(JwtResponse(token))
    }


    @Operation(
        summary = "Logs out user",
        description = "Logs out current user",
        tags = ["Authorization"],
        responses = [
            io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "Successful operation"
            )
        ]
    )

    @PostMapping("/logout")
    fun logout(response: HttpServletResponse): ResponseEntity<*> {
        val authCookie = Cookie("FUCKING STUPID COOKIE uwu", null)
        authCookie.maxAge = 0
        authCookie.isHttpOnly = true
        authCookie.path = "/"
        authCookie.secure = true

        response.addCookie(authCookie)
        return ResponseEntity(null, HttpStatus.OK)
    }

    @Operation(
        summary = "Register user",
        description = "Register user by given username and password",
        tags = ["Authorization"],
        responses = [
            io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "Successful operation"
            ),
            io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "409",
                description = "Username is already busy"
            ),
            io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "500",
                description = "Internal server error"
            )
        ]
    )
    @PostMapping("/registration")
    fun register(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "User credentials", required = true, content = [
                Content(schema = Schema(implementation = UserCredentials::class))
            ]
        )
        @RequestBody user: UserCredentials
    ): ResponseEntity<*> {
        val outAnswer: String
        try {
            outAnswer = userService.register(user.username, user.password)
        } catch (exc: Exception) {
            println(exc.toString())
            return ResponseEntity(null, HttpStatus.INTERNAL_SERVER_ERROR)
        }

        return if (outAnswer == "User already exists") ResponseEntity(null, HttpStatus.CONFLICT)
        else ResponseEntity(null, HttpStatus.OK)
    }

    @Operation(
        summary = "Change user info",
        description = "Allows to change user's password",
        tags = ["Authorization"],
        responses = [
            io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "Successful operation"
            ),
            io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "Username not found or invalid password"
            ),
            io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "500",
                description = "Internal server error"
            )
        ]
    )

    @PatchMapping("/password")
    fun changePassword(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Old and new passwords", required = true, content = [
                Content(schema = Schema(implementation = PasswordChangeEntity::class))
            ]
        )
        principal: Principal,
        @RequestBody passwords: PasswordChangeEntity
    ): ResponseEntity<*> {
        if (principal.name == null)
            return ResponseEntity(null, HttpStatus.UNAUTHORIZED)

        val out: Boolean
        try {
            out =
                userService.changeUserInfo(principal.name, principal.name, passwords.oldPassword, passwords.newPassword)
        } catch (exc: Exception) {
            return ResponseEntity(null, HttpStatus.INTERNAL_SERVER_ERROR)
        }

        return if (out) ResponseEntity(null, HttpStatus.OK) else ResponseEntity(null, HttpStatus.CONFLICT)
    }
}