package com.fdsystem.fdserver.controllers.services

import com.fdsystem.fdserver.data.CharRepositoryImpl
import com.fdsystem.fdserver.domain.dtos.*
import com.fdsystem.fdserver.domain.logicentities.DSDataAccessInfo
import com.fdsystem.fdserver.domain.logicentities.DSDataAddInfo
import com.fdsystem.fdserver.domain.logicentities.DSMeasurement
import com.fdsystem.fdserver.domain.logicentities.DSMeasurementList
import org.apache.commons.logging.LogFactory
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThatNoException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

import org.mockito.Mockito
import java.time.Instant

internal class DataServiceTest {
    private val charRepositoryMock: CharRepositoryImpl =
        Mockito.mock(CharRepositoryImpl::class.java)

    private val serviceToTest = DataService(charRepositoryMock)

    private data class MockExpectations(
        val pulseListExample:
        List<MeasurementData> = listOf(
            MeasurementData("60", Instant.MIN),
            MeasurementData("63", Instant.MIN)
        ),

        val dspulseListExample:
        List<DSMeasurement> = listOf(
            DSMeasurement("pulse", "60", Instant.MIN),
            DSMeasurement("pulse", "63", Instant.MIN)
        ),

        val arterialPressureListExample:
        List<MeasurementData> = listOf(
            MeasurementData("60", Instant.MIN),
            MeasurementData("63", Instant.MIN)
        ),

        val dsarterialPressureListExample:
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

    private val mockExpectations = MockExpectations()
    private val mockParameters = MockParameters()

    init {
        Mockito.`when`(
            charRepositoryMock.get(
                mockParameters.dsDataAccessInfoWithFullInitPulse
            )
        ).thenReturn(mockExpectations.dspulseListExample)

        Mockito.`when`(
            charRepositoryMock.get(
                mockParameters.dsDataAccessInfoWithFullInitArterial
            )
        ).thenReturn(mockExpectations.dsarterialPressureListExample)

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
    fun getMeasurementsWithSeveralElementsReturned() {
        // Arrange
        // Prepare parameters
        val token = "123"
        val bucketName = "someone"
        val requiredNames = listOf("pulse", "arterialpressure")

        // Act
        val returnedMeasurements =
            serviceToTest.getMeasurements(token, bucketName, requiredNames)

        // Assert
        assertEquals(
            listOf(
                MeasurementDTO(
                    "pulse",
                    mockExpectations.pulseListExample
                ),
                MeasurementDTO(
                    "arterialpressure",
                    mockExpectations.arterialPressureListExample
                )
            ),
            returnedMeasurements
        )
    }

    @Test
    fun getMeasurementsWithOneElementReturned() {
        // Arrange
        // Prepare parameters
        val token = "123"
        val bucketName = "someone"
        val requiredNames = listOf("pulse")

        // Act
        val returnedMeasurements =
            serviceToTest.getMeasurements(token, bucketName, requiredNames)

        // Assert
        assertEquals(listOf(MeasurementDTO("pulse", mockExpectations.pulseListExample)), returnedMeasurements)
    }

    @Test
    fun getMeasurementsWithNoElementsReturned() {
        // Arrange
        // Prepare parameters
        val token = ""
        val bucketName = ""
        val requiredNames = listOf<String>()

        // Act
        val returnedMeasurements =
            serviceToTest.getMeasurements(token, bucketName, requiredNames)

        // Assert
        assertEquals(listOf<DSMeasurementList>(), returnedMeasurements)
    }

    @Test
    fun sendMeasurementsTestToCheckNoException() {
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

        // Assert
        assertThatNoException()
    }
}