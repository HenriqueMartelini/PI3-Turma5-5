package com.puc.superid.data.datasource

import com.puc.superid.data.model.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Classe responsável por gerenciar operações de banco de dados relacionadas a usuários no Firestore
 *
 * @property db Instância do FirebaseFirestore usada para realizar operações no banco de dado
 */
class UserDataSource(private val db: FirebaseFirestore) {

    /**
     * Salva um novo usuário no Firestore.
     *
     * @param user Objeto do tipo [User] que será salvo no banco de dados
     * @throws Exception Caso ocorra algum erro na operação de inserção
     */
    suspend fun createUser(user: User) {
        db.collection("users")
            .add(user)
            .await()
    }
}