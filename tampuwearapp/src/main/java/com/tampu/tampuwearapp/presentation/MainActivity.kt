package com.tampu.tampuwearapp.presentation

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.*
import com.google.android.gms.wearable.*
import com.tampu.tampuwearapp.presentation.theme.TampuTheme
import java.util.*

class MainActivity : ComponentActivity() {
    private lateinit var dataClient: DataClient
    private var lastSentData by mutableStateOf("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataClient = Wearable.getDataClient(this)

        setContent {
            TampuTheme {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = { sendForcedMetrics() },
                        modifier = Modifier.size(ButtonDefaults.LargeButtonSize)
                    ) {
                        Text("ENVIAR DATOS")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Último envío:")
                    Text(lastSentData, style = MaterialTheme.typography.caption1)
                }
            }
        }
    }

    private fun sendForcedMetrics() {
        val random = Random()
        val dataMap = PutDataMapRequest.create("/metrics").apply {
            dataMap.apply {
                putInt("ritmo", 80 + random.nextInt(20)) // 80-100 bpm
                putInt("estres", 30 + random.nextInt(40)) // 30-70%
                putDouble("peso", 60.0 + random.nextInt(15)) // 60-75 kg
                putLong("timestamp", System.currentTimeMillis())
            }
        }

        dataClient.putDataItem(dataMap.asPutDataRequest()).apply {
            addOnSuccessListener {
                lastSentData = "Ritmo: ${dataMap.dataMap.getInt("ritmo")}\n" +
                        "Estrés: ${dataMap.dataMap.getInt("estres")}%\n" +
                        "Peso: ${dataMap.dataMap.getDouble("peso")}kg"
                Log.d("WATCH_TAMPU", "Datos forzados enviados: $lastSentData")
            }
            addOnFailureListener { e ->
                lastSentData = "Error al enviar"
                Log.e("WATCH_TAMPU", "Error en envío", e)
            }
        }
    }
}