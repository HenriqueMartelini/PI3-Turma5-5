package com.puc.superid.data.datasource

import com.puc.superid.data.model.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserDataSource(private val db: FirebaseFirestore) {
    suspend fun createUser(user: User) {
        db.collection("users")
            .add(user)
            .await()
    }
}