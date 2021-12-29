package com.fdsystem.fdserver.controllers.services

import com.fdsystem.fdserver.data.CharRepositoryImpl
import com.fdsystem.fdserver.data.UserRepositoryImpl
import com.fdsystem.fdserver.domain.dtos.NewPasswordDTOWithUsername
import com.fdsystem.fdserver.domain.dtos.UserCredentialsDTO
import com.fdsystem.fdserver.domain.logicentities.DSUserCredentials
import com.fdsystem.fdserver.domain.logicentities.USCredentialsChangeInfo
import com.fdsystem.fdserver.domain.logicentities.USUserCredentials
import org.springframework.stereotype.Service

@Service
class UserAuthService(
    private val userRepository: UserRepositoryImpl,
    private val influxRepository: CharRepositoryImpl
)
{
    fun register(user: UserCredentialsDTO): String
    {
        val newToken = influxRepository.getNewTokenForUser(
            USUserCredentials
            (user.username, user.password, "")
        )
        try
        {
            val registrationSuccess = userRepository.registerUser(
                USUserCredentials(
                    user.username, user.password, newToken.token
                )
            )
            if (!registrationSuccess)
            {
                return "User already exists"
            }
        }
        catch (exc: Exception)
        {
            influxRepository.deleteToken(newToken)
            throw exc
        }

        return "Success"
    }

//    fun userAuthSuccess(user: UserCredentialsDTO): Boolean
//    {
//        val token = userRepository.getUserToken(
//            USUserCredentials(
//                user
//                    .username, user.password, ""
//            )
//        )
//            .dbToken
//        return (!(token == "User doesn't exist" || token == "Wrong password"))
//    }

    fun changeUserInfo(userInfo: NewPasswordDTOWithUsername): Boolean
    {
        return userRepository.changePasswordAndUsername(
            USCredentialsChangeInfo(
                userInfo.username, userInfo.username,
                userInfo.oldPassword, userInfo.newPassword
            )
        )
    }

    fun getUserByUsername(
        username: String
    ): UserCredentialsDTO
    {
        val userModel = userRepository.getUserByUsername(
            USUserCredentials
                (username, "", "")
        )
        return UserCredentialsDTO(
            userModel.username, userModel.password,
            userModel.dbToken
        )
    }
}