package com.fdsystem.fdserver.domain.userrepository

import com.fdsystem.fdserver.domain.logicentities.USCredentialsChangeInfo
import com.fdsystem.fdserver.domain.logicentities.USUserCredentials
import com.fdsystem.fdserver.domain.models.UserModel

interface UserRepositoryInterface
{
    fun userExists(username: String): Boolean

    fun checkPassword(user: USUserCredentials): Boolean

    fun changePasswordAndUsername(
        credentialChangeInfo: USCredentialsChangeInfo
    ): Boolean

    fun getUserByUsername(user: USUserCredentials): UserModel

    fun registerUser(user: USUserCredentials): UserModel

    fun getUserToken(user: USUserCredentials): UserModel
}