package com.puc.superid.utils

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestore
import com.puc.superid.data.datasource.UserDataSource
import com.puc.superid.data.model.User
import com.puc.superid.data.repository.UserRepository
import kotlinx.coroutines.tasks.await

object FirebaseUtils {

    // Função para registrar o usuário no Firebase Authentication e Firestore
    suspend fun registerUserInFirestore(name: String, email: String, password: String, imei: String, context: Context) {
        val auth = FirebaseAuth.getInstance()
        val firestore = FirebaseFirestore.getInstance()

        try {
            // Criar a conta no Firebase Authentication
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid.orEmpty()

            val hashedPassword = StringUtils.hashPassword(password)

            // Criar o objeto User
            val user = User(
                uid = uid,
                name = name,
                email = email,
                imei = imei,
                password = hashedPassword
            )

            // Criando o repositório
            val userDataSource = UserDataSource(firestore)
            val userRepository = UserRepository(userDataSource)

            // Usando o repositório para salvar o usuário
            userRepository.createUser(user)

            Log.d("FirebaseUtils", "Usuário registrado com sucesso.")
        } catch (e: FirebaseAuthException) {
            Log.e("FirebaseUtils", "Erro ao criar usuário no Firebase Authentication: ${e.message}")
            throw e  // Propaga o erro para o ViewModel
        } catch (e: Exception) {
            Log.e("FirebaseUtils", "Erro ao salvar o usuário: ${e.message}")
            throw e
        }
    }
}