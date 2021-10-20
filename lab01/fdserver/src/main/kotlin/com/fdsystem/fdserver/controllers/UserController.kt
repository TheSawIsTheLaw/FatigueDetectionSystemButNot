package com.fdsystem.fdserver.controllers

import com.fdsystem.fdserver.config.NetworkConfig
import com.fdsystem.fdserver.controllers.services.FacadeService
import io.swagger.annotations.*
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/user")
@Api(value = "userApi", description = "Api for users of a system", tags = ["User API"])
class UserController(val facadeService: FacadeService)
{
    @ApiOperation(value = "Logs in DB user", response = String::class)
    @ApiParam(name = "token", value = "Token for DB access", required = true, example = "OAOAOOAOAAOMMMMMMM")
    @ApiImplicitParams(
        *[
            ApiImplicitParam(
                name = "org", value = "User's organisation for DB access", required = true,
                example = "Kidnappers"
            )
        ]
    )
    @ApiResponses(
        value = [
            ApiResponse(code = 200, message = "Success"),
            ApiResponse(code = 401, message = "Illegal token, address or organization")
        ]
    )
    // Нормальные респонсы
    @GetMapping("/login/{token}")
    fun login(
        @PathVariable("token") token: String,
        @RequestParam("org") org: String
    ): String
    {
        facadeService.loginToInflux(NetworkConfig.influxdbURL, token, org)
        return facadeService.checkDBHealth()
    }

    @ApiOperation(value = "Logs out DB user", response = String::class)
    @ApiResponse(code = 200, message = "Success")
    @GetMapping("/logout")
    // Респонсы
    fun logout(): String
    {
        facadeService.logout()
        return "Done"
    }

    ///--- Добавлен PATCH метод
    @ApiOperation(value = "Change user info", response = String::class)
    @ApiParam(name = "username", value = "Name of user", required = true, example = "Mark")
    @ApiImplicitParams(
        *[
            ApiImplicitParam(
                name = "oldPassword", value = "Old user's password", required = true,
                example = "*******"
            ),
            ApiImplicitParam(
                name = "newPassword", value = "New user's password", required = true,
                example = "*******"
            )
        ]
    )
    @ApiResponse(code = 200, message = "Success")
    // респонс на существующего пользователя
    // руспонс на 500
    @PatchMapping("/changePassword/{username}")
    fun relogin(
        @PathVariable("username") username: String,
        @RequestParam("oldPassword") oldPassword: String,
        @RequestParam("newPassword") newPassword: String
    ): String
    {
        val out = facadeService.changeUserInfo(username, username, oldPassword, newPassword)
        return if (out) "Success" else "User already exists"
    }
}