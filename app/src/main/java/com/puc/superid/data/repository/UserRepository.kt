package com.puc.superid.data.repository

import com.puc.superid.data.datasource.UserDataSource
import com.puc.superid.data.model.User

class UserRepository(private val userDataSource: UserDataSource) {
    suspend fun createUser(user: User) {
        userDataSource.createUser(user)
    }
}