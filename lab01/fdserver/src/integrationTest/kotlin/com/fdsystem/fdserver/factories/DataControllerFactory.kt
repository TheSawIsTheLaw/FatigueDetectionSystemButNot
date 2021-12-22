package com.fdsystem.fdserver.factories

import com.fdsystem.fdserver.controllers.DataController
import com.fdsystem.fdserver.controllers.services.DataService

class DataControllerFactory {
    private val charRepositoryFactory = CharRepositoryFactory()
    private val jwtTokenUtilFactory = JwtTokenUtilFactory()

    private val defaultController: DataController by lazy {
        DataController(
            DataService(charRepositoryFactory.getCharRepository()),
            jwtTokenUtilFactory.createJwtTokenUtilWithDefaultSecret()
        )
    }

    fun getDataController() = defaultController
}