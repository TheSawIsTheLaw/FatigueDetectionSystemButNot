package com.fdsystem.fdserver.controllers.services

import com.fdsystem.fdserver.config.NetworkConfig
import com.fdsystem.fdserver.data.UserRepositoryImpl
import com.fdsystem.fdserver.domain.dtos.PasswordChangeDTO
import com.fdsystem.fdserver.domain.service.user.*
import org.springframework.stereotype.Service

@Service
class UserAuthService
{
    private var userRepository = UserRepositoryImpl(
        NetworkConfig.postgresUsername,
        NetworkConfig.postgresPassword
    )

    fun register(user: UserCredentialsToAuth): String
    {
        val newToken = userRepository.registerUser(user.username, user.password)
            .dbToken

        if (newToken.isEmpty())
        {
            return "User already exists"
        }

        return "Success"
    }

    fun userAuthSuccess(username: String, password: String): Boolean
    {
        val token = userRepository.getUserToken(username, password).dbToken
        return (!(token == "User doesn't exist" || token == "Wrong password"))
    }

    fun changeUserInfo(
        username: String,
        userInfo: PasswordChangeInformation
    ): Boolean
    {
        return userRepository.changePasswordAndUsername(
            PasswordChangeDTO(
                username, username,
                userInfo.oldPassword, userInfo.newPassword
            )
        )
    }

    fun getUserByUsername(
        username: String
    ): UserCredentials
    {
        val returnedDTO = userRepository.getUserByUsername(username)
        return UserCredentials(returnedDTO.username, returnedDTO.password,
            returnedDTO.dbToken)
    }
}