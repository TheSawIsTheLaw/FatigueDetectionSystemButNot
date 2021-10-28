package com.fdsystem.fdserver.data

import com.fdsystem.fdserver.config.NetworkConfig
import com.fdsystem.fdserver.domain.dtos.PasswordChangeDTO
import com.fdsystem.fdserver.domain.dtos.UserCredentialsDTO
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


    override fun registerUser(
        username: String,
        password: String
    ): UserCredentialsDTO
    {
        if (userExists(username))
        {
            return UserCredentialsDTO(username, password, "")
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
                it[dbToken] = newToken
            }
        }

        return UserCredentialsDTO(username, password, newToken)
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

    override fun getUserByUsername(username: String): UserCredentialsDTO
    {
        if (!userExists(username))
        {
            return UserCredentialsDTO("", "", "")
        }

        var select: List<UsersTable.UserDTO> = listOf()
        transaction(connection.getConnectionToDB())
        {
            select = UsersTable.select { UsersTable.username.eq(username) }
                .map { mapToUserDTO(it) }
        }

        return UserCredentialsDTO(
            select[0].username, select[0].password,
            select[0].dbToken
        )
    }

    override fun changePasswordAndUsername(
        userInfo: PasswordChangeDTO
    ): Boolean
    {
        if (!userExists(userInfo.oldUsername))
        {
            return false
        }

        transaction(connection.getConnectionToDB())
        {
            UsersTable.update({
                UsersTable.username.eq(userInfo.oldUsername) and
                        UsersTable.password.eq(userInfo.oldPassword)
            })
            {
                it[username] = userInfo.newUsername
                it[password] = userInfo.newPassword
            }
        }

        return true
    }

    override fun getUserToken(
        username: String,
        password: String
    ): UserCredentialsDTO
    {
        if (!userExists(username))
        {
            return UserCredentialsDTO(username, password, "")
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
            return UserCredentialsDTO(username, password, "")
        }

        return UserCredentialsDTO(username, password, select[0].dbToken)
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

