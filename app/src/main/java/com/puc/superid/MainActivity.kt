package com.puc.superid

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.puc.superid.ui.OnboardingActivity
import com.puc.superid.ui.login.LoginScreen
import com.puc.superid.ui.theme.SuperidTheme

class MainActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE)

        val currentUser = auth.currentUser
        val isFirstTime = sharedPreferences.getBoolean("isFirstTime", true)

        if (currentUser == null) {
            startActivity(Intent(this, LoginScreen::class.java))
            finish()
        } else {
            val isFirstTime = true
            if (isFirstTime) {
                sharedPreferences.edit().putBoolean("isFirstTime", false).apply()
                startActivity(Intent(this, OnboardingActivity::class.java))
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val logins = remember { mutableStateListOf<String>() }

    LaunchedEffect(Unit) {
        fetchLoginPartners { fetchedLogins ->
            logins.clear()
            logins.addAll(fetchedLogins)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    listOf(Color(0xFF0D0B2D), Color(0xFF0033FF))
                )
            )
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF0D0B2D),
                        titleContentColor = Color.White
                    ),
                    title = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text("SuperID")
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { /* Drawer futuro */ }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    actions = {
                        IconButton(onClick = { /* Buscar */ }) {
                            Icon(Icons.Default.Search, contentDescription = "Buscar")
                        }
                        IconButton(onClick = { /* Adicionar */ }) {
                            Icon(Icons.Default.Add, contentDescription = "Adicionar")
                        }
                    }
                )
            },
            content = { paddingValues ->
                LazyColumn(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    items(logins) { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFD3D3D3))
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = null,
                                modifier = Modifier.size(40.dp),
                                tint = Color.DarkGray
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = item,
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.Black
                            )
                        }
                    }
                }
            }
        )
    }
}