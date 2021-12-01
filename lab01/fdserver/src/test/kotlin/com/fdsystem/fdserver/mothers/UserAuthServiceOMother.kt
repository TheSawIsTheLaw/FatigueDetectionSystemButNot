package com.fdsystem.fdserver.mothers

import com.fdsystem.fdserver.domain.dtos.NewPasswordDTOWithUsername
import com.fdsystem.fdserver.domain.dtos.UserCredentialsDTO

data class UserAuthServiceOMother(
    private val defaultUsername: String = "Username",
    private val exceptionUsername: String = "excUsername",

    private val defaultPassword: String = "Password",
    private val alreadyExistPassword: String = "AnotherPasswordToCheckRetryOfRegistration",
    private val newSuccessPassword: String = "newPas",
    private val newFailPassword: String = "FailNewPas",

    private val defaultToken: String = "",

    val successRegistrationUser: UserCredentialsDTO = UserCredentialsDTO(
        defaultUsername,
        defaultPassword,
        defaultToken
    ),
    val alreadyExistUser: UserCredentialsDTO = UserCredentialsDTO(defaultUsername, alreadyExistPassword, defaultToken),

    val successChangeUserInfo: NewPasswordDTOWithUsername = NewPasswordDTOWithUsername(
        defaultUsername,
        defaultPassword,
        newSuccessPassword
    ),
    val failChangeUserInfo: NewPasswordDTOWithUsername = NewPasswordDTOWithUsername(
        defaultUsername,
        defaultPassword,
        newFailPassword
    ),
    val exceptionChangeUserInfo: NewPasswordDTOWithUsername = NewPasswordDTOWithUsername(
        exceptionUsername,
        "",
        ""
    )
)
