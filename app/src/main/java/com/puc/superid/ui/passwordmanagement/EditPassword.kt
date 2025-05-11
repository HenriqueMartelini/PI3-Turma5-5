package com.puc.superid.ui.passwordmanagement

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.puc.superid.viewmodel.EditPasswordViewModel
import com.puc.superid.ui.theme.Purple40
import com.puc.superid.ui.theme.Pink40

@Composable
fun EditPasswordScreen(
    passwordId: String,
    navController: NavController,
    viewModel: EditPasswordViewModel = viewModel()
) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadPasswordData(passwordId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = state.title,
            onValueChange = { viewModel.onFieldChange(it, state.username, state.password, state.category) },
            label = { Text("Título") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = state.username,
            onValueChange = { viewModel.onFieldChange(state.title, it, state.password, state.category) },
            label = { Text("Usuário") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = state.password,
            onValueChange = { viewModel.onFieldChange(state.title, state.username, it, state.category) },
            label = { Text("Senha") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = state.category,
            onValueChange = { viewModel.onFieldChange(state.title, state.username, state.password, it) },
            label = { Text("Categoria") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                viewModel.updatePassword(
                    onSuccess = {
                        Toast.makeText(context, "Senha atualizada!", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    },
                    onError = {
                        Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                    }
                )
            },
            colors = ButtonDefaults.buttonColors(containerColor = Purple40)
        ) {
            Text("Salvar")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                viewModel.deletePassword(
                    onSuccess = {
                        Toast.makeText(context, "Senha excluída!", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    },
                    onError = {
                        Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                    }
                )
            },
            colors = ButtonDefaults.buttonColors(containerColor = Pink40)
        ) {
            Text("Excluir")
        }
    }
}