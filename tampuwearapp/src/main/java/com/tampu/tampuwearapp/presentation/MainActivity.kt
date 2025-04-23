package com.tampu.tampuwearapp.presentation

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.tooling.preview.devices.WearDevices
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import com.tampu.tampuwearapp.presentation.theme.TampuTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            WearApp()
        }

        // Simulación de parámetros y envío cada 5 segundos
        val handler = Handler(Looper.getMainLooper())
        var bpm = 75
        var estres = 30
        var peso = 70.0

        val enviarRunnable = object : Runnable {
            override fun run() {
                enviarDatosSimulados(bpm, estres, peso)

                bpm += (-3..3).random()
                estres += (-5..5).random()
                peso += listOf(-0.1, 0.0, 0.1).random()

                handler.postDelayed(this, 5000)
            }
        }

        handler.post(enviarRunnable)
    }

    private fun enviarDatosSimulados(bpm: Int, estres: Int, peso: Double) {
        val dataClient = Wearable.getDataClient(this)
        val dataMap = PutDataMapRequest.create("/datos_simulados").apply {
            dataMap.putInt("bpm", bpm)
            dataMap.putInt("estres", estres)
            dataMap.putDouble("peso", peso)
            dataMap.putLong("timestamp", System.currentTimeMillis())
            dataMap.putString("uuid", java.util.UUID.randomUUID().toString())
        }

        val request = dataMap.asPutDataRequest().setUrgent()
        Log.d("WEAR_TAMPU", "📤 Enviando datos: bpm=$bpm, estrés=$estres, peso=$peso")
        dataClient.putDataItem(request)
    }
}

@Composable
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
                    text = "Enviando datos simulados...",
                    color = Color.LightGray,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Composable
fun PreviewWearApp() {
    WearApp()
}
