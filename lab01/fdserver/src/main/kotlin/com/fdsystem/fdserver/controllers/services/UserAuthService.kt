package com.fdsystem.fdserver.controllers.services

import com.fdsystem.fdserver.config.NetworkConfig
import com.fdsystem.fdserver.data.CharRepositoryImpl
import com.fdsystem.fdserver.data.UserRepositoryImpl

class UserAuthService
{
    private var userRepository = UserRepositoryImpl(NetworkConfig.postgresUsername, NetworkConfig.postgresPassword)

    fun register(username: String, password: String): String
    {
        val newToken = userRepository.registerUser(username, password)

        if (newToken.isEmpty())
        {
            return "User already exists"
        }

        return "Success"
    }

    fun login(username: String, password: String): CharRepositoryImpl?
    {
        val token = userRepository.getUserToken(username, password)
        if (token == "User doesn't exist" || token == "Wrong password")
        {
            return null
        }

        return CharRepositoryImpl(NetworkConfig.influxdbURL, token, NetworkConfig.influxOrganization)
    }

    fun changeUserInfo(oldUsername: String, newUsername: String, oldPassword: String, newPassword: String): Boolean
    {
        return userRepository.changePasswordAndUsername(oldUsername, newUsername, oldPassword, newPassword)
    }
}