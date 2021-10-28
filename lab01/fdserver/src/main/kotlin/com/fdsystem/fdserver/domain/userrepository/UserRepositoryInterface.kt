package com.fdsystem.fdserver.domain.userrepository

import com.fdsystem.fdserver.domain.dtos.PasswordChangeDTO
import com.fdsystem.fdserver.domain.dtos.UserCredentialsDTO

interface UserRepositoryInterface
{
    fun userExists(username: String): Boolean

    fun checkPassword(username: String, password: String): Boolean

    fun changePasswordAndUsername(userInfo: PasswordChangeDTO): Boolean

    fun getUserByUsername(username: String): UserCredentialsDTO

    // returns Token
    fun registerUser(username: String, password: String): UserCredentialsDTO

    fun getUserToken(username: String, password: String): UserCredentialsDTO
}