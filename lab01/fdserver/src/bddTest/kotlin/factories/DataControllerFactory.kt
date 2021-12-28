package factories

import com.fdsystem.fdserver.controllers.DataController
import com.fdsystem.fdserver.controllers.services.DataService

internal object DataControllerFactory {
    private val charRepositoryFactory = CharRepositoryFactory
    private val jwtTokenUtilFactory = JwtTokenUtilFactory

    fun getDataController() = DataController(
        DataService(charRepositoryFactory.getCharRepository()),
        jwtTokenUtilFactory.createJwtTokenUtilWithDefaultSecret()
    )
}