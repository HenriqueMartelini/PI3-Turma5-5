package com.puc.superid

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.google.firebase.auth.FirebaseAuth
import com.puc.superid.ui.MainScreen
import com.puc.superid.ui.registration.SignUpActivity
import com.puc.superid.ui.theme.SuperidTheme

class MainActivity : ComponentActivity() {

    // Instância do FirebaseAuth utilizada para verificar o status de autenticação
    private lateinit var auth: FirebaseAuth

    /**
     * Método chamado quando a Activity é criada. Verifica se o usuário está autenticado no Firebase e
     * navega para a tela de registro ou para a tela principal do aplicativo
     *
     * @param savedInstanceState Estado salvo da Activity, utilizado para restaurar a UI em caso de reinicialização
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializa o FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // Verifica o usuário atual
        val currentUser = auth.currentUser

        auth.signOut()

        // Se o usuário não estiver autenticado, redireciona para a tela de cadastro
        if (currentUser == null) {
            startActivity(Intent(this, SignUpActivity::class.java))
            finish()
        } else {
            // Caso o usuário já esteja autenticado, direciona para a tela principal
            setContent {
                SuperidTheme {
                    MainScreen()
                }
            }
        }
    }
}