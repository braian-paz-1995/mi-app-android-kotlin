package com.example.app.menu


import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkHourTracker(context: Context, navController: NavHostController) {
    val prefs = remember { context.getSharedPreferences("WorkPrefs", Context.MODE_PRIVATE) }
    var inputNormal by remember { mutableStateOf("") }
    var inputFeriado by remember { mutableStateOf("") }

    val datePickerState = rememberDatePickerState()
    val selectedDateMillis = datePickerState.selectedDateMillis ?: System.currentTimeMillis()
    val dayFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val selectedDateString = dayFormat.format(Date(selectedDateMillis))

    var hoursNormalDay by remember(selectedDateMillis) { mutableStateOf(prefs.getFloat("normal_$selectedDateString", 0f)) }
    var hoursFeriadoDay by remember(selectedDateMillis) { mutableStateOf(prefs.getFloat("feriado_$selectedDateString", 0f)) }
    var totalNormal by remember { mutableStateOf(prefs.getFloat("total_n", 0f)) }
    var totalFeriado by remember { mutableStateOf(prefs.getFloat("total_f", 0f)) }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        item {
            WorkHeaderSection()
            Spacer(modifier = Modifier.height(16.dp))
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Cargar para: $selectedDateString", fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = inputNormal,
                        onValueChange = { inputNormal = it },
                        label = { Text("Horas Normales") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = inputFeriado,
                        onValueChange = { inputFeriado = it },
                        label = { Text("Horas Feriado") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Button(onClick = {
                        val n = inputNormal.toFloatOrNull() ?: 0f
                        val f = inputFeriado.toFloatOrNull() ?: 0f
                        hoursNormalDay += n
                        hoursFeriadoDay += f
                        totalNormal += n
                        totalFeriado += f
                        prefs.edit()
                            .putFloat("normal_$selectedDateString", hoursNormalDay)
                            .putFloat("feriado_$selectedDateString", hoursFeriadoDay)
                            .putFloat("total_n", totalNormal)
                            .putFloat("total_f", totalFeriado)
                            .apply()
                        inputNormal = ""; inputFeriado = ""
                    }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                        Text("Guardar Horas")
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                WorkResultBox("Total Normal", totalNormal)
                WorkResultBox("Total Feriado", totalFeriado)
            }
            Spacer(modifier = Modifier.height(20.dp))
            OutlinedButton(onClick = { navController.popBackStack() }, modifier = Modifier.fillMaxWidth()) {
                Text("Volver al Menú")
            }
            Spacer(modifier = Modifier.height(20.dp))
            DatePicker(state = datePickerState, title = null, headline = null, showModeToggle = false, modifier = Modifier.scale(0.8f))
        }
    }
}

@Composable
fun WorkHeaderSection() {
    val sdf = SimpleDateFormat("EEEE, dd/MM", Locale.getDefault())
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text("Laboral", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text(sdf.format(Date()), color = MaterialTheme.colorScheme.secondary)
    }
}

@Composable
fun WorkResultBox(label: String, valNum: Float) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontSize = 12.sp)
        Text("%.1f".format(valNum), fontSize = 24.sp, fontWeight = FontWeight.Bold)
    }
}