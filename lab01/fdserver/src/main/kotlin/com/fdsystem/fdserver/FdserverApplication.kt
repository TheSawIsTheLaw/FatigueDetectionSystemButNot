package com.fdsystem.fdserver

import org.springframework.boot.Banner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
class FdserverApplication

fun main(args: Array<String>)
{
    runApplication<FdserverApplication>(*args) {
        setBannerMode(Banner.Mode.OFF)
    }
}
