package factories

import com.fdsystem.fdserver.controllers.UserController
import com.fdsystem.fdserver.controllers.services.JwtUserDetailsService
import com.fdsystem.fdserver.controllers.services.UserAuthService

internal object UserControllerFactory {
    private val userRepository = UserRepositoryFactory.getUserRepository()
    private val charRepository = CharRepositoryFactory.getCharRepository()
    private val jwtTokenUtilFactory = JwtTokenUtilFactory

    fun getUserController() = UserController(
        UserAuthService(userRepository, charRepository),
        jwtTokenUtilFactory.createJwtTokenUtilWithDefaultSecret(),
        JwtUserDetailsService(userRepository)
    )
}