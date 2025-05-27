package com.puc.superid

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.puc.superid.ui.OnboardingActivity
import com.puc.superid.ui.login.LoginScreen
import com.puc.superid.ui.login.QRCodeScannerActivity
import com.puc.superid.ui.passwordmanagement.EditPasswordActivity
import com.puc.superid.ui.registration.RegisterLoginActivity
import com.puc.superid.ui.theme.SuperidTheme
import com.puc.superid.utils.FirebaseUtils
import com.puc.superid.viewmodel.EditPasswordViewModel
import com.puc.superid.ui.CategoryManagementActivity

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
            //val isFirstTime = true
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val logins = remember { mutableStateListOf<FirebaseUtils.LoginItem>() }
    val context = LocalContext.current
    var selectedItem by remember { mutableStateOf<FirebaseUtils.LoginItem?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    val viewModel = remember { EditPasswordViewModel() }
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val expandedCategories = remember { mutableStateMapOf<String, Boolean>() }

    DisposableEffect(Unit) {
        val listener = FirebaseUtils.listenToUserLogins(userId) { fetchedLogins ->
            logins.clear()
            logins.addAll(fetchedLogins)

            fetchedLogins.distinctBy { it.category }.forEach {
                if (!expandedCategories.containsKey(it.category)) {
                    expandedCategories[it.category] = false
                }
            }
        }

        onDispose {
            listener.remove()
        }
    }

    val loginsByCategory = logins.groupBy { it.category }

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
                            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            context.startActivity(Intent(context, CategoryManagementActivity::class.java))
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.category_icon),
                                contentDescription = "Gerenciar Categorias",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        IconButton(onClick = { /* Buscar */ }) {
                            Icon(Icons.Default.Search, contentDescription = "Buscar", tint = Color.White)
                        }
                        IconButton(onClick = {
                            context.startActivity(Intent(context, QRCodeScannerActivity::class.java))
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.qrcode_icon),
                                contentDescription = "Escanear QR Code",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        IconButton(onClick = {
                            context.startActivity(Intent(context, RegisterLoginActivity::class.java))
                        }) {
                            Icon(Icons.Default.Add, contentDescription = "Adicionar", tint = Color.White)
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
                    loginsByCategory.keys.sorted().forEach { category ->
                        val categoryLogins = loginsByCategory[category] ?: emptyList()

                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        expandedCategories[category] = !(expandedCategories[category] ?: false)
                                    }
                                    .background(Color(0xFF1A1A2E))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        painter = painterResource(
                                            id = if (expandedCategories[category] == true)
                                                R.drawable.folder_open
                                            else
                                                R.drawable.folder_closed
                                        ),
                                        contentDescription = null,
                                        modifier = Modifier.size(24.dp),
                                        tint = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Text(
                                        text = category,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = Color.White,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = "${categoryLogins.size} itens",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.LightGray
                                    )
                                }
                            }
                        }

                        if (expandedCategories[category] == true) {
                            items(categoryLogins.sortedBy { it.site }) { item ->
                                Column(modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedItem = item
                                        showDialog = true
                                    }
                                ) {
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
                                        Column {
                                            Text(
                                                text = item.site,
                                                style = MaterialTheme.typography.bodyLarge,
                                                color = Color.Black
                                            )
                                            Text(
                                                text = item.email,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = Color.Gray
                                            )
                                        }
                                    }
                                    HorizontalDivider(
                                        thickness = 1.dp,
                                        color = Color.LightGray
                                    )
                                }
                            }
                        }
                    }
                }

                if (showDialog && selectedItem != null) {
                    AlertDialog(
                        onDismissRequest = { showDialog = false },
                        title = {
                            Text(text = selectedItem?.site ?: "", style = MaterialTheme.typography.titleMedium)
                        },
                        text = {
                            Text("O que deseja fazer com essa senha?")
                        },
                        confirmButton = {
                            TextButton(onClick = {
                                showDialog = false
                                val intent = Intent(context, EditPasswordActivity::class.java).apply {
                                    putExtra(EditPasswordActivity.EXTRA_USER_ID, userId)
                                    putExtra(EditPasswordActivity.EXTRA_CATEGORY, selectedItem?.category ?: "")
                                    putExtra(EditPasswordActivity.EXTRA_SITE, selectedItem?.site ?: "")
                                }
                                context.startActivity(intent)
                            }) {
                                Text("Editar senha")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = {
                                selectedItem?.let { item ->
                                    viewModel.deletePassword(
                                        userId = userId,
                                        category = item.category,
                                        site = item.site,
                                        onSuccess = {
                                            Toast.makeText(context, "Login excluído com sucesso", Toast.LENGTH_SHORT).show()
                                            showDialog = false
                                        },
                                        onError = { error ->
                                            Toast.makeText(context, "Erro ao excluir: $error", Toast.LENGTH_LONG).show()
                                            Log.e("MainActivity", "Erro ao excluir: $error")
                                        }
                                    )
                                }
                            }) {
                                Text("Excluir senha", color = Color.Red)
                            }
                        }
                    )
                }
            }
        )
    }
}