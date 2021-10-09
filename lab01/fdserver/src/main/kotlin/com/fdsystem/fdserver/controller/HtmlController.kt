package com.fdsystem.fdserver.controller

import org.springframework.boot.web.servlet.error.ErrorController
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody


@Controller
class HtmlController: ErrorController
{
    @GetMapping("/")
    fun welcome(model: Model): String
    {
        model["title"] = "FDSystem start page"
        model["explanation"] = "Если бы знали, как долго я пытался сделать так, чтобы это работало, - вы бы расплакались."
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