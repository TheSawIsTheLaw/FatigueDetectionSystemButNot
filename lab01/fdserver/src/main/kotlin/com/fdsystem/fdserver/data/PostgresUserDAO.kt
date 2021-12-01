package com.fdsystem.fdserver.data

import com.fdsystem.fdserver.domain.logicentities.USUserCredentials
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class PostgresUserDAO(private val connection: PostgresConnection) : UserDAOInterface {
    override fun registerUser(username: String, password: String, token: String) {
        transaction(connection.getConnectionToDB())
        {
            UsersTable.insert {
                it[UsersTable.username] = username
                it[UsersTable.password] = password
                it[dbToken] = dbToken
            }
        }
    }

    override fun getUserByUsername(username: String): Triple<String, String, String> {
        var user: Triple<String, String, String> = Triple("", "", "")
        transaction(connection.getConnectionToDB())
        {
            user = UsersTable.select { UsersTable.username.eq(username) }
                .map { Triple(it[UsersTable.username], it[UsersTable.password], it[UsersTable.dbToken]) }.first()
        }

        return user
    }

    override fun changeCredentials(
        oldUsername: String,
        newUsername: String,
        oldPassword: String,
        newPassword: String
    ) {
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
    }
}