package com.fdsystem.fdserver.domain.userrepository

import com.fdsystem.fdserver.domain.UserCredentials

interface UserRepositoryInterface {
    fun userExists(username: String): Boolean

    fun checkPassword(username: String, password: String): Boolean

    fun changePasswordAndUsername(
        oldUsername: String,
        newUsername: String,
        oldPassword: String,
        newPassword: String
    ): Boolean

    fun getUserByUsername(username: String): UserCredentials

    // returns Token
    fun registerUser(username: String, password: String): String

    fun getUserToken(username: String, password: String): String
}