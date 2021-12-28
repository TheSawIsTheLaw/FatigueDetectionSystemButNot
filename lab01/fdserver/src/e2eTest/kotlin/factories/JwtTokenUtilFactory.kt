package factories

import com.fdsystem.fdserver.controllers.components.JwtTokenUtil

internal object JwtTokenUtilFactory {
    fun createJwtTokenUtilWithDefaultSecret(): JwtTokenUtil {
        val utilToReturn = JwtTokenUtil()
        org.springframework.test.util.ReflectionTestUtils.setField(
            utilToReturn,
            "secret",
            "LZKPDJMW7MSY273666MZNAAAUCJEBASUKOXK666777A"
        )

        return utilToReturn
    }
}