package com.tampu.app

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.android.gms.wearable.*

class MainActivity : ComponentActivity(), DataClient.OnDataChangedListener {
    private var ritmo by mutableStateOf(0)
    private var estres by mutableStateOf(0)
    private var peso by mutableStateOf(0.0)
    private var estadoConexion by mutableStateOf("Esperando datos...")
    private val dataClient by lazy { Wearable.getDataClient(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        MetricDisplay("❤️ Ritmo cardíaco", "$ritmo bpm")
                        Spacer(modifier = Modifier.height(16.dp))
                        MetricDisplay("😰 Nivel de estrés", "$estres%")
                        Spacer(modifier = Modifier.height(16.dp))
                        MetricDisplay("⚖️ Peso estimado", "%.1f kg".format(peso))
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(estadoConexion, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }

    @Composable
    fun MetricDisplay(label: String, value: String) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, style = MaterialTheme.typography.bodyMedium)
            Text(value, style = MaterialTheme.typography.headlineMedium)
        }
    }

    override fun onStart() {
        super.onStart()
        dataClient.addListener(this)
        checkConnection()
    }

    private fun checkConnection() {
        Wearable.getNodeClient(this).connectedNodes.addOnSuccessListener { nodes ->
            estadoConexion = if (nodes.isNotEmpty()) {
                "Conectado a: ${nodes[0].displayName}"
            } else {
                "No hay dispositivos conectados"
            }
        }
    }

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        try {
            for (i in 0 until dataEvents.count) {
                val event = dataEvents.get(i)
                if (event.type == DataEvent.TYPE_CHANGED && event.dataItem.uri.path == "/metrics") {
                    val dataMap = DataMapItem.fromDataItem(event.dataItem).dataMap
                    ritmo = dataMap.getInt("ritmo")
                    estres = dataMap.getInt("estres")
                    peso = dataMap.getDouble("peso")
                    estadoConexion = "Datos actualizados: ${System.currentTimeMillis() % 10000}"
                    Log.d("TAMPU_PHONE", "Datos recibidos - Ritmo: $ritmo, Estrés: $estres, Peso: $peso")
                }
            }
        } catch (e: Exception) {
            Log.e("TAMPU_PHONE", "Error procesando datos", e)
        } finally {
            dataEvents.close()
        }
    }

    override fun onStop() {
        dataClient.removeListener(this)
        super.onStop()
    }
}