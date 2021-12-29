package com.fdsystem.fdserver.factories

import com.fdsystem.fdserver.domain.dtos.NewPasswordDTO

class NewPasswordDTOFactory {
    fun getNewPasswordDTO(password: String) = NewPasswordDTO(password, password + "new")
    fun getNewPasswordDTOForReturnToPreviousState(password: String) = NewPasswordDTO(password + "new", password)
}