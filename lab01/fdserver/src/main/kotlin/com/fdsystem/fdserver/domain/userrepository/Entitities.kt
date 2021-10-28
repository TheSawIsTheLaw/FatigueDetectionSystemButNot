package com.fdsystem.fdserver.domain.userrepository

data class UserCredentials(
    val username: String,
    val password: String,
    val dbToken: String
)