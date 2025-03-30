package com.puc.superid.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.puc.superid.utils.FirebaseUtils
import kotlinx.coroutines.launch

/**
 * ViewModel responsável pela lógica de criação de conta de usuário
 *
 * O SignUpViewModel contém a lógica para interagir com o Firebase e registrar um usuário
 * no banco de dados Firestore. Faz os registros de forma assíncrona com os coroutines
 */
class SignUpViewModel : ViewModel() {

    /**
     * Cria uma nova conta de usuário no Firebase e Firestore
     *
     * Este método chama a função registerUserInFirestore do utilitário FirebaseUtils para
     * registrar o usuário. Caso a operação seja bem-sucedida, o callback onSuccess é chamado.
     * Caso contrário, o callback onFailure é chamado com uma mensagem de erro
     *
     * @param name O nome do usuário
     * @param email O e-mail do usuário
     * @param password A senha do usuário
     * @param imei O IMEI do dispositivo do usuário
     * @param context O contexto da aplicação, necessário para o uso do Firebase
     * @param onSuccess Callback a ser chamado em caso de sucesso, sem parâmetros
     * @param onFailure Callback a ser chamado em caso de falha, com a mensagem de erro como parâmetro
     */
    fun createAccount(
        name: String,
        email: String,
        password: String,
        imei: String,
        context: Context,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                // Chama a função para registrar o usuário no Firestore
                FirebaseUtils.registerUserInFirestore(name, email, password, imei, context)
                // Se o registro for bem-sucedido, chama o callback de sucesso
                onSuccess()
            } catch (e: Exception) {
                // Em caso de erro, chama o callback de falha com a mensagem do erro
                onFailure(e.message ?: "Erro desconhecido")
            }
        }
    }
}