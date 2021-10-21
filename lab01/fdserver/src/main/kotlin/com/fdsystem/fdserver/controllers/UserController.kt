package com.fdsystem.fdserver.controllers

import com.fdsystem.fdserver.controllers.services.FacadeService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/user")
class UserController(val facadeService: FacadeService) {
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
    @GetMapping("/login/{username}")
    fun login(
        @Parameter(description = "Username of the user", required = true)
        @PathVariable("username") username: String,
        @Parameter(description = "Password for login", required = true)
        @RequestParam("password") password: String
    ): ResponseEntity<*> {
        try {
            facadeService.login(username, password)
        } catch (exc: Exception) {
            return ResponseEntity(null, HttpStatus.INTERNAL_SERVER_ERROR)
        }

        return if (facadeService.checkDBHealth() == "Authorized") ResponseEntity(null, HttpStatus.OK)
        else ResponseEntity(null, HttpStatus.NOT_FOUND)
    }

    @Operation(
        summary = "Logs out DB user",
        description = "Logs out current user",
        tags = ["Authorization"],
        responses = [
            io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "Successful operation"
            )
        ]
    )
    @GetMapping("/logout")
    fun logout(): ResponseEntity<*> {
        facadeService.logout()
        return ResponseEntity(null, HttpStatus.OK)
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
    @PatchMapping("/password/{username}")
    fun changePassword(
        @Parameter(description = "Username of the user", required = true, example = "satan")
        @PathVariable("username") username: String,
        @Parameter(description = "Old user's password", required = true, example = "*******")
        @RequestParam("oldPassword") oldPassword: String,
        @Parameter(description = "New user's password", required = true, example = "*******")
        @RequestParam("newPassword") newPassword: String
    ): ResponseEntity<*> {
        val out: Boolean
        try {
            out = facadeService.changeUserInfo(username, username, oldPassword, newPassword)
        } catch (exc: Exception) {
            return ResponseEntity(null, HttpStatus.INTERNAL_SERVER_ERROR)
        }

        return if (out) ResponseEntity(null, HttpStatus.OK) else ResponseEntity(null, HttpStatus.CONFLICT)
    }
}