package com.fdsystem.fdserver.controllers

import com.fdsystem.fdserver.controllers.components.JwtTokenUtil
import com.fdsystem.fdserver.controllers.jwt.JwtResponse
import com.fdsystem.fdserver.controllers.services.JwtUserDetailsService
import com.fdsystem.fdserver.controllers.services.UserAuthService
import com.fdsystem.fdserver.domain.PasswordChangeEntity
import com.fdsystem.fdserver.domain.PasswordChangeInformation
import com.fdsystem.fdserver.domain.UserCredentials
import com.fdsystem.fdserver.domain.UserCredentialsToAuth
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import org.apache.commons.logging.LogFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.DisabledException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*
import java.security.Principal
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@RestController
@CrossOrigin
@RequestMapping("api/v1/user")
class UserController(
    val userService: UserAuthService,
    val authenticationManager: AuthenticationManager,
    val jwtTokenUtil: JwtTokenUtil,
    val userDetailsService: JwtUserDetailsService
)
{
    private fun authenticate(username: String, password: String)
    {
        try
        {
            authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(username, password)
            )
        }
        catch (e: DisabledException)
        {
            throw java.lang.Exception("USER_DISABLED", e)
        }
        catch (e: BadCredentialsException)
        {
            throw java.lang.Exception("INVALID_CREDENTIALS", e)
        }
    }

    @Operation(
        summary = "Logs in user",
        description = "Logs the user into the system",
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
                Content(
                    schema = Schema(
                        implementation =
                        UserCredentialsToAuth::class
                    )
                )
            ]
        )
        @RequestBody authenticationRequest: UserCredentialsToAuth
    ): ResponseEntity<*>
    {
        try
        {
            authenticate(
                authenticationRequest.username,
                authenticationRequest.password
            )
        }
        catch (exc: Exception)
        {
            return ResponseEntity(
                "Lol, auth fucked up", HttpStatus
                    .INTERNAL_SERVER_ERROR
            )
        }

        val userDetails: UserDetails
        try
        {
            userDetails = userDetailsService
                .loadUserByUsername(authenticationRequest.username)
        }
        catch (exc: Exception)
        {
            return ResponseEntity(null, HttpStatus.NOT_FOUND)
        }

        val userDBToken = userService.getUserByUsername(
            authenticationRequest
                .username
        ).dbToken

        val token = jwtTokenUtil.generateToken(userDetails, userDBToken)
        return ResponseEntity.ok(JwtResponse(token))
    }


//    @Operation(
//        summary = "Logs out user",
//        description = "Logs out current user",
//        tags = ["Authorization"],
//        responses = [
//            io.swagger.v3.oas.annotations.responses.ApiResponse(
//                responseCode = "200",
//                description = "Successful operation"
//            )
//        ]
//    )
//    @PostMapping("/logout")
//    fun logout(response: HttpServletResponse): ResponseEntity<*>
//    {
//        return ResponseEntity(null, HttpStatus.OK)
//    }

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
    ): ResponseEntity<*>
    {
        val userRegistrationStatus: String
        try
        {
            userRegistrationStatus =
                userService.register(user.username, user.password)
        }
        catch (exc: Exception)
        {
            return ResponseEntity(null, HttpStatus.INTERNAL_SERVER_ERROR)
        }

        return if (userRegistrationStatus == "User already exists") ResponseEntity(
            null,
            HttpStatus.CONFLICT
        )
        else ResponseEntity(null, HttpStatus.OK)
    }

    @Operation(
        summary = "Change user password",
        description = "Allows to change current user's password",
        tags = ["Authorization"],
        responses = [
            io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "Successful operation"
            ),
            io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "Invalid password"
            ),
            io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "405",
                description = "Not authorized"
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
                Content(schema = Schema(implementation = PasswordChangeInformation::class))
            ]
        )
        @RequestBody changeInformation: PasswordChangeInformation,
        request: HttpServletRequest
    ): ResponseEntity<*>
    {
        val userJwtToken =
            request.getHeader("Authorization")
                .split(" ")[1]
                .trim()

        val username = jwtTokenUtil.getUsernameFromToken(userJwtToken)

        val out: Boolean
        try
        {
            out =
                userService.changeUserInfo(
                    username,
                    username,
                    changeInformation.oldPassword,
                    changeInformation.newPassword
                )
        }
        catch (exc: Exception)
        {
            return ResponseEntity(null, HttpStatus.INTERNAL_SERVER_ERROR)
        }

        return if (out) ResponseEntity(null, HttpStatus.OK)
        else ResponseEntity(
            null,
            HttpStatus.CONFLICT
        )
    }

//    @GetMapping("/lol")
//    fun testGet(request: HttpServletRequest): ResponseEntity<*>
//    {
//        return ResponseEntity(
//            jwtTokenUtil.getAllClaimsFromToken(
//                request.getHeader(
//                    "Authorization"
//                ).split(" ")[1].trim())["DBToken"], HttpStatus.OK
//        )
//    }
}