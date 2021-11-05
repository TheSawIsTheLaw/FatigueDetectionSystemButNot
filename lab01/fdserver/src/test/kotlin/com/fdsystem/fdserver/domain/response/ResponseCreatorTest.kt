package com.fdsystem.fdserver.domain.response

import org.junit.jupiter.api.Test
import org.springframework.http.ResponseEntity

internal class ResponseCreatorTest
{
    // As ResponseCreator is an object - there is no need to initialize it

    @Test
    fun prepareMessageTestSuccess()
    {
        // Arrange
        val message = "testMessage"
        val description = "testDescr"
        val code = 201

        // Set private method public
        val requiredPrivateMethod = ResponseCreator.javaClass.getDeclaredMethod(
            "prepareMessage",
            Int::class.java,
            String::class.java,
            String::class.java
        )
        requiredPrivateMethod.isAccessible = true

        // Prepare parameters
        val requiredParameters = arrayOfNulls<Any>(3)
        requiredParameters[0] = code
        requiredParameters[1] = message
        requiredParameters[2] = description

        // Act
        val preparedResponseMessage = requiredPrivateMethod.invoke(
            ResponseCreator,
            *requiredParameters
        ) as ResponseMessage

        // Assert
        assert(
            preparedResponseMessage.message == message &&
                    preparedResponseMessage.description == description &&
                    preparedResponseMessage.code == code
        )
    }

    @Test
    fun internalServerErrorResponseTestSuccess()
    {
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
    fun okResponseTestSuccess()
    {
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
    fun userNotFoundResponseTestSuccess()
    {
        // Arrange
        val message = "notFoundMessage"
        val description = "notFoundDescription"

        // Act
        val preparedResponse = ResponseCreator.userNotFoundResponse(
            message,
            description
        )

        // Assert
        assert(
            preparedResponse.body!!.message == message &&
                    preparedResponse.body!!.description == description &&
                    preparedResponse.statusCode.is4xxClientError
        )
    }
}