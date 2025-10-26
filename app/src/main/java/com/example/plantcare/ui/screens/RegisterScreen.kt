// ui/screens/RegisterScreen.kt
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
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var login by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loginError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Регистрация",
            fontSize = 24.sp,
            fontWeight = MaterialTheme.typography.titleLarge.fontWeight,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        OutlinedTextField(
            value = login,
            onValueChange = { login = it },
            label = { Text("Логин") },
            isError = loginError != null,
            supportingText = { if (loginError != null) Text(loginError!!) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Пароль") },
            visualTransformation = PasswordVisualTransformation(),
            isError = passwordError != null,
            supportingText = { if (passwordError != null) Text(passwordError!!) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        )

        Button(
            onClick = {
                loginError = null
                passwordError = null

                if (login.trim().isEmpty()) {
                    loginError = "Введите логин"
                } else if (password.length < 4) {
                    passwordError = "Пароль должен быть не короче 4 символов"
                } else {
                    context.saveUserName(login.trim())
                    context.saveUserPassword(password)
                    context.saveOnboardingCompleted(true)
                    onRegisterSuccess()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("Зарегистрироваться", color = MaterialTheme.colorScheme.onPrimary)
        }

        // "Войти" — серая текстовая кнопка
        TextButton(onClick = onNavigateToLogin) {
            Text("Войти", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        // "Войти как гость" — такая же текстовая кнопка
        TextButton(
            onClick = {
                context.saveUserName("Гость")
                context.saveUserPassword("")
                context.saveOnboardingCompleted(true)
                onRegisterSuccess()
            }
        ) {
            Text("Войти как гость", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}