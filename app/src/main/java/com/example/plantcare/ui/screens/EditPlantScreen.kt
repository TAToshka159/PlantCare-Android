// EditPlantScreen.kt
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
import com.example.plantcare.data.database.entity.CareEvent
import com.example.plantcare.data.database.entity.Plant
import kotlinx.coroutines.launch

@Composable
fun EditPlantScreen(
    plantId: Long,
    onPlantUpdated: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val app = context.applicationContext as PlantCareApplication
    val dao = app.database.plantCareDao()

    var plant by remember { mutableStateOf<Plant?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(plantId) {
        plant = dao.getPlantById(plantId)
        isLoading = false
    }

    if (isLoading || plant == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val p = plant!!
    var name by remember { mutableStateOf(p.name) }
    var type by remember { mutableStateOf(p.type) }
    var room by remember { mutableStateOf(p.room) }
    var wateringInterval by remember { mutableStateOf(p.wateringInterval.toString()) }
    var fertilizingInterval by remember { mutableStateOf(p.fertilizingInterval.toString()) }

    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Редактировать растение", fontSize = 24.sp, modifier = Modifier.padding(bottom = 24.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Название") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = type,
            onValueChange = { type = it },
            label = { Text("Тип") },
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

        Button(
            onClick = {
                if (name.isBlank() || type.isBlank() || room.isBlank()) return@Button

                val watering = wateringInterval.toIntOrNull() ?: p.wateringInterval
                val fertilizing = fertilizingInterval.toIntOrNull() ?: p.fertilizingInterval
                val updated = p.copy(
                    name = name.trim(),
                    type = type.trim(),
                    room = room.trim(),
                    wateringInterval = watering,
                    fertilizingInterval = fertilizing
                )
                val now = System.currentTimeMillis()

                coroutineScope.launch {
                    // Обновляем растение
                    dao.updatePlant(updated)

                    // 🔑 ПЕРЕСОЗДАЁМ СОБЫТИЯ УХОДА
                    dao.deleteCareEventsByPlant(updated.id)
                    dao.insertCareEvent(
                        CareEvent(
                            id = now + 1,
                            plantId = updated.id,
                            type = "watering",
                            datePlanned = now + updated.wateringInterval * 24L * 60 * 60 * 1000,
                            dateDone = null
                        )
                    )
                    dao.insertCareEvent(
                        CareEvent(
                            id = now + 2,
                            plantId = updated.id,
                            type = "fertilizing",
                            datePlanned = now + updated.fertilizingInterval * 24L * 60 * 60 * 1000,
                            dateDone = null
                        )
                    )

                    onPlantUpdated()
                }
            },
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        ) {
            Text("Сохранить")
        }

        Button(
            onClick = {
                coroutineScope.launch {
                    dao.deletePlantAndRelatedData(p.id)
                    onPlantUpdated()
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Удалить растение", color = MaterialTheme.colorScheme.onError)
        }
    }
}