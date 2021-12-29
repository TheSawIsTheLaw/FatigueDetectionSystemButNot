package factories

import com.fdsystem.fdserver.config.PostgresConfiguration
import com.fdsystem.fdserver.data.UserRepositoryImpl
import java.io.File

internal object UserRepositoryFactory {
    val confPath =
        "./src/integrationTest/kotlin/com/fdsystem/fdserver/config/FDPostgresConf.json"

    fun getUserRepository(): UserRepositoryImpl {
        return UserRepositoryImpl(PostgresConfiguration(File(confPath)))
    }
}