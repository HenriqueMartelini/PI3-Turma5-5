package com.puc.superid.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.puc.superid.ui.theme.SuperidTheme
import com.puc.superid.utils.FirebaseUtils


/**
 * Representa uma categoria com seu ID e nome.
 *
 * @property id Identificador único da categoria.
 * @property nome Nome exibido da categoria.
 */


data class CategoriaItem(
    val id: String,
    val nome: String
)

/**
 * Activity responsável pelo gerenciamento das categorias do usuário.
 *
 * Exibe uma lista das categorias existentes, permitindo seleção e exclusão,
 * respeitando regras de negócio (não excluir categoria padrão "WebSite" e impedir exclusão
 * se existirem logins associados).
 */

class CategoryManagementActivity : ComponentActivity() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SuperidTheme {
                CategoryManagementScreen()
            }
        }
    }


    /**
     * Tela composable que exibe a lista de categorias do usuário.
     *
     * Inclui lógica para seleção de categoria e exibição de diálogo para confirmação
     * da exclusão com as devidas validações.
     */


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun CategoryManagementScreen() {
        val categorias = remember { mutableStateListOf<CategoriaItem>() }
        var selectedItem by remember { mutableStateOf<CategoriaItem?>(null) }
        var showDialog by remember { mutableStateOf(false) }
        val context = LocalContext.current
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        LaunchedEffect(Unit) {
            FirebaseUtils.fetchUserCategories(userId) { categoryNames ->
                categorias.clear()
                categoryNames.forEach { categoryName ->
                    categorias.add(CategoriaItem(id = categoryName, nome = categoryName))
                }
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
                        title = { Text("Gerenciar Categorias") },
                        navigationIcon = {
                            IconButton(
                                onClick = { (context as? CategoryManagementActivity)?.finish() }
                            ) {
                                Icon(
                                    Icons.Default.ArrowBack,
                                    contentDescription = "Voltar",
                                    tint = Color.White
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color(0xFF0D0B2D),
                            titleContentColor = Color.White
                        )
                    )
                }
            ) { paddingValues ->
                LazyColumn(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    items(categorias) { categoria ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedItem = categoria
                                    showDialog = true
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFD3D3D3))
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = categoria.nome,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Color.Black
                                )
                            }
                            HorizontalDivider(color = Color.LightGray)
                        }
                    }
                }

                if (showDialog && selectedItem != null) {
                    AlertDialog(
                        onDismissRequest = { showDialog = false },
                        title = { Text("Excluir categoria") },
                        text = { Text("Deseja excluir a categoria '${selectedItem?.nome}'?") },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    selectedItem?.let { categoria ->
                                        if (categoria.nome == "WebSite") {
                                            Toast.makeText(
                                                context,
                                                "A categoria 'WebSite' não pode ser excluída.",
                                                Toast.LENGTH_LONG
                                            ).show()
                                            showDialog = false
                                            return@TextButton
                                        }

                                        FirebaseFirestore.getInstance()
                                            .collection("users")
                                            .document(userId)
                                            .collection("categories")
                                            .document(categoria.nome)
                                            .collection("logins")
                                            .limit(1)
                                            .get()
                                            .addOnSuccessListener { result ->
                                                if (!result.isEmpty) {
                                                    Toast.makeText(
                                                        context,
                                                        "Há logins associados a esta categoria. Exclua-os primeiro.",
                                                        Toast.LENGTH_LONG
                                                    ).show()
                                                } else {
                                                    FirebaseFirestore.getInstance()
                                                        .collection("users")
                                                        .document(userId)
                                                        .collection("categories")
                                                        .document(categoria.nome)
                                                        .delete()
                                                        .addOnSuccessListener {
                                                            categorias.remove(categoria)
                                                            Toast.makeText(
                                                                context,
                                                                "Categoria excluída com sucesso.",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                        }
                                                        .addOnFailureListener {
                                                            Toast.makeText(
                                                                context,
                                                                "Erro ao excluir categoria.",
                                                                Toast.LENGTH_LONG
                                                            ).show()
                                                        }
                                                }
                                                showDialog = false
                                            }
                                    }
                                }
                            ) {
                                Text("Excluir", color = Color.Red)
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDialog = false }) {
                                Text("Cancelar")
                            }
                        }
                    )
                }
            }
        }
    }
}