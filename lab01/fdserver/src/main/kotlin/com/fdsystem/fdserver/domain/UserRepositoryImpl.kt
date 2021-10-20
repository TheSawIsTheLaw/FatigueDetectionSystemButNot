package com.fdsystem.fdserver.domain

import org.jetbrains.exposed.sql.Database
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
    val connection = PostgresConnection(username_, password_, "localhost:5432/users")

    override fun userExists(username: String)
    {
        transaction(connection.getConnectionToDB())
        {
            
        }
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