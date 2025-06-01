package com.puc.superid.ui.registration

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.puc.superid.utils.FirebaseUtils


/**
 * Activity para cadastro de uma nova categoria para o usuário.
 *
 * Exibe uma tela com campo de texto para o nome da categoria e botão para salvar.
 * Ao salvar, chama FirebaseUtils para adicionar a categoria ao Firestore associada ao usuário atual.
 */

class NewCategoryActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            NewCategoryScreen()
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun NewCategoryScreen() {
        val context = LocalContext.current
        var categoria by remember { mutableStateOf("") }

        val gradient = Brush.horizontalGradient(listOf(Color(0xFF3E8EFF), Color(0xFF6C63FF)))

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Nova Categoria", color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = { finish() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = Color(0xFF0D0B2D))
                )
            },
            containerColor = Color(0xFF0D0B2D)
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 24.dp)
                    .background(Color(0xFF0D0B2D)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Cadastrar nova categoria",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    CustomInputField(
                        label = "Nome da categoria",
                        value = categoria,
                        onValueChange = { categoria = it }
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    /**
                     * Botão que salva a categoria no Firestore.
                     *
                     * Verifica se o campo não está vazio e obtém o ID do usuário atual do FirebaseAuth.
                     * Em seguida, chama FirebaseUtils.addUserCategory passando a categoria.
                     * Exibe Toasts para sucesso ou erro e finaliza a Activity em caso de sucesso.
                     */

                    Button(
                        onClick = {
                            if (categoria.isNotBlank()) {
                                val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                                FirebaseUtils.addUserCategory(
                                    userId = userId,
                                    categoryName = categoria,
                                    onComplete = { success ->
                                        if (success) {
                                            Toast.makeText(context, "Categoria adicionada com sucesso!", Toast.LENGTH_SHORT).show()
                                            finish()
                                        } else {
                                            Toast.makeText(context, "Erro ao adicionar categoria", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .shadow(4.dp, RoundedCornerShape(12.dp)),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(gradient, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Salvar categoria",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }

    /**
     * Campo de texto customizado para entrada de dados.
     *
     * @param label Texto do rótulo do campo.
     * @param value Valor atual do campo.
     * @param onValueChange Callback disparado ao alterar o texto.
     * @param isPassword Indica se o campo deve mascarar a entrada como senha.
     */

    @Composable
    fun CustomInputField(
        label: String,
        value: String,
        onValueChange: (String) -> Unit,
        isPassword: Boolean = false
    ) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            singleLine = true,
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            modifier = Modifier
                .fillMaxWidth()
                .shadow(2.dp, RoundedCornerShape(12.dp))
                .background(Color.White, RoundedCornerShape(12.dp)),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )
    }
}