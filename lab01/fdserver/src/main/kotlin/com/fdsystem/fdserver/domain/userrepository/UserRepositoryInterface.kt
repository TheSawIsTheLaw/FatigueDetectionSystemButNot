package com.fdsystem.fdserver.domain.userrepository

interface UserRepositoryInterface
{
    fun userExists(username: String): Boolean

    fun checkPassword(username: String, password: String): Boolean

    fun changePasswordAndUsername(
        oldUsername: String,
        newUsername: String,
        oldPassword: String,
        newPassword: String
    ): Boolean

    fun registerUser(username: String, password: String): Boolean

    fun getUserToken(username: String, password: String): String
}