package com.fdsystem.fdserver.domain.response

data class ResponseMessage(
    val code: Int,
    val message: String,
    val description: String,
)