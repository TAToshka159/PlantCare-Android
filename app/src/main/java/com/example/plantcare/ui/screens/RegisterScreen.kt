// RegisterScreen.kt
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
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: (String, Boolean, Boolean) -> Unit, // <-- Изменили: логин, isGuest, isAdmin
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

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
                    return@Button
                }
                if (password.length < 4) {
                    passwordError = "Пароль должен быть не короче 4 символов"
                    return@Button
                }

                coroutineScope.launch {
                    val app = context.applicationContext as PlantCareApplication
                    val dao = app.database.plantCareDao()
                    val existingUser = dao.getUserByLogin(login.trim())

                    if (existingUser != null) {
                        loginError = "Логин уже занят"
                        return@launch
                    }

                    val newUser = User(
                        id = 0,
                        login = login.trim(),
                        passwordHash = PasswordUtils.hashPassword(password),
                        role = "user" // <-- Роль по умолчанию "user"
                    )

                    val userId = dao.insertUser(newUser)
                    context.saveCurrentUserId(userId)
                    context.saveUserName(newUser.login)
                    context.saveOnboardingCompleted(true)
                    context.saveIsGuest(false) // <-- Не гость
                    context.saveUserRole(false) // <-- Не админ
                    onRegisterSuccess(newUser.login, false, false) // <-- Не гость, не админ
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("Зарегистрироваться", color = MaterialTheme.colorScheme.onPrimary)
        }

        TextButton(onClick = onNavigateToLogin) {
            Text("Войти", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        TextButton(
            onClick = {
                context.saveCurrentUserId(-1) // -1 = гость
                context.saveUserName("Гость")
                context.saveOnboardingCompleted(true)
                context.saveIsGuest(true) // <-- Сохраняем, что это гость
                context.saveUserRole(false) // <-- Гость не админ
                onRegisterSuccess("Гость", true, false) // <-- Гость, не админ
            }
        ) {
            Text("Войти как гость", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}