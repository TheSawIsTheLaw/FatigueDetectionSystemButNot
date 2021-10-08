package com.fdsystem.FDSystem

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class FdSystemApplication

data class Message(val id: String?, val text: String)

fun main(args: Array<String>)
{
    runApplication<FdSystemApplication>(*args)
}
