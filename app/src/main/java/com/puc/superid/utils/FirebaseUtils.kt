package com.puc.superid.utils

import LoginItem
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
 * Objeto utilitário com métodos para interagir com Firebase Authentication e Firestore,
 * facilitando operações como cadastro de usuário, manipulação de categorias e logins.
 */
object FirebaseUtils {

    /**
     * Gera uma chave API aleatória composta por letras maiúsculas, minúsculas e dígitos,
     * com tamanho fixo de 24 caracteres.
     *
     * @return String contendo a chave gerada.
     */

    private fun generateApiKey(): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..24)
            .map { allowedChars.random() }
            .joinToString("")
    }

    /**
     * Registra um novo usuário no Firebase Authentication com email e senha,
     * e salva os dados do usuário no Firestore, incluindo a senha criptografada.
     *
     * Também cria uma estrutura padrão de categorias para o usuário.
     *
     * @param name Nome completo do usuário.
     * @param email Email para cadastro.
     * @param password Senha para cadastro (será criptografada para salvar no Firestore).
     * @param imei Identificador IMEI do dispositivo do usuário.
     * @param context Contexto da aplicação para uso em possíveis mensagens.
     *
     * @throws FirebaseAuthException Caso falhe a criação no Firebase Authentication.
     * @throws Exception Caso ocorra erro ao salvar os dados no Firestore.
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


    /**
     * Cria categorias padrão ("App", "WebSite", "Teclado físico") na subcoleção "categories"
     * para um usuário recém-registrado no Firestore.
     *
     * @param userId ID do usuário no Firebase Authentication / Firestore.
     * @param firestore Instância do FirebaseFirestore.
     */

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


    /**
     * Salva um novo login (credenciais) para o usuário em uma categoria específica no Firestore.
     *
     * Gera uma API Key aleatória para cada login salvo.
     *
     * @param userId ID do usuário.
     * @param site Nome do site ou serviço do login.
     * @param email Email usado no login.
     * @param password Senha do login (sem criptografia).
     * @param category Categoria onde o login será salvo.
     * @param context Contexto para exibir mensagens Toast.
     * @param onComplete Callback chamado com true em caso de sucesso, false em falha.
     */

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

    /**
     * Busca as categorias cadastradas para um usuário no Firestore.
     *
     * @param userId ID do usuário.
     * @param onResult Callback que recebe uma lista de nomes de categorias (Strings).
     */

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

    /**
     * Adiciona uma nova categoria para o usuário no Firestore.
     *
     * @param userId ID do usuário.
     * @param categoryName Nome da categoria a ser adicionada.
     * @param onComplete Callback que indica sucesso (true) ou falha (false).
     */

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

    /**
     * Escuta em tempo real as alterações nas categorias e logins do usuário.
     * Retorna uma [ListenerRegistration] para controle do listener.
     *
     * @param userId ID do usuário.
     * @param onResult Callback que retorna uma lista atualizada de [LoginItem].
     * @return ListenerRegistration para remover o listener posteriormente.
     */

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
}