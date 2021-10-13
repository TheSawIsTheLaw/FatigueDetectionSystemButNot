package com.fdsystem.fdserver.controller

import io.swagger.annotations.*
import org.springframework.boot.web.servlet.error.ErrorController
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping


@Controller
@Api(value = "html", description = "Navigation through the pages", tags = ["Pages"])
class HtmlController : ErrorController
{
    @ApiOperation(value = "Welcome page of FDSystem")
    @GetMapping("/")
    fun welcome(model: Model): String
    {
        model["title"] = "FDSystem start page"
        model["explanation"] =
            "Если бы Вы знали, как долго я пытался сделать так, чтобы это работало, - Вы бы расплакались."
        return "welcome"
    }

    override fun getErrorPath(): String?
    {
        return "/error"
    }

    @ApiOperation(value = "Error page of FDSystem")
    @GetMapping("/error")
    fun noPage(model: Model): String
    {
        return "noPageError"
    }

}