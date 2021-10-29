package com.fdsystem.fdserver.controllers

import com.fdsystem.fdserver.controllers.components.JwtTokenUtil
import com.fdsystem.fdserver.controllers.jwt.JwtResponse
import com.fdsystem.fdserver.controllers.services.JwtUserDetailsService
import com.fdsystem.fdserver.controllers.services.UserAuthService
import com.fdsystem.fdserver.domain.response.ResponseCreator
import com.fdsystem.fdserver.domain.response.ResponseMessage
import com.fdsystem.fdserver.domain.service.user.PasswordChangeInformation
import com.fdsystem.fdserver.domain.service.user.UserCredentials
import com.fdsystem.fdserver.domain.service.user.UserCredentialsToAuth
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.http.HttpCookie
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.web.bind.annotation.*
import java.net.URLEncoder
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpUtils


@RestController
@CrossOrigin
@RequestMapping("api/v1/user")
class UserController(
    val userService: UserAuthService,
//    val authenticationManager: AuthenticationManager,
    val jwtTokenUtil: JwtTokenUtil,
    val userDetailsService: JwtUserDetailsService
)
{
//    private fun authenticate(username: String, password: String)
//    {
//        try
//        {
//            authenticationManager.authenticate(
//                UsernamePasswordAuthenticationToken(username, password)
//            )
//        }
//        catch (e: DisabledException)
//        {
//            throw java.lang.Exception("USER_DISABLED", e)
//        }
//        catch (e: BadCredentialsException)
//        {
//            throw java.lang.Exception("INVALID_CREDENTIALS", e)
//        }
//    }

    @Operation(
        summary = "Logs in user",
        description = "Logs the user into the system",
        tags = ["Authorization"],
        responses = [
            io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "Successful operation",
                content = [
                    Content(
                        schema = Schema(
                            implementation =
                            JwtResponse::class
                        )
                    )]
            ),
            io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "Username not found or invalid password",
                content = [
                    Content(
                        schema = Schema(
                            implementation =
                            ResponseMessage::class
                        )
                    )]
            ),
            io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "500",
                description = "Internal server error",
                content = [
                    Content(
                        schema = Schema(
                            implementation =
                            ResponseMessage::class
                        )
                    )]
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
        @RequestBody authenticationRequest: UserCredentialsToAuth,
//        response: HttpServletResponse
    ): ResponseEntity<*>
    {
//        try
//        {
//            authenticate(
//                authenticationRequest.username,
//                authenticationRequest.password
//            )
//        }
//        catch (exc: Exception)
//        {
//            return ResponseEntity(
//                "hm.", HttpStatus
//                    .INTERNAL_SERVER_ERROR
//            )
//        }
        val userDetails: UserDetails
        try
        {
            userDetails = userDetailsService
                .loadUserByUsername(authenticationRequest.username)
        }
        catch (exc: UsernameNotFoundException)
        {
            return ResponseCreator.userNotFoundResponse(
                "User not found",
                "If you are unregistered - try to go to /registration"
            )
        }
        catch (exc: Exception)
        {
            return ResponseCreator.internalServerErrorResponse(
                "Something terrible with Postgres...",
                "Try again later or send a message to a developer"
            )
        }

        if (authenticationRequest.password != userDetails.password)
        {
            return ResponseCreator.userNotFoundResponse(
                "User not found or invalid password",
                "If you are unregistered - try to go to /registration"
            )
        }

        val userDBToken = userService.getUserByUsername(
            authenticationRequest.username
        ).dbToken

        val token = jwtTokenUtil.generateToken(userDetails, userDBToken)
//        val jwtTokenCookie = Cookie("FDSystemAuth", token)
//        jwtTokenCookie.maxAge = 86400
//        jwtTokenCookie.secure = true
//        jwtTokenCookie.isHttpOnly = true
//        jwtTokenCookie.path = "/api/v1/data"

//        response.addCookie(jwtTokenCookie)

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
                description = "Successful operation",
                content = [
                    Content(
                        schema = Schema(
                            implementation =
                            ResponseMessage::class
                        )
                    )]
            ),
            io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "409",
                description = "Username is already busy",
                content = [
                    Content(
                        schema = Schema(
                            implementation =
                            ResponseMessage::class
                        )
                    )]
            ),
            io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "500",
                description = "Internal server error",
                content = [
                    Content(
                        schema = Schema(
                            implementation =
                            ResponseMessage::class
                        )
                    )]
            )
        ]
    )
    @PostMapping("/registration")
    fun register(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "User credentials", required = true, content = [
                Content(schema = Schema(implementation = UserCredentialsToAuth::class))
            ]
        )
        @RequestBody user: UserCredentialsToAuth
    ): ResponseEntity<*>
    {
        val userRegistrationStatus: String
        try
        {
            userRegistrationStatus =
                userService.register(user)
        }
        catch (exc: Exception)
        {
            return ResponseCreator.internalServerErrorResponse(
                "Auth server is dead :(",
                "Let's dance on its grave!"
            )
        }

        if (userRegistrationStatus == "User already exists")
        {
            val responseMessage = ResponseMessage(
                409,
                "User already exists",
                "Try another username ;)"
            )
            return ResponseEntity(responseMessage, HttpStatus.CONFLICT)
        }
        else
        {
            return ResponseCreator.okResponse(
                "Success!",
                "You can now log into the system!"
            )
        }
    }

    @Operation(
        summary = "Change user password",
        description = "Allows to change current user's password",
        tags = ["Authorization"],
        responses = [
            io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "Successful operation",
                content = [
                    Content(
                        schema = Schema(
                            implementation =
                            ResponseMessage::class
                        )
                    )]
            ),
            io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "Invalid password",
                content = [
                    Content(
                        schema = Schema(
                            implementation =
                            ResponseMessage::class
                        )
                    )]
            ),
            io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "401",
                description = "Not authorized"
            ),
            io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "500",
                description = "Internal server error",
                content = [
                    Content(
                        schema = Schema(
                            implementation =
                            ResponseMessage::class
                        )
                    )]
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
                @Parameter(
            description = "User JWToken",
            required = true
        )
        @RequestHeader("Authorization") jwtToken: String
//        @Parameter(
//            description = "User JWToken",
//            required = true
//        )
//        @CookieValue("FDSystemAuth") jwtToken: String
    ): ResponseEntity<*>
    {
        val userJwtToken = jwtToken.split(" ")[1].trim()

        val username = jwtTokenUtil.getUsernameFromToken(userJwtToken)

        val out: Boolean
        try
        {
            out = userService.changeUserInfo(username, changeInformation)
        }
        catch (exc: Exception)
        {
            val responseMessage = ResponseMessage(
                500,
                "Auth server is dead :(",
                "Let's dance on its grave!"
            )
            return ResponseEntity(
                responseMessage,
                HttpStatus.INTERNAL_SERVER_ERROR
            )
        }

        if (out)
        {
            return ResponseCreator.okResponse(
                "Password changed successfully",
                "It's your new life!.."
            )
        }
        else
        {
            val responseMessage = ResponseMessage(
                409,
                "Password wasn't changed",
                "Type your current password carefully"
            )
            return ResponseEntity(responseMessage, HttpStatus.CONFLICT)
        }
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