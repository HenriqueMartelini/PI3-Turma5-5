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

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()

        val currentUser = auth.currentUser

        auth.signOut()

        if (currentUser == null) {
            startActivity(Intent(this, SignUpActivity::class.java))
            finish()
        } else {
            setContent {
                SuperidTheme {
                    MainScreen()
                }
            }
        }
    }
}