// ui/screens/LoginScreen.kt
package com.example.plantcare.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.plantcare.data.*

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var login by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Вход",
            fontSize = 24.sp,
            fontWeight = MaterialTheme.typography.titleLarge.fontWeight,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        OutlinedTextField(
            value = login,
            onValueChange = { login = it },
            label = { Text("Логин") },
            isError = error != null,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Пароль") },
            visualTransformation = PasswordVisualTransformation(),
            isError = error != null,
            supportingText = { if (error != null) Text(error!!) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        )

        Button(
            onClick = {
                val savedLogin = context.getUserName()
                val savedPassword = context.getUserPassword()

                // Гость не может войти через экран входа
                if (savedLogin == "Гость") {
                    error = "Такой пользователь не существует"
                    return@Button
                }

                if (login == savedLogin && password == savedPassword) {
                    context.saveOnboardingCompleted(true)
                    onLoginSuccess()
                } else {
                    error = "Такой пользователь не существует"
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("Войти", color = MaterialTheme.colorScheme.onPrimary)
        }

        TextButton(onClick = onNavigateToRegister) {
            Text("Нет аккаунта? Зарегистрироваться", color = MaterialTheme.colorScheme.primary)
        }
    }
}