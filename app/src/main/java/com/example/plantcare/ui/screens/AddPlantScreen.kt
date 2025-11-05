// AddPlantScreen.kt
package com.example.plantcare.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.plantcare.PlantCareApplication
import com.example.plantcare.data.getCurrentUserId
import com.example.plantcare.data.database.entity.CareEvent
import com.example.plantcare.data.database.entity.Plant
import kotlinx.coroutines.launch

@Composable
fun AddPlantScreen(
    onPlantAdded: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val userId = context.getCurrentUserId()

    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    var room by remember { mutableStateOf("") }
    var wateringInterval by remember { mutableStateOf("7") }
    var fertilizingInterval by remember { mutableStateOf("30") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Добавить растение", fontSize = 24.sp, modifier = Modifier.padding(bottom = 24.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Название") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = type,
            onValueChange = { type = it },
            label = { Text("Тип (например, Фикус)") },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
        )

        OutlinedTextField(
            value = room,
            onValueChange = { room = it },
            label = { Text("Комната") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
        )

        OutlinedTextField(
            value = wateringInterval,
            onValueChange = { wateringInterval = it },
            label = { Text("Полив каждые N дней") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
        )

        OutlinedTextField(
            value = fertilizingInterval,
            onValueChange = { fertilizingInterval = it },
            label = { Text("Удобрение каждые N дней") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(onClick = onPlantAdded) {
                Text("Отмена")
            }
            Button(
                onClick = {
                    if (name.isBlank() || type.isBlank() || room.isBlank()) return@Button
                    val watering = wateringInterval.toIntOrNull() ?: 7
                    val fertilizing = fertilizingInterval.toIntOrNull() ?: 30
                    val now = System.currentTimeMillis()

                    coroutineScope.launch {
                        val app = context.applicationContext as PlantCareApplication
                        val dao = app.database.plantCareDao()
                        val plant = Plant(
                            id = now,
                            userId = userId,
                            name = name.trim(),
                            type = type.trim(),
                            photoUri = null,
                            room = room.trim(),
                            createdAt = now,
                            wateringInterval = watering,
                            fertilizingInterval = fertilizing
                        )
                        val plantId = dao.insertPlant(plant)

                        // 🔑 СОЗДАЁМ СОБЫТИЯ УХОДА
                        dao.insertCareEvent(
                            CareEvent(
                                id = now + 1,
                                plantId = plantId,
                                type = "watering",
                                datePlanned = now + watering * 24L * 60 * 60 * 1000,
                                dateDone = null
                            )
                        )
                        dao.insertCareEvent(
                            CareEvent(
                                id = now + 2,
                                plantId = plantId,
                                type = "fertilizing",
                                datePlanned = now + fertilizing * 24L * 60 * 60 * 1000,
                                dateDone = null
                            )
                        )

                        onPlantAdded()
                    }
                },
                modifier = Modifier.weight(1f).padding(start = 8.dp)
            ) {
                Text("Сохранить")
            }
        }
    }
}