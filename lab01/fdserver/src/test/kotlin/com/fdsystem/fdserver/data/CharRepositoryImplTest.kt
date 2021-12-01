package com.fdsystem.fdserver.data

import com.fdsystem.fdserver.config.InfluxdbConfiguration
import com.fdsystem.fdserver.domain.logicentities.DSDataAccessInfo
import com.fdsystem.fdserver.domain.logicentities.DSMeasurement
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.File

internal class CharRepositoryImplTest {
    val confPath =
        "./src/test/kotlin/com/fdsystem/fdserver/config/FDInfluxConf.json"
    val configuration = InfluxdbConfiguration(File(confPath))

    val repositoryToTest = CharRepositoryImpl(configuration)

//    private data class MockParameters(
//        val successQuery: String = "from(bucket: \"bucket\"\n" +
//                "|> range(start: 0)\n" +
//                "|> filter(fn: (r) => (r[\"_measurement\"] == \"measName\"))"
//    )
//
//    private val mockParameters = MockParameters()
//
//    private data class MockExpectations(
//        val i: Int = 1
//    )
//
//    private val mockExpectations = MockExpectations()

    init {
        // dbClientMock
        // I can't create Table by myself and creating Channel<FluxRecord>, sry
    }

    @Test
    fun getTestSuccess() {
        // Arrange
        val token = "token"
        val bucketName = "bucket"
        val timeRange = Pair(0, 0)
        val measurementName = "measName"
        val dataAccessInfo = DSDataAccessInfo(
            token, bucketName, timeRange,
            measurementName
        )

        // Prepare private method
        val requiredPrivateMethod =
            repositoryToTest.javaClass.getDeclaredMethod(
                "get",
                DSDataAccessInfo::class.java,
                InfluxConnection::class.java
            )
        requiredPrivateMethod.isAccessible = true

        // Prepare parameters
        val requiredParameters = arrayOfNulls<Any>(2)
        requiredParameters[0] = dataAccessInfo
        requiredParameters[1] =
            InfluxConnection("http://localhost:8086", "1", "1")

        var causeOfException = ""

        // Act
        var gotData: List<DSMeasurement>? = null
        try {
            gotData = requiredPrivateMethod.invoke(
                repositoryToTest,
                *requiredParameters
            ) as List<DSMeasurement>
        } catch (exc: Exception) {
            causeOfException = exc.cause.toString()
        }

        // Assert
        assertNull(gotData)
        assertTrue(causeOfException.indexOf("Failed to connect") >= 1)
    }
}