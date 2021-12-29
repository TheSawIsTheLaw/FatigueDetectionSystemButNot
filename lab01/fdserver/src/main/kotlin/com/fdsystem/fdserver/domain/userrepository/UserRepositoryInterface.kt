package com.fdsystem.fdserver.domain.userrepository

import com.fdsystem.fdserver.domain.logicentities.USCredentialsChangeInfo
import com.fdsystem.fdserver.domain.logicentities.USUserCredentials

interface UserRepositoryInterface
{
    fun userExists(username: String): Boolean

    fun checkPassword(user: USUserCredentials): Boolean

    fun changePasswordAndUsername(
        credentialChangeInfo: USCredentialsChangeInfo
    ): Boolean

    fun getUserByUsername(user: USUserCredentials): USUserCredentials

    fun registerUser(user: USUserCredentials): Boolean

    fun getUserToken(user: USUserCredentials): USUserCredentials
}