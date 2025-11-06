// EditPlantScreen.kt
package com.example.plantcare.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.plantcare.PlantCareApplication
import com.example.plantcare.data.database.entity.CareEvent
import com.example.plantcare.data.database.entity.Photo
import com.example.plantcare.data.database.entity.Plant
import com.example.plantcare.util.FileUtil
import kotlinx.coroutines.launch
import java.io.File

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
    var photos by remember { mutableStateOf<List<Photo>>(emptyList()) }

    LaunchedEffect(plantId) {
        plant = dao.getPlantById(plantId)
        photos = dao.getPhotosByPlant(plantId)
        isLoading = false
    }

    if (isLoading || plant == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
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

    // Launcher для выбора фото из галереи
    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val savedPath = FileUtil.saveImageFromUri(context, it, "plant_${System.currentTimeMillis()}.jpg")
            if (savedPath != null) {
                val newPhotoUri = Uri.fromFile(File(savedPath)).toString()
                coroutineScope.launch {
                    // Сохраняем новое фото в таблицу photos
                    val newPhoto = Photo(
                        id = System.currentTimeMillis(),
                        plantId = p.id,
                        photoUri = newPhotoUri,
                        date = System.currentTimeMillis()
                    )
                    dao.insertPhoto(newPhoto)
                    photos = dao.getPhotosByPlant(p.id)

                    // Обновляем главное фото
                    val updated = p.copy(photoUri = newPhotoUri)
                    dao.updatePlant(updated)
                    plant = updated
                }
            }
        }
    }

    // Диалог выбора способа смены фото
    var showPhotoDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(16.dp)
    ) {
        Text("Редактировать растение", fontSize = 28.sp, fontWeight = FontWeight.Bold,modifier = Modifier.padding(bottom = 24.dp))

        // Превью главного фото с возможностью замены
        if (!p.photoUri.isNullOrBlank()) {
            Image(
                painter = rememberAsyncImagePainter(p.photoUri),
                contentDescription = "Главное фото",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(150.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .align(Alignment.CenterHorizontally)
                    .clickable { showPhotoDialog = true }
                    .padding(bottom = 16.dp)
            )
        } else {
            OutlinedButton(
                onClick = { showPhotoDialog = true },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 16.dp)
            ) {
                Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                Text("Добавить фото")
            }
        }

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

        // Кнопка сохранения
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
                    dao.updatePlant(updated)

                    // Пересоздаём события ухода
                    dao.deleteCareEventsByPlant(updated.id)
                    dao.insertCareEvent(
                        CareEvent(
                            id = now + 1,
                            plantId = updated.id,
                            type = "watering",
                            datePlanned = now + updated.wateringInterval * 24L * 60 * 60 * 1000
                        )
                    )
                    dao.insertCareEvent(
                        CareEvent(
                            id = now + 2,
                            plantId = updated.id,
                            type = "fertilizing",
                            datePlanned = now + updated.fertilizingInterval * 24L * 60 * 60 * 1000
                        )
                    )

                    onPlantUpdated()
                }
            },
            modifier = Modifier.fillMaxWidth().padding(top = 24.dp)
        ) {
            Text("Сохранить")
        }

        // Кнопка удаления
        Button(
            onClick = {
                coroutineScope.launch {
                    dao.deletePlantAndRelatedData(p.id)
                    onPlantUpdated()
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        ) {
            Text("Удалить растение", color = MaterialTheme.colorScheme.onError)
        }
    }

    // Диалог выбора способа смены фото
    if (showPhotoDialog) {
        AlertDialog(
            onDismissRequest = { showPhotoDialog = false },
            title = { Text("Сменить фото") },
            text = { Text("Выберите фото из галереи") },
            confirmButton = {
                Button(
                    onClick = {
                        photoPicker.launch("image/*")
                        showPhotoDialog = false
                    }
                ) {
                    Text("Выбрать фото")
                }
            },
            dismissButton = {
                Button(onClick = { showPhotoDialog = false }) {
                    Text("Отмена")
                }
            }
        )
    }
}