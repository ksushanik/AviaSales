package com.example.aviasales

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.aviasales.ui.theme.AviaSalesTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.ui.platform.LocalFocusManager
import java.time.ZoneOffset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.text.KeyboardOptions

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AviaSalesTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    FlightSearchScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlightSearchScreen(modifier: Modifier = Modifier) {
    var departureCity by remember { mutableStateOf("") }
    var arrivalCity by remember { mutableStateOf("") }
    var departureDate by remember { mutableStateOf<LocalDate?>(null) }
    var returnDate by remember { mutableStateOf<LocalDate?>(null) }
    var adults by remember { mutableStateOf("1") }
    var children by remember { mutableStateOf("0") }
    var infants by remember { mutableStateOf("0") }
    
    // Состояния для отображения ошибок
    var departureCityError by remember { mutableStateOf(false) }
    var arrivalCityError by remember { mutableStateOf(false) }
    var departureDateError by remember { mutableStateOf(false) }
    var passengersError by remember { mutableStateOf(false) }
    
    // Состояния для DatePicker
    var showDepartureDatePicker by remember { mutableStateOf(false) }
    var showReturnDatePicker by remember { mutableStateOf(false) }
    
    val context = LocalContext.current
    val cities = context.resources.getStringArray(R.array.cities).toList()
    var isDropdownExpandedDeparture by remember { mutableStateOf(false) }
    var isDropdownExpandedArrival by remember { mutableStateOf(false) }
    
    val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    val focusManager = LocalFocusManager.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Города вылета и прилета
        ExposedDropdownMenuBox(
            expanded = isDropdownExpandedDeparture,
            onExpandedChange = { isDropdownExpandedDeparture = it }
        ) {
            OutlinedTextField(
                value = departureCity,
                onValueChange = { departureCity = it },
                label = { Text("Город вылета") },
                isError = departureCityError,
                supportingText = if (departureCityError) {
                    { Text(text = "Выберите город вылета") }
                } else null,
                trailingIcon = if (departureCityError) {
                    { Icon(
                        imageVector = Icons.Rounded.Warning,
                        contentDescription = "error",
                        tint = MaterialTheme.colorScheme.error
                    ) }
                } else null,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = isDropdownExpandedDeparture,
                onDismissRequest = { isDropdownExpandedDeparture = false }
            ) {
                cities.forEach { city ->
                    DropdownMenuItem(
                        text = { Text(city) },
                        onClick = {
                            departureCity = city
                            departureCityError = false
                            isDropdownExpandedDeparture = false
                        }
                    )
                }
            }
        }

        ExposedDropdownMenuBox(
            expanded = isDropdownExpandedArrival,
            onExpandedChange = { isDropdownExpandedArrival = it }
        ) {
            OutlinedTextField(
                value = arrivalCity,
                onValueChange = { arrivalCity = it },
                label = { Text("Город прилета") },
                isError = arrivalCityError,
                supportingText = if (arrivalCityError) {
                    { Text(text = "Выберите город прилета") }
                } else null,
                trailingIcon = if (arrivalCityError) {
                    { Icon(
                        imageVector = Icons.Rounded.Warning,
                        contentDescription = "error",
                        tint = MaterialTheme.colorScheme.error
                    ) }
                } else null,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = isDropdownExpandedArrival,
                onDismissRequest = { isDropdownExpandedArrival = false }
            ) {
                cities.forEach { city ->
                    DropdownMenuItem(
                        text = { Text(city) },
                        onClick = {
                            arrivalCity = city
                            arrivalCityError = false
                            isDropdownExpandedArrival = false
                        }
                    )
                }
            }
        }

        // Даты
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = departureDate?.format(dateFormatter) ?: "",
                onValueChange = { },
                label = { Text("Дата вылета") },
                readOnly = true,
                isError = departureDateError,
                supportingText = if (departureDateError) {
                    { Text(text = "Выберите дату вылета") }
                } else null,
                trailingIcon = {
                    IconButton(onClick = { showDepartureDatePicker = true }) {
                        Icon(
                            imageVector = Icons.Rounded.DateRange,
                            contentDescription = "Выбрать дату"
                        )
                    }
                },
                modifier = Modifier.weight(1f)
            )
            
            OutlinedTextField(
                value = returnDate?.format(dateFormatter) ?: "",
                onValueChange = { },
                label = { Text("Дата возврата") },
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showReturnDatePicker = true }) {
                        Icon(
                            imageVector = Icons.Rounded.DateRange,
                            contentDescription = "Выбрать дату"
                        )
                    }
                },
                modifier = Modifier.weight(1f)
            )
        }

        // Пассажиры
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = adults,
                onValueChange = { newValue ->
                    if (newValue.isEmpty() || newValue.toIntOrNull() != null) {
                        adults = newValue.take(2)
                    }
                },
                label = { Text("Взрослые") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = passengersError,
                supportingText = if (passengersError) {
                    { Text(text = "Мин. 1 взрослый") }
                } else null,
                modifier = Modifier.weight(1f)
            )
            
            OutlinedTextField(
                value = children,
                onValueChange = { newValue ->
                    if (newValue.isEmpty() || newValue.toIntOrNull() != null) {
                        children = newValue.take(2)
                    }
                },
                label = { Text("Дети") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
            
            OutlinedTextField(
                value = infants,
                onValueChange = { newValue ->
                    if (newValue.isEmpty() || newValue.toIntOrNull() != null) {
                        infants = newValue.take(2)
                    }
                },
                label = { Text("Младенцы") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
        }

        // Кнопка поиска
        Button(
            onClick = {
                // Валидация
                departureCityError = departureCity.isEmpty()
                arrivalCityError = arrivalCity.isEmpty()
                departureDateError = departureDate == null
                passengersError = adults.toIntOrNull() ?: 0 < 1
                
                if (!departureCityError && !arrivalCityError && !departureDateError && !passengersError) {
                    // TODO: Implement search
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Найти билеты")
        }
    }

    // DatePicker для даты вылета
    if (showDepartureDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = System.currentTimeMillis()
        )
        
        DatePickerDialog(
            onDismissRequest = { showDepartureDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        departureDate = LocalDate.ofEpochDay(millis / (24 * 60 * 60 * 1000))
                        departureDateError = false
                    }
                    showDepartureDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDepartureDatePicker = false }) {
                    Text("Отмена")
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                showModeToggle = false
            )
        }
    }

    // DatePicker для даты возврата
    if (showReturnDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = System.currentTimeMillis()
        )
        
        DatePickerDialog(
            onDismissRequest = { showReturnDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        returnDate = LocalDate.ofEpochDay(millis / (24 * 60 * 60 * 1000))
                    }
                    showReturnDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showReturnDatePicker = false }) {
                    Text("Отмена")
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                showModeToggle = false
            )
        }
    }
}