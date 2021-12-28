package factories

import com.fdsystem.fdserver.domain.dtos.UserCredentialsDTO

internal object UserCredentialsDTOFactory {
    fun getExistingUser() = UserCredentialsDTO(
        "testUser",
        "password",
        "HsJBf0sINtvxedXJio2Lg7iskJgLcR5q8a0MZtqoiWZt66pBEQ0LUz0IPEe5ooD2GqaxQoGxzqoIi-U1CLINow=="
    )
}