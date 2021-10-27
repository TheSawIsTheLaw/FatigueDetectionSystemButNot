package com.fdsystem.fdserver.data

import com.fdsystem.fdserver.config.NetworkConfig
import com.fdsystem.fdserver.domain.UserCredentials
import com.fdsystem.fdserver.domain.userrepository.UserRepositoryInterface
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

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

class UserRepositoryImpl(
    postgresUsername_: String,
    postgresPassword_: String
) : UserRepositoryInterface
{
    private val connection = PostgresConnection(
        postgresUsername_,
        postgresPassword_,
        NetworkConfig.postgresURL
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


    override fun registerUser(username: String, password: String): String
    {
        if (userExists(username))
        {
            return ""
        }

        val newToken = CharRepositoryImpl(
            NetworkConfig.influxdbURL,
            NetworkConfig.influxAdminToken,
            NetworkConfig.influxOrganization
        ).getNewTokenForUser(username)

        transaction(connection.getConnectionToDB())
        {
            UsersTable.insert {
                it[UsersTable.username] = username
                it[UsersTable.password] = password
                it[UsersTable.dbToken] = newToken
            }
        }

        return newToken
    }

    override fun checkPassword(username: String, password: String): Boolean
    {
        if (!userExists(username))
        {
            return false
        }

        var select: List<UsersTable.UserDTO> = listOf()
        transaction(connection.getConnectionToDB())
        {
            select = UsersTable
                .select {
                    UsersTable.username.eq(username) and UsersTable.password.eq(
                        password
                    )
                }
                .map { mapToUserDTO(it) }
        }

        return select.isNotEmpty()
    }

    override fun getUserByUsername(username: String): UserCredentials
    {
        if (!userExists(username))
        {
            return UserCredentials("", "", "")
        }

        var select: List<UsersTable.UserDTO> = listOf()
        transaction(connection.getConnectionToDB())
        {
            select = UsersTable.select { UsersTable.username.eq(username) }
                .map { mapToUserDTO(it) }
        }

        return UserCredentials(select[0].username, select[0].password,
            select[0].dbToken)
    }

    override fun changePasswordAndUsername(
        oldUsername: String,
        newUsername: String,
        oldPassword: String,
        newPassword: String
    ): Boolean
    {
        if (!userExists(oldUsername))
        {
            return false
        }

        transaction(connection.getConnectionToDB())
        {
            UsersTable.update({
                UsersTable.username.eq(oldUsername) and
                        UsersTable.password.eq(oldPassword)
            })
            {
                it[username] = newUsername
                it[password] = newPassword
            }
        }

        return true
    }

    override fun getUserToken(username: String, password: String): String
    {
        if (!userExists(username))
        {
            return "User doesn't exist"
        }

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
            return "Wrong password"
        }

        return select[0].dbToken
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

