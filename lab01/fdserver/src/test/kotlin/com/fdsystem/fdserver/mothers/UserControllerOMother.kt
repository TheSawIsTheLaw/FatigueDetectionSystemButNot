package com.fdsystem.fdserver.mothers

import com.fdsystem.fdserver.domain.dtos.NewPasswordDTO
import com.fdsystem.fdserver.domain.dtos.UserCredentialsDTO

data class UserControllerOMother(
    private val successValidateUsername: String = "successUser",
    private val internalServerErrorUsername: String = "internalServerErrorUser",
    private val alreadyExistUsername: String = "alreadyExistsUser",
    private val notFoundUser: String = "notFoundUser",

    private val successValidatePassword: String = "pass",
    private val invalidPassword: String = "InvalidPass",

    private val defaultDBToken: String = "",

    val successUserCredentials: UserCredentialsDTO = UserCredentialsDTO(
        successValidateUsername,
        successValidatePassword,
        defaultDBToken
    ),
    val invalidPasswordUserCredentials: UserCredentialsDTO = UserCredentialsDTO(
        successValidateUsername,
        invalidPassword,
        defaultDBToken
    ),
    val internalServerErrorUserCredentials: UserCredentialsDTO = UserCredentialsDTO(
        internalServerErrorUsername,
        successValidatePassword,
        defaultDBToken
    ),
    val alreadyExistUserCredentials: UserCredentialsDTO = UserCredentialsDTO(
        alreadyExistUsername,
        successValidatePassword,
        defaultDBToken
    ),
    val notFoundUserCredentials: UserCredentialsDTO = UserCredentialsDTO(
        notFoundUser,
        successValidatePassword,
        defaultDBToken
    ),

    private val defaultNewPassword: String = "newPas",

    val correctNewPasswordDTO: NewPasswordDTO = NewPasswordDTO("oldPas", defaultNewPassword),
    val incorrectNewPasswordDTO: NewPasswordDTO = NewPasswordDTO("IncorrectOldPas", defaultNewPassword),

    val correctJWT: String = "Cearer totototo",
    val incorrectJWT: String = "totototo",
    val serverFailJWT: String = "Bearer serverFail",
    val incorrectPasswordJWT: String = "Bearer incorrectPasUser"
)
