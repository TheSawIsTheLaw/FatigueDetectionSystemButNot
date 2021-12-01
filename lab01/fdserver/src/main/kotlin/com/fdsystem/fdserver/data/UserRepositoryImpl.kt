package com.fdsystem.fdserver.data

import com.fdsystem.fdserver.config.PostgresConfiguration
import com.fdsystem.fdserver.domain.logicentities.USCredentialsChangeInfo
import com.fdsystem.fdserver.domain.logicentities.USUserCredentials
import com.fdsystem.fdserver.domain.userrepository.UserRepositoryInterface
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Repository

class PostgresConnection(
    private val username: String,
    private val password: String,
    private val url: String
) {
    fun getConnectionToDB(): Database {
        val urlForDB = "jdbc:postgresql://$url"
        return Database.connect(
            urlForDB,
            user = username,
            password = password,
            driver = "org.postgresql.Driver"
        )
    }
}

@Repository
class UserRepositoryImpl(
    private val config: PostgresConfiguration,
) : UserRepositoryInterface {
    private val userDAO = PostgresUserDAO(
        PostgresConnection(
            config.configData.postgresUsername,
            config.configData.postgresPassword,
            config.configData.postgresURL
        )
    )

    override fun userExists(username: String): Boolean {
        return userDAO.getUserByUsername(username).second.isNotBlank()
    }


    override fun registerUser(
        user: USUserCredentials
    ): Boolean {
        val username = user.username

        if (userExists(username)) {
            return false
        }

        val password = user.password
        val token = user.dbToken

        userDAO.registerUser(username, password, token)

        return true
    }

    override fun getUserByUsername(user: USUserCredentials): USUserCredentials {
        val username = user.username
        val gotUser = userDAO.getUserByUsername(username)

        if (gotUser.second == "") {
            return USUserCredentials("", "", "")
        }

        return USUserCredentials(username, gotUser.second, gotUser.third)
    }

    override fun checkPassword(user: USUserCredentials): Boolean {
        val gotUser = getUserByUsername(user)

        return gotUser.password == user.password
    }

    override fun changePasswordAndUsername(
        credentialChangeInfo: USCredentialsChangeInfo
    ): Boolean {
        val username = credentialChangeInfo.oldUsername
        val currentPassword = credentialChangeInfo.oldPassword

        if (currentPassword.isBlank() || userDAO.getUserByUsername(username).second != currentPassword) {
            return false
        }

        userDAO.changeCredentials(
            username,
            credentialChangeInfo.newUsername,
            currentPassword,
            credentialChangeInfo.newPassword
        )

        return true
    }

    override fun getUserToken(
        user: USUserCredentials
    ): USUserCredentials {
        val gotUser = getUserByUsername(user)

        val gotPassword = gotUser.password
        return if (user.password == gotPassword)
            USUserCredentials(user.username, gotPassword, gotUser.dbToken)
        else
            USUserCredentials("", "", "")
    }
}
