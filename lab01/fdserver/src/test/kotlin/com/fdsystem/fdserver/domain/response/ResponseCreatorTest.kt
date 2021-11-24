package com.fdsystem.fdserver.domain.response

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.http.ResponseEntity

internal class ResponseCreatorTest {
    // As ResponseCreator is an object - there is no need to initialize it

    @Test
    fun internalServerErrorResponseTestSuccess() {
        // Arrange
        val message = "internalErrorMessage"
        val description = "internalErrorDescription"


        // Act
        val preparedResponse =
            ResponseCreator.internalServerErrorResponse(message, description)

        // Assert
        assert(
            preparedResponse.body!!.message == message &&
                    preparedResponse.body!!.description == description &&
                    preparedResponse.statusCode.is5xxServerError
        )
    }

    @Test
    fun okResponseTestSuccess() {
        // Arrange
        val message = "testMessage"
        val description = "testDescr"

        // Act
        val preparedResponse = ResponseCreator.okResponse(message, description)

        // Assert
        assert(
            preparedResponse.body!!.message == message &&
                    preparedResponse.body!!.description == description &&
                    preparedResponse.statusCode.is2xxSuccessful
        )
    }

    @Test
    fun userNotFoundResponseTestSuccess() {
        // Arrange
        val message = "notFoundMessage"
        val description = "notFoundDescription"

        // Act
        val preparedResponse = ResponseCreator.userNotFoundResponse(
            message,
            description
        )

        // Assert
        assertEquals(message, preparedResponse.body!!.message)
        assertEquals(description, preparedResponse.body!!.description)
        assertTrue(preparedResponse.statusCode.is4xxClientError)
    }
}