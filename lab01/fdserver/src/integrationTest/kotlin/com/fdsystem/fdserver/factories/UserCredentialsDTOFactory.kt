package com.fdsystem.fdserver.factories

import com.fdsystem.fdserver.domain.dtos.UserCredentialsDTO

class UserCredentialsDTOFactory {
    fun getExistingUser() = UserCredentialsDTO(
        "testUser",
        "password",
        "HsJBf0sINtvxedXJio2Lg7iskJgLcR5q8a0MZtqoiWZt66pBEQ0LUz0IPEe5ooD2GqaxQoGxzqoIi-U1CLINow=="
    )

    fun getNewUserForPasswordChange() = UserCredentialsDTO(
        "passwordChangeUser",
        "password",
        "HsJBf0sINtvxedXJio2Lg7iskJgLcR5q8a0MZtqoiWZt66pBEQ0LUz0IPEe5ooD2GqaxQoGxzqoIi-U1CLINow=="
    )

    fun getNewUserWithPasswordChanged() = UserCredentialsDTO(
        "passwordChangeUser",
        "password",
        "HsJBf0sINtvxedXJio2Lg7iskJgLcR5q8a0MZtqoiWZt66pBEQ0LUz0IPEe5ooD2GqaxQoGxzqoIi-U1CLINow=="
    )

    fun getExistingUserWithNewPassword() = UserCredentialsDTO(
        "testUser",
        "passwordnew",
        "HsJBf0sINtvxedXJio2Lg7iskJgLcR5q8a0MZtqoiWZt66pBEQ0LUz0IPEe5ooD2GqaxQoGxzqoIi-U1CLINow=="
    )

    fun getNotExistingUser() = UserCredentialsDTO(
        "re-sealed all bags, won't change shit",
        "on my skin, dig deep like maggots",
        "All I am is just a fragment of what i have left to my name"
    )

    fun getNewUserForCreation() = UserCredentialsDTO(
        "istasha",
        "the scrub",
        "neural nightmares"
    )
}