package factories

import com.fdsystem.fdserver.config.InfluxdbConfiguration
import com.fdsystem.fdserver.data.CharRepositoryImpl
import java.io.File

internal object CharRepositoryFactory {
    val confPath =
        "./src/integrationTest/kotlin/com/fdsystem/fdserver/config/FDInfluxConf.json"

    fun getCharRepository(): CharRepositoryImpl {
        return CharRepositoryImpl(InfluxdbConfiguration(File(confPath)))
    }
}