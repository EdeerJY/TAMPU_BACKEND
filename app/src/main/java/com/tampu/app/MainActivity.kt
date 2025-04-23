package com.tampu.app

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
import com.google.android.gms.wearable.*
import com.tampu.app.ui.theme.TampuTheme

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

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        Log.d("PHONE_TAMPU", "📥 Datos recibidos en onDataChanged")
        for (event in dataEvents) {
            if (event.type == DataEvent.TYPE_CHANGED &&
                event.dataItem.uri.path == "/datos_simulados"
            ) {
                val dataMap = DataMapItem.fromDataItem(event.dataItem).dataMap
                ritmo.value = dataMap.getInt("bpm")
                estres.value = dataMap.getInt("estres")
                peso.value = dataMap.getDouble("peso")

                Log.d("PHONE_TAMPU", "✅ bpm=${ritmo.value}, estres=${estres.value}, peso=${peso.value}")

                runOnUiThread {
                    Toast.makeText(this, "Datos recibidos", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Wearable.getDataClient(this).addListener(this)
        Log.d("PHONE_TAMPU", "🟢 Listener agregado en onStart")
    }

    override fun onStop() {
        super.onStop()
        Wearable.getDataClient(this).removeListener(this)
        Log.d("PHONE_TAMPU", "🔴 Listener removido en onStop")
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
