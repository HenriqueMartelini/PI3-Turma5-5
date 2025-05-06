package com.puc.superid.ui.login

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*

@Composable
fun MainPage() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1C2D69)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Bem-vindo!",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White
        )
    }
}