package com.fdsystem.fdserver.factories

import com.fdsystem.fdserver.domain.logicentities.USCredentialsChangeInfo
import com.fdsystem.fdserver.domain.logicentities.USUserCredentials

class USUserCredentialsFactory {
    fun createAnyWithoutToken() = USUserCredentials("newUser", "password", "")

    fun createEmptyUser() = USUserCredentials("", "", "")

    fun createNonExistingUser() = USUserCredentials("nonExistingUser", "lol", "")

    fun createNewUserForPasswordChange() = USUserCredentials("newUserForChange", "pas1", "ololo")

    private val userForChange = createNewUserForPasswordChange()
    fun getChangedUser() = USUserCredentials("newNewUserForCheck", "pas2", userForChange.dbToken)
    private val changedUser = getChangedUser()

    fun getCredentialsForChange() =
        USCredentialsChangeInfo(
            userForChange.username,
            changedUser.username,
            userForChange.password,
            changedUser.password
        )

    fun getExistingUser() = USUserCredentials(
        "user",
        "password",
        "HsJBf0sINtvxedXJio2Lg7iskJgLcR5q8a0MZtqoiWZt66pBEQ0LUz0IPEe5ooD2GqaxQoGxzqoIi-U1CLINow=="
    )

    private val existingUser = getExistingUser()

    fun getCredentialsForFailureChangeExistingUser() = USCredentialsChangeInfo(
        existingUser.username,
        existingUser.username,
        existingUser.password + "wololo",
        existingUser.password + "newWololo"
    )

    fun getExistingUserWithInvalidPassword() = USUserCredentials(
        "user",
        "invalidPassword",
        "HsJBf0sINtvxedXJio2Lg7iskJgLcR5q8a0MZtqoiWZt66pBEQ0LUz0IPEe5ooD2GqaxQoGxzqoIi-U1CLINow=="
    )

    fun createNewUser() = USUserCredentials(
        "newUser",
        "password",
        "HsJBf0sINtvxedXJio2Lg7iskJgLcR5q8a0MZtqoiWZt66pBEQ0LUz0IPEe5ooD2GqaxQoGxzqoIi-U1CLINow=="
    )
}