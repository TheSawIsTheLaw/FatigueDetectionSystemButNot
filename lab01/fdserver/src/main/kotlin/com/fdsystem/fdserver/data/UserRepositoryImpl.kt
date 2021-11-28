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
)
{
    fun getConnectionToDB(): Database
    {
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
    private val config: PostgresConfiguration
) : UserRepositoryInterface
{
    private val connection = PostgresConnection(
        config.configData.postgresUsername,
        config.configData.postgresPassword,
        config.configData.postgresURL
    )

    private fun mapToUserDTO(it: ResultRow) =
        UsersTable.UserDTO(
            it[UsersTable.id],
            it[UsersTable.username],
            it[UsersTable.password],
            it[UsersTable.dbToken]
        )

    override fun userExists(username: String): Boolean
    {
        var select: List<UsersTable.UserDTO> = listOf()
        transaction(connection.getConnectionToDB())
        {
            select = UsersTable.select { UsersTable.username.eq(username) }
                .map { mapToUserDTO(it) }
        }

        return select.isNotEmpty()
    }


    override fun registerUser(
        user: USUserCredentials
    ): Boolean
    {
        if (userExists(user.username))
        {
            return false
        }

        transaction(connection.getConnectionToDB())
        {
            UsersTable.insert {
                it[username] = user.username
                it[password] = user.password
                it[dbToken] = user.dbToken
            }
        }

        return true
    }

    override fun checkPassword(user: USUserCredentials): Boolean
    {
        if (!userExists(user.username))
        {
            return false
        }

        var select: List<UsersTable.UserDTO> = listOf()
        transaction(connection.getConnectionToDB())
        {
            select = UsersTable
                .select {
                    UsersTable.username.eq(user.username) and UsersTable.password.eq(
                        user.password
                    )
                }
                .map { mapToUserDTO(it) }
        }

        return select.isNotEmpty()
    }

    override fun getUserByUsername(user: USUserCredentials): USUserCredentials
    {
        if (!userExists(user.username))
        {
            return USUserCredentials("", "", "")
        }

        var select: List<UsersTable.UserDTO> = listOf()
        transaction(connection.getConnectionToDB())
        {
            select = UsersTable.select { UsersTable.username.eq(user.username) }
                .map { mapToUserDTO(it) }
        }

        return USUserCredentials(
            select[0].username, select[0].password,
            select[0].dbToken
        )
    }

    override fun changePasswordAndUsername(
        credentialChangeInfo: USCredentialsChangeInfo
    ): Boolean
    {
        if (!userExists(credentialChangeInfo.oldUsername))
        {
            return false
        }

        transaction(connection.getConnectionToDB())
        {
            UsersTable.update({
                UsersTable.username.eq(credentialChangeInfo.oldUsername) and
                        UsersTable.password.eq(credentialChangeInfo.oldPassword)
            })
            {
                it[username] = credentialChangeInfo.newUsername
                it[password] = credentialChangeInfo.newPassword
            }
        }

        return true
    }

    override fun getUserToken(
        user: USUserCredentials
    ): USUserCredentials
    {
        if (!userExists(user.username))
        {
            return USUserCredentials(user.username, user.password, "")
        }

        val username = user.username
        val password = user.password

        var select: List<UsersTable.UserDTO> = listOf()
        transaction(connection.getConnectionToDB())
        {
            select = UsersTable.select {
                UsersTable.username.eq(username) and UsersTable.password.eq(
                    password
                )
            }
                .map { mapToUserDTO(it) }
        }

        if (select.isEmpty())
        {
            return USUserCredentials(username, password, "")
        }

        return USUserCredentials(username, password, select[0].dbToken)
    }
}

//fun main()
//{
//    val connection = PostgresConnection(NetworkConfig.postgresUsername, NetworkConfig.postgresPassword, "localhost:5432/users")
//
//    val newToken = CharRepositoryImpl(
//        NetworkConfig.influxdbURL,
//        NetworkConfig.influxAdminToken,
//        NetworkConfig.influxOrganization
//    ).getNewTokenForUser("Yakuba Dmitry")
//
//    transaction(connection.getConnectionToDB())
//    {
//        UsersTable.insert {
//            it[username] = "username"
//            it[password] = "password"
//            it[dbToken] = newToken
//        }
//    }
//}

