package com.fdsystem.fdserver.domain.service.user

data class UserCredentials(
    val username: String,
    val password: String,
    val dbToken: String
)

data class UserCredentialsToAuth(
    val username: String,
    val password: String
)

data class PasswordChangeInformation(
    val oldPassword: String,
    val newPassword: String
)