package com.fdsystem.fdserver.data

import com.fdsystem.fdserver.domain.userrepository.UserRepositoryInterface
import org.hibernate.sql.Select
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Query
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.select
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
    username_: String,
    password_: String
) : UserRepositoryInterface
{
    private val connection = PostgresConnection(username_, password_, "localhost:5432/users")

    private fun mapToUserDTO(it: ResultRow) =
        UserTable.UserDTO(it[UserTable.id], it[UserTable.username], it[UserTable.password], it[UserTable.dbToken])

    override fun userExists(username: String): Boolean
    {
        var select: List<UserTable.UserDTO> = listOf()
        transaction(connection.getConnectionToDB())
        {
            select = UserTable.select { UserTable.username.eq(username) }
                .map { mapToUserDTO(it) }
        }
        if (select.isEmpty())
        {
            return false
        }

        return true
    }


    override fun registerUser(username: String, password: String): Boolean
    {
        TODO("Not yet implemented")
    }

    override fun getUserToken(username: String, password: String): String
    {
        TODO("Not yet implemented")
    }
}

