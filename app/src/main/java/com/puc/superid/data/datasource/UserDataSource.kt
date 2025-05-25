package com.puc.superid.data.datasource

import com.puc.superid.data.model.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Classe responsável por gerenciar operações de banco de dados relacionadas a usuários no Firestore
 *
 * @property db Instância do FirebaseFirestore usada para realizar operações no banco de dado
 */
class UserDataSource(private val firestore: FirebaseFirestore) {

    /**
     * Salva um novo usuário no Firestore.
     *
     * @param user Objeto do tipo [User] que será salvo no banco de dados
     * @throws Exception Caso ocorra algum erro na operação de inserção
     */
    suspend fun createUser(userId: String, user: User) {
        firestore.collection("users")
            .document(userId) // Usa o UID como ID
            .set(user)
            .await()
    }

}