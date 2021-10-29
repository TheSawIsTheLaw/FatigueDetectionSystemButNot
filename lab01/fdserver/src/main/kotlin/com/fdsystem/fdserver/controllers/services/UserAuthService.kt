package com.fdsystem.fdserver.controllers.services

import com.fdsystem.fdserver.config.NetworkConfig
import com.fdsystem.fdserver.data.UserRepositoryImpl
import com.fdsystem.fdserver.domain.dtos.NewPasswordDTOWithUsername
import com.fdsystem.fdserver.domain.dtos.UserCredentialsDTO
import com.fdsystem.fdserver.domain.logicentities.DSUserCredentials
import com.fdsystem.fdserver.domain.logicentities.USCredentialsChangeInfo
import com.fdsystem.fdserver.domain.logicentities.USUserCredentials
import org.springframework.stereotype.Service

@Service
class UserAuthService
{
    private var userRepository = UserRepositoryImpl(
        NetworkConfig.postgresUsername,
        NetworkConfig.postgresPassword
    )

    fun register(user: UserCredentialsDTO): String
    {
        val newToken = userRepository.registerUser(
            USUserCredentials(
                user
                    .username, user.password, ""
            )
        )
            .dbToken

        if (newToken.isEmpty())
        {
            return "User already exists"
        }

        return "Success"
    }

    fun userAuthSuccess(user: UserCredentialsDTO): Boolean
    {
        val token = userRepository.getUserToken(
            USUserCredentials(
                user
                    .username, user.password, ""
            )
        )
            .dbToken
        return (!(token == "User doesn't exist" || token == "Wrong password"))
    }

    fun changeUserInfo(userInfo: NewPasswordDTOWithUsername): Boolean
    {
        return userRepository.changePasswordAndUsername(
            USCredentialsChangeInfo(
                userInfo.username, userInfo.username,
                userInfo.oldPassword, userInfo.oldPassword
            )
        )
    }

    fun getUserByUsername(
        username: String
    ): DSUserCredentials
    {
        val userModel = userRepository.getUserByUsername(USUserCredentials
            (username, "", ""))
        return DSUserCredentials(
            userModel.username, userModel.password,
            userModel.dbToken
        )
    }
}