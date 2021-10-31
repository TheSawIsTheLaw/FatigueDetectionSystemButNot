package com.fdsystem.fdserver.controllers.services

import com.fdsystem.fdserver.data.CharRepositoryImpl
import com.fdsystem.fdserver.domain.logicentities.DSDataAccessInfo
import com.fdsystem.fdserver.domain.logicentities.DSMeasurement
import com.fdsystem.fdserver.domain.logicentities.DSMeasurementList
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.mockito.Mockito
import java.time.Instant

internal class DataServiceTest
{
    // Все экспекты либо засандалить в подготовку данных,
    // либо сделать отдельный дата класс...
    val charRepositoryMock: CharRepositoryImpl =
        Mockito.mock(CharRepositoryImpl::class.java)

    val serviceToTest = DataService(charRepositoryMock)

    private val pulseListExampleExpectation:
            List<DSMeasurement> = listOf(
        DSMeasurement("pulse", "60", Instant.MIN),
        DSMeasurement("pulse", "63", Instant.MIN)
    )

    private val arterialPressureListExampleExpectation:
            List<DSMeasurement> = listOf(
        DSMeasurement("arterialpressure", "60", Instant.MIN),
        DSMeasurement("arterialpressure", "63", Instant.MIN)
    )

    init
    {
        Mockito.`when`(
            charRepositoryMock.get(
                DSDataAccessInfo(
                    "123",
                    "someone",
                    Pair(0, 0),
                    "pulse"
                )
            )
        ).thenReturn(pulseListExampleExpectation)

        Mockito.`when`(
            charRepositoryMock.get(
                DSDataAccessInfo(
                    "123",
                    "someone",
                    Pair(0, 0),
                    "arterialpressure"
                )
            )
        ).thenReturn(arterialPressureListExampleExpectation)

        Mockito.`when`(
            charRepositoryMock.get(
                DSDataAccessInfo(
                    "",
                    "",
                    Pair(0, 0),
                    ""
                )
            )
        ).thenReturn(listOf())
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
        assert(returnedMeasurements == pulseListExampleExpectation)
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
                DSMeasurementList("pulse", pulseListExampleExpectation),
                DSMeasurementList(
                    "arterialpressure",
                    arterialPressureListExampleExpectation
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
                DSMeasurementList("pulse", pulseListExampleExpectation)
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
    fun sendMeasurements()
    {
    }
}