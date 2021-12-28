package UserScenarioAddAndCheck

import com.fdsystem.fdserver.controllers.jwt.JwtResponse
import com.fdsystem.fdserver.domain.dtos.ResponseMeasurementsDTO
import com.fdsystem.fdserver.factories.DataControllerFactory
import com.fdsystem.fdserver.factories.MeasurementsListFactory
import com.fdsystem.fdserver.factories.UserControllerFactory
import com.fdsystem.fdserver.factories.UserCredentialsDTOFactory
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Test

internal class UserScenarioTest {
    private val userControllerFactory = UserControllerFactory()
    private val dataControllerFactory = DataControllerFactory()
    private val userCredentialsDTOFactory = UserCredentialsDTOFactory()
    private val measurementsListFactory = MeasurementsListFactory()

    private val userController = userControllerFactory.getUserController()
    private val dataController = dataControllerFactory.getDataController()

//    private var numberOfExecutions: Int = 100

//    init {
//        numberOfExecutions = System.getProperty("numberOfExecutions").toInt()
//    }

    // sudo gradle e2etest --info --rerun-tasks -DnOfExecs=100
    @Test
    fun userScenarioNTests() {
        var numberOfSuccessExecutions = 0
        val numberOfExecutions = try {
            System.getProperty("numberOfExecutions").toInt()
        } catch (exc: Exception) {
            throw Exception("No parameter provided for number of Executions: use -DnOfExec=120 for example")
        }

        for (i in 0 until numberOfExecutions) {
            numberOfSuccessExecutions += runScenarioTest(i)
        }

        println("Successful $numberOfSuccessExecutions of $numberOfExecutions")
        assertEquals(numberOfSuccessExecutions, numberOfExecutions)
    }

    // Почему иф-ы?
    // Да потому что в задании требуется узнать, сколько тестов не прошло.
//    @RepeatedTest(100)
    private fun runScenarioTest(numberOfExecution: Int): Int {
        // Arrange
        val userCredentials = userCredentialsDTOFactory.getExistingUser()

        // Act
        val gotLoginResponse = userController.login(userCredentials)

        // Assert
        if (gotLoginResponse.statusCode.isError || (gotLoginResponse.body as JwtResponse).token.isBlank()) {
            println("Login error")
            return 0
        }

        // Arrange
        val userJwtToken = "Bearer " + (gotLoginResponse.body as JwtResponse).token
        val measurementsToAdd = measurementsListFactory.getMeasurementsToAdd(numberOfExecution)

        // Act
        val gotAddResponse = dataController.addData(measurementsToAdd, userJwtToken)

        // Assert
        if (gotAddResponse.statusCode.isError) {
            println("Add data error")
            return 0
        }

        // Arrange
        val measurementsToGet = measurementsListFactory.getMeasurementsListWithBotArterialPressure()

        // Act
        val gotGetResponse = dataController.getData(measurementsToGet, userJwtToken)

        // Assert
        if (gotGetResponse.statusCode.isError) {
            println("Get data error")
            return 0
        }

        val gotGetValues = (gotGetResponse.body as ResponseMeasurementsDTO).measurementsList.first().values
        if (gotGetValues.last().value != measurementsToAdd.measurements.last().values.last().value) {
            println("Last value doesn't match added")
            return 0
        }

        return 1
    }
}