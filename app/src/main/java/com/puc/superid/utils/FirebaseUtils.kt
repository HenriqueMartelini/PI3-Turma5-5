package com.puc.superid.utils

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.puc.superid.data.datasource.UserDataSource
import com.puc.superid.data.model.User
import com.puc.superid.data.repository.UserRepository
import kotlinx.coroutines.tasks.await

/**
 * Classe que contém métodos do Firebase.
 */
object FirebaseUtils {

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
            // Cria o usuário no Firebase Authentication com o email e senha inseridos
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid.orEmpty()

            // Criptografa a senha antes de armazenar no Firestore
            val hashedPassword = StringUtils.hashPassword(password)

            // Cria um objeto User com as informações do usuário
            val user = User(
                uid = uid,
                name = name,
                email = email,
                imei = imei,
                password = hashedPassword
            )

            // Cria a instância de UserDataSource e UserRepository para salvar os dados no Firestore
            val userDataSource = UserDataSource(firestore)
            val userRepository = UserRepository(userDataSource)

            // Salva o usuário no Firestore
            userRepository.createUser(user)

            Log.d("FirebaseUtils", "Usuário registrado com sucesso.")
        } catch (e: FirebaseAuthException) {
            // Loga o erro se houver falha na criação do usuário no Firebase Authentication
            Log.e("FirebaseUtils", "Erro ao criar usuário no Firebase Authentication: ${e.message}")
            throw e
        } catch (e: Exception) {
            // Loga erros inesperados e lança a exceção
            Log.e("FirebaseUtils", "Erro ao salvar o usuário: ${e.message}")
            throw e
        }
    }

    fun fetchLoginPartners(onResult: (List<String>) -> Unit) {
        val db = Firebase.firestore
        db.collection("loginPartner")
            .get()
            .addOnSuccessListener { documents ->
                val docNames = documents.map { it.id }
                onResult(docNames)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }

    fun fetchCategorias(onResult: (List<String>) -> Unit) {
        val db = Firebase.firestore
        db.collection("categorias")
            .get()
            .addOnSuccessListener { documents ->
                Log.d("FirebaseDebug", "Número de documentos encontrados: ${documents.size()}")
                if (documents.isEmpty) {
                    Log.d("FirebaseDebug", "Nenhum documento encontrado na coleção 'categorias'.")
                }
                val categorias = documents.map { it.id }
                onResult(categorias)
            }
            .addOnFailureListener { exception ->
                Log.e("FirebaseUtils", "Erro ao carregar categorias: ${exception.message}")
                onResult(emptyList())
            }
    }

    fun createCategoria(
        categoria: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val db = Firebase.firestore
        val data = mapOf("categoria" to categoria)

        db.collection("categorias")
            .document(categoria)
            .set(data)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onError(exception) }
    }

}