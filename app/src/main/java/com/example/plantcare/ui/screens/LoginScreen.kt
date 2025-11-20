// LoginScreen.kt
package com.example.plantcare.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.plantcare.PlantCareApplication
import com.example.plantcare.data.PasswordUtils
import com.example.plantcare.data.database.entity.User
import com.example.plantcare.data.saveCurrentUserId
import com.example.plantcare.data.saveIsGuest
import com.example.plantcare.data.saveOnboardingCompleted
import com.example.plantcare.data.saveUserRole
import com.example.plantcare.data.saveUserName
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: (String, Boolean) -> Unit, // <-- Изменили: логин и isAdmin
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

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
                if (login.isEmpty() || password.isEmpty()) {
                    error = "Заполните все поля"
                    return@Button
                }

                coroutineScope.launch {
                    val app = context.applicationContext as PlantCareApplication
                    val dao = app.database.plantCareDao()
                    val user = dao.getUserByLogin(login)
                    val enteredHash = PasswordUtils.hashPassword(password)

                    if (user != null && user.passwordHash == enteredHash) {
                        val isAdmin = user.role == "admin"
                        context.saveCurrentUserId(user.id)
                        context.saveUserName(user.login)
                        context.saveOnboardingCompleted(true)
                        context.saveIsGuest(false) // <-- Не гость
                        context.saveUserRole(isAdmin) // <-- Сохраняем роль
                        onLoginSuccess(user.login, isAdmin) // <-- Передаём логин и isAdmin
                    } else {
                        error = "Такой пользователь не существует"
                    }
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