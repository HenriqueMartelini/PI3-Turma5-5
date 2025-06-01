package com.puc.superid.ui.registration

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.puc.superid.R
import com.puc.superid.ui.theme.SuperidTheme
import com.puc.superid.utils.FirebaseUtils
import com.puc.superid.utils.StringUtils

class RegisterLoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SuperidTheme {
                val navController = rememberNavController()
                AppNavHost(navController)
            }
        }
    }
}


/**
 * NavHost que gerencia a navegação entre as telas principais do app.
 *
 * @param navController controlador de navegação para trocar telas.
 */

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "register_login") {
        composable("register_login") {
            RegisterLoginScreen(navController)
        }
        composable("home") {
            Text(
                "Home Screen",
                color = Color.White,
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.DarkGray)
            )
        }
        composable("nova_categoria") {
            Text(
                "Tela de Cadastro de Nova Categoria",
                color = Color.White,
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.DarkGray)
            )
        }
    }
}


/**
 * Tela principal para adicionar um novo login com usuário, senha, site e categoria.
 *
 * Exibe um formulário para entrada de dados, valida os campos, encripta a senha e salva
 * os dados no Firebase. Permite também selecionar ou adicionar categorias.
 *
 * @param navController controlador de navegação para trocar telas.
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterLoginScreen(navController: NavController) {
    val context = LocalContext.current
    val categories = remember { mutableStateListOf<String>() }
    val selectedCategory = remember { mutableStateOf("") }
    val isDropDownExpanded = remember { mutableStateOf(false) }
    val itemPosition = remember { mutableIntStateOf(0) }

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var site by remember { mutableStateOf("") }

    val gradient = Brush.horizontalGradient(
        listOf(Color(0xFF3E8EFF), Color(0xFF6C63FF))
    )

    LaunchedEffect(Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        FirebaseUtils.fetchUserCategories(userId) { categorias ->
            categories.clear()
            categories.addAll(categorias)
            if (categories.isNotEmpty()) {
                selectedCategory.value = categories.first()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "SuperID",
                        color = Color.White,
                        modifier = Modifier.clickable {
                            (context as? RegisterLoginActivity)?.finish()
                        }
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Buscar",
                            tint = Color.White
                        )
                    }
                    IconButton(onClick = { }) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Adicionar",
                            tint = Color.White
                        )
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
                .background(Color(0xFF0D0B2D))
                .padding(horizontal = 24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
                modifier = Modifier.fillMaxSize()
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Adicionar novo login",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xAA4B5C8A))
                ) {
                    Column(
                        modifier = Modifier
                            .padding(24.dp)
                            .fillMaxWidth()
                    ) {
                        CustomInputField(
                            label = "Usuário",
                            value = username,
                            onValueChange = { username = it }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        CustomInputField(
                            label = "Senha",
                            value = password,
                            onValueChange = { password = it },
                            isPassword = true
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        CustomInputField(
                            label = "Site",
                            value = site,
                            onValueChange = { site = it }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box {
                                Row(
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable {
                                        isDropDownExpanded.value = true
                                    }
                                ) {
                                    Text(
                                        text = selectedCategory.value.ifEmpty { "Escolha uma categoria" },
                                        color = Color.Black,
                                        fontSize = 16.sp,
                                    )
                                    Icon(
                                        painter = painterResource(id = R.drawable.images),
                                        contentDescription = "DropDown Icon",
                                        modifier = Modifier
                                            .size(24.dp)
                                            .padding(start = 8.dp)
                                    )
                                }
                                DropdownMenu(
                                    expanded = isDropDownExpanded.value,
                                    onDismissRequest = {
                                        isDropDownExpanded.value = false
                                    }) {
                                    categories.forEachIndexed { index, category ->
                                        DropdownMenuItem(
                                            text = {
                                                Text(text = category)
                                            },
                                            onClick = {
                                                selectedCategory.value = category
                                                isDropDownExpanded.value = false
                                            }
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Adicionar nova categoria",
                                color = Color(0xFF3E8EFF),
                                modifier = Modifier
                                    .clickable {
                                        context.startActivity(
                                            Intent(
                                                context,
                                                NewCategoryActivity::class.java
                                            )
                                        )
                                    }
                                    .padding(start = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        Button(
                            onClick = {
                                if (username.isNotBlank() && password.isNotBlank() && site.isNotBlank()) {
                                    if (!StringUtils.isValidEmail(username)) {
                                        Toast.makeText(
                                            context,
                                            "Email inválido",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        return@Button
                                    }

                                    if (!StringUtils.isValidDomain(site)) {
                                        Toast.makeText(
                                            context,
                                            "O domínio do site é inválido. Deve começar com www. e não conter subdomínios ou caminhos.",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        return@Button
                                    }

                                    val encryptedPassword = StringUtils.encryptString(context, password)

                                    FirebaseUtils.saveUserLogin(
                                        userId = FirebaseAuth.getInstance().currentUser?.uid ?: "",
                                        site = site,
                                        email = username,
                                        password = encryptedPassword,
                                        category = selectedCategory.value,
                                        context = context
                                    ) { success ->
                                        if (success) {
                                            Toast.makeText(context, "Login salvo com sucesso!", Toast.LENGTH_SHORT).show()
                                            navController.popBackStack()
                                        }
                                    }
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Preencha todos os campos",
                                        Toast.LENGTH_SHORT
                                    ).show()
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
                                    text = "Adicionar novo login",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Campo de entrada customizado para textos simples ou senha.
 *
 * @param label texto do rótulo do campo.
 * @param value valor atual do campo.
 * @param onValueChange callback para atualizar o valor do campo.
 * @param isPassword define se o campo deve ocultar o texto (senha).
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