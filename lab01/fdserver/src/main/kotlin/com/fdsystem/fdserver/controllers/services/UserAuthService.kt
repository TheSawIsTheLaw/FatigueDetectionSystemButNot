package com.fdsystem.fdserver.controllers.services

import com.fdsystem.fdserver.config.NetworkConfig
import com.fdsystem.fdserver.data.CharRepositoryImpl
import com.fdsystem.fdserver.data.UserRepositoryImpl
import org.springframework.stereotype.Service

@Service
class UserAuthService
{
    private var userRepository = UserRepositoryImpl(
        NetworkConfig.postgresUsername,
        NetworkConfig.postgresPassword
    )

    fun register(username: String, password: String): String
    {
        val newToken = userRepository.registerUser(username, password)

        if (newToken.isEmpty())
        {
            return "User already exists"
        }

        return "Success"
    }

    fun userAuthSuccess(username: String, password: String): Boolean
    {
        val token = userRepository.getUserToken(username, password)
        return (!(token == "User doesn't exist" || token == "Wrong password"))
    }

    fun changeUserInfo(
        oldUsername: String,
        newUsername: String,
        oldPassword: String,
        newPassword: String
    ): Boolean
    {
        return userRepository.changePasswordAndUsername(
            oldUsername,
            newUsername,
            oldPassword,
            newPassword
        )
    }
}