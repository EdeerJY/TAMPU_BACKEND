package com.tampu.app

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.*
import com.tampu.app.ui.theme.TampuTheme
import java.util.UUID

class MainActivity : ComponentActivity(), DataClient.OnDataChangedListener {

    private val ritmo = mutableStateOf(0)
    private val estres = mutableStateOf(0)
    private val peso = mutableStateOf(0.0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TampuTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { padding ->
                    DatosRecibidosUI(
                        modifier = Modifier.padding(padding),
                        ritmo = ritmo.value,
                        estres = estres.value,
                        peso = peso.value
                    )
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d("PHONE_TAMPU", "🟢 Registrando listener...")
        Wearable.getDataClient(this).addListener(this)
        verificarConexionWearOS()
        verificarDataItemsPendientes()
        enviarReadySignal()
    }

    override fun onStop() {
        super.onStop()
        Wearable.getDataClient(this).removeListener(this)
        Log.d("PHONE_TAMPU", "🔴 Listener removido en onStop")
    }

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        Log.d("PHONE_TAMPU", "📥 Entró en onDataChanged con ${dataEvents.count} eventos")

        for (event in dataEvents) {
            val uri = event.dataItem.uri
            val path = uri.path

            Log.d("PHONE_TAMPU", "📦 Evento recibido desde nodo: ${uri.host}")
            Log.d("PHONE_TAMPU", "📦 Path recibido: $path")

            if (event.type == DataEvent.TYPE_CHANGED && path?.startsWith("/datos_simulados") == true) {
                val dataMap = DataMapItem.fromDataItem(event.dataItem).dataMap
                val bpm = dataMap.getInt("bpm", -1)
                val estresValue = dataMap.getInt("estres", -1)
                val pesoValue = dataMap.getDouble("peso", -1.0)

                Log.d(
                    "PHONE_TAMPU",
                    "✅ Datos recibidos: bpm=$bpm, estres=$estresValue, peso=$pesoValue"
                )

                ritmo.value = bpm
                estres.value = estresValue
                peso.value = pesoValue

                runOnUiThread {
                    Toast.makeText(this, "📥 Datos actualizados", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun verificarConexionWearOS() {
        Thread {
            try {
                val nodes = Tasks.await(Wearable.getNodeClient(this@MainActivity).connectedNodes)
                for (node in nodes) {
                    Log.d("PHONE_TAMPU", "📡 Nodo conectado: ${node.displayName} (${node.id})")
                }
                if (nodes.isEmpty()) {
                    Log.w("PHONE_TAMPU", "⚠️ No hay nodos conectados con el reloj")
                }
            } catch (e: Exception) {
                Log.e("PHONE_TAMPU", "❌ Error verificando nodos", e)
            }
        }.start()
    }

    private fun verificarDataItemsPendientes() {
        Wearable.getDataClient(this).dataItems
            .addOnSuccessListener { buffer ->
                Log.d("PHONE_TAMPU", "📂 getDataItems returned ${buffer.count} items")
                for (item in buffer) {
                    Log.d("PHONE_TAMPU", "📄 Item encontrado en path: ${item.uri.path}")
                }
                buffer.release()
            }
            .addOnFailureListener {
                Log.e("PHONE_TAMPU", "❌ Error al acceder a getDataItems", it)
            }
    }

    private fun enviarReadySignal() {
        val dataClient = Wearable.getDataClient(this)

        val uniquePath = "/ready_signal_" + UUID.randomUUID().toString() // ⚠️ cambia el path
        val dataMap = PutDataMapRequest.create(uniquePath).apply {
            dataMap.putLong("timestamp", System.currentTimeMillis())
            dataMap.putString("uuid", UUID.randomUUID().toString())
        }

        val request = dataMap.asPutDataRequest().setUrgent()
        dataClient.putDataItem(request)
            .addOnSuccessListener {
                Log.d("PHONE_TAMPU", "✅ Señal de ready enviada al reloj en $uniquePath")
            }
            .addOnFailureListener {
                Log.e("PHONE_TAMPU", "❌ Error al enviar señal de ready", it)
            }
    }


    @Composable
    fun DatosRecibidosUI(
        modifier: Modifier = Modifier,
        ritmo: Int,
        estres: Int,
        peso: Double
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Bienvenido a Tampu", fontSize = 22.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Ritmo cardíaco: $ritmo bpm")
            Text("Nivel de estrés: $estres%")
            Text("Variación de peso: ${"%.1f".format(peso)} kg")
        }
    }
}