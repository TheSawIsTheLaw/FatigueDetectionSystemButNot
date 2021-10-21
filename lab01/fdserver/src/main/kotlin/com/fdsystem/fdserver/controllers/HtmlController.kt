package com.fdsystem.fdserver.controllers

import io.swagger.annotations.*
import org.springframework.boot.web.servlet.error.ErrorController
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping


@Controller
class HtmlController : ErrorController
{
    @GetMapping("/welcome")
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

    @GetMapping("/error")
    fun noPage(model: Model): String
    {
        return "noPageError"
    }

}