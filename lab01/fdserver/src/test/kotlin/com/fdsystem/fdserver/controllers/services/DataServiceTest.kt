package com.fdsystem.fdserver.controllers.services

import com.fdsystem.fdserver.data.CharRepositoryImpl
import com.fdsystem.fdserver.domain.dtos.AcceptMeasurementsDTO
import com.fdsystem.fdserver.domain.dtos.AcceptMeasurementsListDTO
import com.fdsystem.fdserver.domain.dtos.MeasurementDataWithoutTime
import com.fdsystem.fdserver.domain.logicentities.DSDataAccessInfo
import com.fdsystem.fdserver.domain.logicentities.DSDataAddInfo
import com.fdsystem.fdserver.domain.logicentities.DSMeasurement
import com.fdsystem.fdserver.domain.logicentities.DSMeasurementList
import org.junit.jupiter.api.Test

import org.mockito.Mockito
import java.time.Instant

internal class DataServiceTest
{
    val charRepositoryMock: CharRepositoryImpl =
        Mockito.mock(CharRepositoryImpl::class.java)

    val serviceToTest = DataService(charRepositoryMock)

    private data class MockExpectation(
        val pulseListExample:
        List<DSMeasurement> = listOf(
            DSMeasurement("pulse", "60", Instant.MIN),
            DSMeasurement("pulse", "63", Instant.MIN)
        ),

        val arterialPressureListExample:
        List<DSMeasurement> = listOf(
            DSMeasurement("arterialpressure", "60", Instant.MIN),
            DSMeasurement("arterialpressure", "63", Instant.MIN)
        )
    )

    private data class MockParameters(
        val dsDataAccessInfoWithFullInitPulse: DSDataAccessInfo =
            DSDataAccessInfo(
                "123",
                "someone",
                Pair(0, 0),
                "pulse"
            ),

        val dsDataAccessInfoWithFullInitArterial: DSDataAccessInfo =
            DSDataAccessInfo(
                "123",
                "someone",
                Pair(0, 0),
                "arterialpressure"
            ),

        val dsDataInfoEmpty: DSDataAccessInfo =
            DSDataAccessInfo(
                "",
                "",
                Pair(0, 0),
                ""
            ),

        val dsDataAddInfoWithPulse: DSDataAddInfo =
            DSDataAddInfo(
                "123", "someone", DSMeasurementList(
                    "pulse", listOf(
                        DSMeasurement("pulse", "34", Instant.MIN),
                        DSMeasurement("pulse", "36", Instant.MIN)
                    )
                )
            ),

        val dsDataAddInfoWithArterial: DSDataAddInfo =
            DSDataAddInfo(
                "123", "someone", DSMeasurementList(
                    "arterialpressure", listOf(
                        DSMeasurement(
                            "arterialpressure", "100",
                            Instant.MIN
                        ),
                        DSMeasurement(
                            "arterialpressure", "200",
                            Instant.MIN
                        )
                    )
                )
            )
    )

    private val mockExpectation = MockExpectation()
    private val mockParameters = MockParameters()

    init
    {
        Mockito.`when`(
            charRepositoryMock.get(
                mockParameters.dsDataAccessInfoWithFullInitPulse
            )
        ).thenReturn(mockExpectation.pulseListExample)

        Mockito.`when`(
            charRepositoryMock.get(
                mockParameters.dsDataAccessInfoWithFullInitArterial
            )
        ).thenReturn(mockExpectation.arterialPressureListExample)

        Mockito.`when`(
            charRepositoryMock.get(
                mockParameters.dsDataInfoEmpty
            )
        ).thenReturn(listOf())

        Mockito.doNothing().`when`(charRepositoryMock).add(
            mockParameters.dsDataAddInfoWithPulse
        )

        Mockito.doNothing().`when`(charRepositoryMock).add(
            mockParameters.dsDataAddInfoWithArterial
        )
    }

    @Test
    fun getMeasurementWithFullListReturned()
    {
        // Arrange
        // Prepare parameters
        val token = "123"
        val bucketName = "someone"
        val charName = "pulse"

        // Set private method public
        val requiredPrivateMethod =
            serviceToTest.javaClass.getDeclaredMethod(
                "getMeasurement",
                String::class.java, String::class.java, String::class.java
            )
        requiredPrivateMethod.isAccessible = true

        // Prepare method parameters
        val privateMethodParameters = arrayOfNulls<Any>(3)
        privateMethodParameters[0] = token
        privateMethodParameters[1] = bucketName
        privateMethodParameters[2] = charName

        // Act
        val returnedMeasurements =
            requiredPrivateMethod.invoke(
                serviceToTest,
                *privateMethodParameters
            )

        // Assert
        assert(returnedMeasurements == mockExpectation.pulseListExample)
    }

    @Test
    fun getMeasurementWithEmptyListReturned()
    {
        // Arrange
        // Prepare parameters
        val token = ""
        val bucketName = ""
        val charName = ""

        // Set private method public
        val requiredPrivateMethod =
            serviceToTest.javaClass.getDeclaredMethod(
                "getMeasurement",
                String::class.java, String::class.java, String::class.java
            )
        requiredPrivateMethod.isAccessible = true

        // Prepare method parameters
        val privateMethodParameters = arrayOfNulls<Any>(3)
        privateMethodParameters[0] = token
        privateMethodParameters[1] = bucketName
        privateMethodParameters[2] = charName

        // Act
        val returnedMeasurements =
            requiredPrivateMethod.invoke(
                serviceToTest,
                *privateMethodParameters
            )

        // Assert
        assert(returnedMeasurements == listOf<DSMeasurement>())
    }

    @Test
    fun getMeasurementsWithSeveralElementsReturned()
    {
        // Arrange
        // Prepare parameters
        val token = "123"
        val bucketName = "someone"
        val requiredNames = listOf("pulse", "arterialpressure")

        // Act
        val returnedMeasurements =
            serviceToTest.getMeasurements(token, bucketName, requiredNames)

        // Assert
        assert(
            returnedMeasurements == listOf<DSMeasurementList>(
                DSMeasurementList(
                    "pulse",
                    mockExpectation.pulseListExample
                ),
                DSMeasurementList(
                    "arterialpressure",
                    mockExpectation.arterialPressureListExample
                )
            )
        )
    }

    @Test
    fun getMeasurementsWithOneElementReturned()
    {
        // Arrange
        // Prepare parameters
        val token = "123"
        val bucketName = "someone"
        val requiredNames = listOf("pulse")

        // Act
        val returnedMeasurements =
            serviceToTest.getMeasurements(token, bucketName, requiredNames)

        // Assert
        assert(
            returnedMeasurements == listOf<DSMeasurementList>(
                DSMeasurementList("pulse", mockExpectation.pulseListExample)
            )
        )
    }

    @Test
    fun getMeasurementsWithNoElementsReturned()
    {
        // Arrange
        // Prepare parameters
        val token = ""
        val bucketName = ""
        val requiredNames = listOf<String>()

        // Act
        val returnedMeasurements =
            serviceToTest.getMeasurements(token, bucketName, requiredNames)

        // Assert
        assert(returnedMeasurements == listOf<DSMeasurementList>())
    }

    @Test
    fun sendMeasurementTestToCheckNoException()
    {
        // Arrange
        val token = "123"
        val bucketName = "someone"
        val charName = "pulse"
        val chars = listOf(
            MeasurementDataWithoutTime("34"),
            MeasurementDataWithoutTime("36")
        )

        // Set private method public
        val requiredPrivateMethod = serviceToTest.javaClass.getDeclaredMethod(
            "sendMeasurement",
            String::class.java, String::class.java, String::class.java,
            java.util.List::class.java
        )
        requiredPrivateMethod.isAccessible = true

        // Prepare method parameters
        val privateMethodParameters = arrayOfNulls<Any>(4)
        privateMethodParameters[0] = token
        privateMethodParameters[1] = bucketName
        privateMethodParameters[2] = charName
        privateMethodParameters[3] = chars

        // Act
        requiredPrivateMethod.invoke(
            serviceToTest,
            *privateMethodParameters
        )

        // If there is no exception then it's ok
    }

    @Test
    fun sendMeasurementsTestToCheckNoException()
    {
        // Arrange
        val token = "123"
        val bucketName = "someone"
        val measurementList = AcceptMeasurementsListDTO(
            listOf(
                AcceptMeasurementsDTO(
                    "pulse", listOf(
                        MeasurementDataWithoutTime("34"),
                        MeasurementDataWithoutTime("36")
                    )
                ),
                AcceptMeasurementsDTO(
                    "arterialpressure", listOf(
                        MeasurementDataWithoutTime("100"),
                        MeasurementDataWithoutTime("200")
                    )
                )
            )
        )

        // Act
        serviceToTest.sendMeasurements(token, bucketName, measurementList)

        // If there is no exception then it's ok
    }
}