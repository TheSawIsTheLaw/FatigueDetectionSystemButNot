package com.fdsystem.fdserver.controllers.services

import com.fdsystem.fdserver.data.UserRepositoryImpl
import com.fdsystem.fdserver.domain.logicentities.USUserCredentials
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service


@Service
class JwtUserDetailsService(private val userRepository: UserRepositoryImpl) :
    UserDetailsService {
    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(username: String): (UserDetails) {
        val user: USUserCredentials
        if (userRepository.userExists(username)) {
            user = userRepository.getUserByUsername(USUserCredentials(username, "", ""))
        } else {
            throw UsernameNotFoundException("User not found with username $username")
        }

        return User(user.username, user.password, arrayListOf())
    }
}