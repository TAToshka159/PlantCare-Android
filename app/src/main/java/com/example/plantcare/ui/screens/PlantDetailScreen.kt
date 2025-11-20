package com.example.plantcare.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.plantcare.PlantCareApplication
import com.example.plantcare.data.database.entity.CareEvent
import com.example.plantcare.data.database.entity.Photo
import com.example.plantcare.util.FileUtil
import com.example.plantcare.util.PlantMood
import com.example.plantcare.util.PluralUtil
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantDetailScreen(
    plantId: Long,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onPhotoClick: (List<String>, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val app = context.applicationContext as PlantCareApplication
    val dao = app.database.plantCareDao()

    var plant by remember { mutableStateOf<com.example.plantcare.data.database.entity.Plant?>(null) }
    var careEvents by remember { mutableStateOf<List<CareEvent>>(emptyList()) }
    var photos by remember { mutableStateOf<List<Photo>>(emptyList()) }
    // --- Новое состояние для информации из энциклопедии ---
    var encyclopediaEntry by remember { mutableStateOf<com.example.plantcare.data.database.entity.EncyclopediaEntry?>(null) }
    // --- /Новое состояние ---

    LaunchedEffect(plantId) {
        plant = dao.getPlantById(plantId)
        careEvents = dao.getUpcomingCareEvents(plantId)
        photos = dao.getPhotosByPlant(plantId)

        // --- Получение информации из энциклопедии ---
        plant?.type?.let { typeName ->
            encyclopediaEntry = dao.getEncyclopediaEntryByTypeName(typeName)
            println("DEBUG: Looking for type: '$typeName', Found EncyclopediaEntry: $encyclopediaEntry") // <-- Отладочная строка
        }
        // --- /Получение информации из энциклопедии ---
    }

    if (plant == null) {
        // --- Оборачиваем в Scaffold с TopAppBar ---
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Детали растения") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Назад"
                            )
                        }
                    }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Растение не найдено", style = MaterialTheme.typography.headlineMedium)
            }
        }
        return
    }

    val p = plant!!
    val coroutineScope = rememberCoroutineScope()

    val wateringEvent = careEvents.find { it.type == "watering" }
    val fertilizingEvent = careEvents.find { it.type == "fertilizing" }

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: android.net.Uri? ->
        uri?.let {
            val savedPath = FileUtil.saveImageFromUri(context, it, "plant_${System.currentTimeMillis()}.jpg")
            if (savedPath != null) {
                coroutineScope.launch {
                    val newPhotoUri = android.net.Uri.fromFile(File(savedPath)).toString()
                    val newPhoto = Photo(
                        id = System.currentTimeMillis(),
                        plantId = p.id,
                        photoUri = newPhotoUri,
                        date = System.currentTimeMillis()
                    )
                    dao.insertPhoto(newPhoto)
                    photos = dao.getPhotosByPlant(p.id)
                }
            }
        }
    }

    // --- Оборачиваем в Scaffold с TopAppBar ---
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Детали растения") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                },
                actions = { // <-- Добавляем иконку карандаша справа
                    IconButton(onClick = onEdit) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Изменить"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text("Название: ${p.name}", style = MaterialTheme.typography.titleLarge)
            Text("Тип: ${p.type}", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(top = 4.dp))
            Text("Комната: ${p.room}", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(top = 4.dp))

            // Смайлик настроения
            val mood = PlantMood.getMood(careEvents)
            Text("Состояние: $mood", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(top = 8.dp))

            Text("Добавлено: ${formatDate(p.createdAt)}", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 4.dp))

            // --- Отображение информации из энциклопедии ---
            encyclopediaEntry?.let { entry ->
                Spacer(modifier = Modifier.height(16.dp))
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                Text("Информация из энциклопедии:", style = MaterialTheme.typography.titleMedium)

                Text("Правила ухода: ${entry.careRules}", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 4.dp))
                Text("Советы по климату: ${entry.climateTips}", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 4.dp))
            }
            // --- /Отображение информации из энциклопедии ---

            // Фото-история
            Text("Фото-история:", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 20.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (!p.photoUri.isNullOrBlank()) {
                    item {
                        val allUris = listOf(p.photoUri) + photos.map { it.photoUri }
                        Image(
                            painter = rememberAsyncImagePainter(p.photoUri),
                            contentDescription = "Главное фото",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .clickable { onPhotoClick(allUris, 0) }
                        )
                    }
                }

                items(photos.size) { index ->
                    val photo = photos[index]
                    val allUris = listOfNotNull(p.photoUri) + photos.map { it.photoUri }
                    val clickIndex = if (p.photoUri != null) index + 1 else index

                    Box {
                        Image(
                            painter = rememberAsyncImagePainter(photo.photoUri),
                            contentDescription = "Фото",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .clickable { onPhotoClick(allUris, clickIndex) }
                        )
                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    dao.deletePhoto(photo.id)
                                    photos = dao.getPhotosByPlant(p.id)
                                }
                            },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.error.copy(alpha = 0.8f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Удалить",
                                tint = MaterialTheme.colorScheme.onError
                            )
                        }
                    }
                }

                item {
                    IconButton(
                        onClick = { photoPicker.launch("image/*") },
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Добавить фото",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Ближайший уход
            Text("Ближайший уход:", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 20.dp))

            // Полив
            if (wateringEvent != null) {
                val days = daysUntil(wateringEvent.datePlanned)
                val dateStr = formatDate(wateringEvent.datePlanned)
                val daysWord = PluralUtil.daysUntil(days)
                val wateringText = when {
                    days > 0 -> "Полив через $days $daysWord ($dateStr)"
                    days == 0 -> "Полив сегодня! ($dateStr)"
                    else -> {
                        val overdueDays = -days
                        val overdueWord = PluralUtil.daysUntil(overdueDays)
                        "Полив просрочен на $overdueDays $overdueWord ($dateStr)"
                    }
                }
                // --- Используем только primary цвет для текста ---
                Text(wateringText, color = MaterialTheme.colorScheme.onSurface)

                // Кнопка "Полито"
                Button(
                    onClick = {
                        coroutineScope.launch {
                            val newDate = System.currentTimeMillis() + (p.wateringInterval * 24L * 60 * 60 * 1000)
                            val updatedEvent = wateringEvent.copy(datePlanned = newDate)
                            dao.updateCareEvent(updatedEvent)
                            // Обновим список событий
                            careEvents = dao.getUpcomingCareEvents(plantId)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Text("Полито")
                }
            }

            // Удобрение
            if (fertilizingEvent != null) {
                val days = daysUntil(fertilizingEvent.datePlanned)
                val dateStr = formatDate(fertilizingEvent.datePlanned)
                val daysWord = PluralUtil.daysUntil(days)
                val fertilizingText = when {
                    days > 0 -> "Удобрение через $days $daysWord ($dateStr)"
                    days == 0 -> "Удобрение сегодня! ($dateStr)"
                    else -> {
                        val overdueDays = -days
                        val overdueWord = PluralUtil.daysUntil(overdueDays)
                        "Удобрение просрочено на $overdueDays $overdueWord ($dateStr)"
                    }
                }
                // --- Используем только primary цвет для текста ---
                Text(fertilizingText, color = MaterialTheme.colorScheme.onSurface)

                // Кнопка "Удобрено"
                Button(
                    onClick = {
                        coroutineScope.launch {
                            val newDate = System.currentTimeMillis() + (p.fertilizingInterval * 24L * 60 * 60 * 1000)
                            val updatedEvent = fertilizingEvent.copy(datePlanned = newDate)
                            dao.updateCareEvent(updatedEvent)
                            // Обновим список событий
                            careEvents = dao.getUpcomingCareEvents(plantId)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Text("Удобрено")
                }
            }
            // --- УБРАЛИ КНОПКУ "ИЗМЕНИТЬ" ---
        }
    }
}

private fun formatDate(timestamp: Long): String {
    return SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date(timestamp))
}

private fun daysUntil(futureTimestamp: Long): Int {
    val now = System.currentTimeMillis()
    val diffMillis = futureTimestamp - now
    return (diffMillis / (1000 * 60 * 60 * 24)).toInt()
}