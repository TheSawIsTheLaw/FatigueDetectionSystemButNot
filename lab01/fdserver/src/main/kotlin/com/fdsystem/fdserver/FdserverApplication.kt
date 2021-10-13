package com.fdsystem.fdserver

import org.springframework.boot.Banner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import springfox.documentation.swagger2.annotations.EnableSwagger2

@SpringBootApplication
class FdserverApplication

fun main(args: Array<String>)
{
    runApplication<FdserverApplication>(*args) {
        setBannerMode(Banner.Mode.OFF)
    }
}
