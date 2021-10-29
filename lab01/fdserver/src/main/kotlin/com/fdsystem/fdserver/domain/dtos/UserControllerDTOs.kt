package com.fdsystem.fdserver.domain.dtos

data class UserCredentialsDTO(
    val username: String,
    val password: String
)

data class NewPasswordDTOWithUsername(
    val username: String,
    val oldPassword: String,
    val newPassword: String
)

data class NewPasswordDTO(
    val oldPassword: String,
    val newPassword: String
)