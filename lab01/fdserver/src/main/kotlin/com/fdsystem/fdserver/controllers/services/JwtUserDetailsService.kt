package com.fdsystem.fdserver.controllers.services

import com.fdsystem.fdserver.config.NetworkConfig
import com.fdsystem.fdserver.data.UserRepositoryImpl
import com.fdsystem.fdserver.domain.dtos.UserCredentialsDTO
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service


@Service
class JwtUserDetailsService : UserDetailsService
{
    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(username: String): (UserDetails)
    {
        org.apache.commons.logging.LogFactory.getLog(javaClass)
            .warn("We are in custom Detail Service")
        val repo = UserRepositoryImpl(
            NetworkConfig.postgresUsername,
            NetworkConfig.postgresPassword
        )
        val user: UserCredentialsDTO
        if (repo.userExists(username))
        {
            user = repo.getUserByUsername(username)
        }
        else
        {
            throw UsernameNotFoundException("User not found with username $username")
        }

        return User(user.username, user.password, arrayListOf())
    }
}