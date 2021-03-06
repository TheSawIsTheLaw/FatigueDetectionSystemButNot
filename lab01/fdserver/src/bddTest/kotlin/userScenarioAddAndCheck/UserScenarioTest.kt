package userScenarioAddAndCheck

import com.fdsystem.fdserver.controllers.jwt.JwtResponse
import com.fdsystem.fdserver.domain.dtos.AcceptMeasurementsListDTO
import com.fdsystem.fdserver.domain.dtos.ResponseMeasurementsDTO
import com.fdsystem.fdserver.domain.dtos.UserCredentialsDTO
import factories.DataControllerFactory
import factories.MeasurementsListFactory
import factories.UserControllerFactory
import factories.UserCredentialsDTOFactory
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.junit.jupiter.api.Assertions.*
import org.springframework.http.ResponseEntity

internal class UserScenarioTest : Spek({
    val userController = UserControllerFactory.getUserController()
    val dataController = DataControllerFactory.getDataController()

    /*
     *  Прикол передачи датасета в тест в том, что фича не предусмотрена.
     *  Поэтому форыч через мапу.
     *  https://www.baeldung.com/kotlin/spek
     */

    val dataset = listOf(
        mapOf(
            "userCredentials" to UserCredentialsDTOFactory.getExistingUser(),
            "measurementsToAdd" to MeasurementsListFactory.getMeasurementsToAdd("botArterialPressure", 666),
            "measurementsToGet" to MeasurementsListFactory.getMeasurementsList("botArterialPressure")
        ),
        mapOf(
            "userCredentials" to UserCredentialsDTOFactory.getExistingUser(),
            "measurementsToAdd" to MeasurementsListFactory.getMeasurementsToAdd("pulse", 33),
            "measurementsToGet" to MeasurementsListFactory.getMeasurementsList("pulse")
        )
    )

    // Ok. In Spek given is given, when is on, then is it. Nice.
    group("A test where user logs in, send some info to DB and checks if it was added") {
        dataset.forEach {
            lateinit var gotLoginResponse: ResponseEntity<*> // For the next test
            test("User login") {
                given("User credentials to login. User exists and all is good.") {
                    val userCredentials = it["userCredentials"] as UserCredentialsDTO

                    on("We're logging user in and get his JWT") {
                        gotLoginResponse = userController.login(userCredentials)

                        it("Login response returns no error") {
                            assertTrue(gotLoginResponse.statusCode.is2xxSuccessful)
                        }
                        it("Body should contain JWT") {
                            assertTrue((gotLoginResponse.body as JwtResponse).token.isNotBlank())
                        }
                    }
                }
            }

            lateinit var userJwtToken: String // For the next test
            lateinit var measurementsToAdd: AcceptMeasurementsListDTO
            test("Add measurement") {
                given("User's JWT and measurement which it wants to add") {
                    userJwtToken = "Bearer " + (gotLoginResponse.body as JwtResponse).token
                    measurementsToAdd = it["measurementsToAdd"] as AcceptMeasurementsListDTO

                    on("Send measurements to BD using user's JWT and get response about operation") {
                        val gotAddResponse = dataController.addData(measurementsToAdd, userJwtToken)

                        it("Response should contains 2xx code") {
                            assertTrue(gotAddResponse.statusCode.is2xxSuccessful)
                        }
                    }
                }
            }

            test("Get measurement") {
                given("List of required to see measurements (contains only one because only one was added") {
                    val measurementsToGet = it["measurementsToGet"] as List<String>

                    on("Get values of measurement by it's name") {
                        val gotGetResponse = dataController.getData(measurementsToGet, userJwtToken)

                        val gotGetValues =
                            (gotGetResponse.body as ResponseMeasurementsDTO).measurementsList.first().values
                        it("Response should contain success code") {
                            assertTrue(gotGetResponse.statusCode.is2xxSuccessful)
                        }
                        it("Last value of got list should match to added value") {
                            assertEquals(
                                gotGetValues.last().value,
                                measurementsToAdd.measurements.last().values.last().value
                            )
                        }
                    }
                }
            }
        }
    }

    println("${dataset.size} tests passed.")
})