package com.tampu.tampuwearapp.presentation

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import com.google.android.gms.wearable.*
import com.tampu.tampuwearapp.presentation.theme.TampuTheme
import java.util.*

class MainActivity : ComponentActivity(), DataClient.OnDataChangedListener {

    private val handler = Handler(Looper.getMainLooper())
    private var runnable: Runnable? = null
    private var envioActivo = false
    private var datosInicialesEnviados = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { WearApp() }

        Wearable.getDataClient(this).addListener(this)
        verificarDataItemsPendientes()
    }

    override fun onDestroy() {
        super.onDestroy()
        Wearable.getDataClient(this).removeListener(this)
        runnable?.let { handler.removeCallbacks(it) }
    }

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        for (event in dataEvents) {
            val path = event.dataItem.uri.path
            Log.d("WEAR_TAMPU", "📦 onDataChanged() → $path")

            if (!envioActivo && path?.startsWith("/ready_signal") == true) {
                Log.d("WEAR_TAMPU", "🟢 Señal recibida vía onDataChanged: $path")
                activarEnvio()
            }
        }
    }

    private fun verificarDataItemsPendientes() {
        Wearable.getDataClient(this).dataItems
            .addOnSuccessListener { buffer ->
                Log.d("WEAR_TAMPU", "🔍 Revisando ${buffer.count} DataItems en cache...")

                for (item in buffer) {
                    val path = item.uri.path
                    Log.d("WEAR_TAMPU", "📄 DataItem encontrado: $path")

                    if (!envioActivo && path?.startsWith("/ready_signal") == true) {
                        Log.d("WEAR_TAMPU", "🟢 Señal detectada en getDataItems: $path")
                        activarEnvio()
                    }

                    if (path?.startsWith("/datos_simulados") == true) {
                        Log.d("WEAR_TAMPU", "📄 Dato simulado pendiente: $path")
                    }
                }

                buffer.release()
            }
            .addOnFailureListener {
                Log.e("WEAR_TAMPU", "❌ Error leyendo DataItems", it)
            }
    }

    private fun activarEnvio() {
        envioActivo = true
        reenviarDatosIniciales()
        iniciarEnvioDatos()
    }

    private fun reenviarDatosIniciales() {
        if (datosInicialesEnviados) return

        val bpm = 75
        val estres = 30
        val peso = 70.0

        val dataMap = PutDataMapRequest.create("/datos_simulados").apply {
            dataMap.putInt("bpm", bpm)
            dataMap.putInt("estres", estres)
            dataMap.putDouble("peso", peso)
            dataMap.putLong("timestamp", System.currentTimeMillis())
            dataMap.putString("uuid", UUID.randomUUID().toString())
        }

        Wearable.getDataClient(this)
            .putDataItem(dataMap.asPutDataRequest().setUrgent())
            .addOnSuccessListener {
                Log.d("WEAR_TAMPU", "✅ Datos iniciales reenviados")
                datosInicialesEnviados = true
            }
            .addOnFailureListener {
                Log.e("WEAR_TAMPU", "❌ Error al reenviar datos", it)
            }
    }

    private fun iniciarEnvioDatos() {
        var bpm = 75
        var estres = 30
        var peso = 70.0

        runnable = object : Runnable {
            override fun run() {
                val dataMap = PutDataMapRequest.create("/datos_simulados").apply {
                    dataMap.putInt("bpm", bpm)
                    dataMap.putInt("estres", estres)
                    dataMap.putDouble("peso", peso)
                    dataMap.putLong("timestamp", System.currentTimeMillis())
                    dataMap.putString("uuid", UUID.randomUUID().toString())
                }

                Wearable.getDataClient(this@MainActivity)
                    .putDataItem(dataMap.asPutDataRequest().setUrgent())
                    .addOnSuccessListener {
                        Log.d("WEAR_TAMPU", "✅ Datos enviados")
                    }
                    .addOnFailureListener {
                        Log.e("WEAR_TAMPU", "❌ Error al enviar datos", it)
                    }

                bpm = (bpm + (-3..3).random()).coerceIn(60, 130)
                estres = (estres + (-5..5).random()).coerceIn(0, 100)
                peso = (peso + listOf(-0.1, 0.0, 0.1).random()).coerceIn(50.0, 100.0)

                handler.postDelayed(this, 5000)
            }
        }

        handler.post(runnable!!)
    }
}

@androidx.compose.runtime.Composable
fun WearApp() {
    TampuTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(16.dp)
            ) {
                TimeText()
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Tampu Wear",
                    color = Color.White,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Esperando señal del teléfono...",
                    color = Color.LightGray,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
