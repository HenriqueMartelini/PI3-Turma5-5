package com.puc.superid.utils

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.puc.superid.data.model.User
import kotlinx.coroutines.tasks.await
import java.util.*

/**
 * Classe que contém métodos do Firebase.
 */
object FirebaseUtils {

    private fun generateApiKey(): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..24)
            .map { allowedChars.random() }
            .joinToString("")
    }

    /**
     * Registra um novo usuário no Firebase Authentication e salva seus dados no Firestore
     *
     * Método responsável pelas seguitnes funções:
     * 1. Criação do usuário no Firebase Authentication usando email e senha
     * 2. Criptografar a senha antes de salvar no Firestore
     * 3. Salva os dados do usuário no Firestore
     *
     * @param name Nome do usuário
     * @param email Email do usuário.
     * @param password Senha do usuário
     * @param imei IMEI do dispositivo do usuário
     * @param context Contexto da aplicação
     * @throws FirebaseAuthException Lançado se ocorrer um erro ao criar o usuário no Firebase Authentication
     * @throws Exception Lançado se ocorrer qualquer outro erro ao salvar o usuário
     */
    suspend fun registerUserInFirestore(
        name: String,
        email: String,
        password: String,
        imei: String,
        context: Context
    ) {
        val auth = FirebaseAuth.getInstance()
        val firestore = FirebaseFirestore.getInstance()

        try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid.orEmpty()

            val hashedPassword = StringUtils.hashPassword(password)

            val user = User(
                uid = uid,
                name = name,
                email = email,
                imei = imei,
                password = hashedPassword
            )

            firestore.collection("users").document(uid).set(user).await()

            createDefaultUserStructure(uid, firestore)

            Log.d("FirebaseUtils", "Usuário registrado com sucesso com subcoleções.")
        } catch (e: Exception) {
            Log.e("FirebaseUtils", "Erro ao registrar usuário: ${e.message}")
            throw e
        }
    }

    private fun createDefaultUserStructure(userId: String, firestore: FirebaseFirestore) {
        val defaultCategories = listOf("App", "WebSite", "Teclado físico")

        val userRef = firestore.collection("users").document(userId)

        val categoriesRef = userRef.collection("categories")

        defaultCategories.forEach { category ->
            categoriesRef.document(category).set(hashMapOf(
                "name" to category,
                "createdAt" to System.currentTimeMillis()
            )).addOnFailureListener { e ->
                Log.e("FirebaseUtils", "Erro ao criar categoria padrão: ${e.message}")
            }
        }

        Log.d("FirebaseUtils", "Estrutura padrão criada para o usuário $userId")
    }

    fun saveUserLogin(
        userId: String,
        site: String,
        email: String,
        password: String,
        category: String,
        context: Context,
        onComplete: (Boolean) -> Unit
    ) {
        val db = Firebase.firestore
        val apiKey = generateApiKey()

        val loginData = hashMapOf(
            "site" to site,
            "email" to email,
            "password" to password,
            "apiKey" to apiKey,
            "createdAt" to System.currentTimeMillis()
        )

        db.collection("users")
            .document(userId)
            .collection("categories")
            .document(category)
            .collection("logins")
            .document(site)
            .set(loginData)
            .addOnSuccessListener {
                Log.d("FirebaseUtils", "Login salvo com sucesso na categoria $category")
                Log.d("FirebaseUtils", "API Key gerada: $apiKey")
                onComplete(true)
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseUtils", "Erro ao salvar login: ${e.message}")
                Toast.makeText(context, "Erro ao salvar login", Toast.LENGTH_SHORT).show()
                onComplete(false)
            }
    }

    fun fetchUserCategories(
        userId: String,
        onResult: (List<String>) -> Unit
    ) {
        Firebase.firestore.collection("users")
            .document(userId)
            .collection("categories")
            .get()
            .addOnSuccessListener { documents ->
                val categories = documents.map { it.id }
                onResult(categories)
            }
            .addOnFailureListener { exception ->
                Log.e("FirebaseUtils", "Erro ao buscar categorias: ${exception.message}")
                onResult(emptyList())
            }
    }

    fun addUserCategory(
        userId: String,
        categoryName: String,
        onComplete: (Boolean) -> Unit
    ) {
        Firebase.firestore.collection("users")
            .document(userId)
            .collection("categories")
            .document(categoryName)
            .set(hashMapOf("name" to categoryName))
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    fun listenToUserLogins(
        userId: String,
        onResult: (List<LoginItem>) -> Unit
    ): ListenerRegistration {
        return Firebase.firestore.collection("users")
            .document(userId)
            .collection("categories")
            .addSnapshotListener { categoriesSnapshot, _ ->
                val logins = mutableListOf<LoginItem>()

                categoriesSnapshot?.documents?.forEach { categoryDoc ->
                    val category = categoryDoc.id
                    categoryDoc.reference.collection("logins")
                        .get()
                        .addOnSuccessListener { loginsSnapshot ->
                            loginsSnapshot.documents.forEach { loginDoc ->
                                logins.add(LoginItem(
                                    id = loginDoc.id,
                                    site = loginDoc.getString("site") ?: "",
                                    email = loginDoc.getString("email") ?: "",
                                    category = category,
                                    createdAt = loginDoc.getLong("createdAt") ?: 0,
                                    apiKey = loginDoc.getString("apiKey") ?: ""
                                ))
                            }
                            onResult(logins)
                        }
                        .addOnFailureListener { e ->
                            Log.e("FirebaseUtils", "Erro ao buscar logins: ${e.message}")
                        }
                }
            }
    }

    data class LoginItem(
        val id: String,
        val site: String,
        val email: String,
        val category: String,
        val createdAt: Long = 0,
        val apiKey: String = ""
    )
}