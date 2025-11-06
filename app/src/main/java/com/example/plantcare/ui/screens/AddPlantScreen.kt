// AddPlantScreen.kt
package com.example.plantcare.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.plantcare.PlantCareApplication
import com.example.plantcare.data.getCurrentUserId
import com.example.plantcare.data.database.entity.CareEvent
import com.example.plantcare.data.database.entity.Plant
import com.example.plantcare.util.FileUtil
import kotlinx.coroutines.launch
import java.io.File

// --- ВАЖНО: Перемещён импорт encyclopediaEntries в начало файла ---
import com.example.plantcare.ui.screens.encyclopediaEntries
// --- /ВАЖНО ---

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
    var selectedPhotoUri by remember { mutableStateOf<Uri?>(null) }

    // --- Состояния для кастомного выпадающего списка ---
    var isDropdownExpanded by remember { mutableStateOf(false) }
    val allEntryNames = remember { encyclopediaEntries.map { it.name } + listOf("Нет в списке") }
    val filteredEntries by remember(type) {
        mutableStateOf(
            // Всегда фильтруем по введённому тексту, даже если он пустой
            allEntryNames.filter { it.contains(type, ignoreCase = true) }
        )
    }
    // --- /Состояния ---

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val savedPath = FileUtil.saveImageFromUri(context, it, "plant_${System.currentTimeMillis()}.jpg")
            selectedPhotoUri = if (savedPath != null) Uri.fromFile(File(savedPath)) else null
        }
    }

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

        // --- Кастомный выпадающий список ---
        Box(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
            OutlinedTextField(
                value = type,
                onValueChange = { value ->
                    type = value
                    // Не меняем isDropdownExpanded здесь, только при клике на иконку
                },
                label = { Text("Тип (например, Фикус)") },
                trailingIcon = {
                    // Иконка стрелки меняется в зависимости от состояния меню
                    IconButton(
                        onClick = {
                            // Переключаем состояние меню при клике на иконку
                            isDropdownExpanded = !isDropdownExpanded
                        }
                    ) {
                        Icon(
                            imageVector = if (isDropdownExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                            contentDescription = if (isDropdownExpanded) "Скрыть список" else "Показать список"
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            // DropdownMenu позиционируется под OutlinedTextField
            // Отображаем, если isDropdownExpanded = true, независимо от содержимого type
            if (isDropdownExpanded) {
                DropdownMenu(
                    expanded = isDropdownExpanded,
                    onDismissRequest = { isDropdownExpanded = false }, // Закрытие при клике вне
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .fillMaxWidth() // Просто заполняем ширину под TextField
                        .padding(horizontal = 8.dp)
                ) {
                    // Показываем отфильтрованные элементы
                    if (filteredEntries.isNotEmpty()) {
                        filteredEntries.forEach { entryName ->
                            DropdownMenuItem(
                                text = { Text(text = entryName) },
                                onClick = {
                                    type = if (entryName == "Нет в списке") {
                                        "" // Очищаем поле, если выбрана опция "Нет в списке"
                                    } else {
                                        entryName // Подставляем выбранное имя
                                    }
                                    isDropdownExpanded = false // Закрываем меню
                                },
                            )
                        }
                    } else {
                        // Если фильтр не дал результатов, показываем "Нет результатов"
                        DropdownMenuItem(
                            text = { Text("Нет результатов") },
                            onClick = { /* Ничего не делаем */ },
                            enabled = false // Делаем неактивным
                        )
                    }
                }
            }
        }
        // --- /Кастомный выпадающий список ---

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

        // Кнопка выбора фото
        OutlinedButton(
            onClick = { photoPicker.launch("image/*") },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Выбрать фото растения")
        }

        // Превью фото
        if (selectedPhotoUri != null) {
            Image(
                painter = rememberAsyncImagePainter(selectedPhotoUri),
                contentDescription = "Фото растения",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(150.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .padding(top = 8.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }

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

                    val photoUriString = selectedPhotoUri?.toString()

                    coroutineScope.launch {
                        val app = context.applicationContext as PlantCareApplication
                        val dao = app.database.plantCareDao()
                        val plant = Plant(
                            id = now,
                            userId = userId,
                            name = name.trim(),
                            type = type.trim(), // <-- Теперь будет подставлено из списка или введено вручную
                            photoUri = photoUriString,
                            room = room.trim(),
                            createdAt = now,
                            wateringInterval = watering,
                            fertilizingInterval = fertilizing
                        )
                        val plantId = dao.insertPlant(plant)

                        // Создаём события ухода
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