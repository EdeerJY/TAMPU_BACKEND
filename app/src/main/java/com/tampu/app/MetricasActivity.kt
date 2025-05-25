package com.tampu.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tampu.app.databinding.ActivityMetricasBinding
import android.content.Intent


class MetricasActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMetricasBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMetricasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Aquí iría la lógica para mostrar métricas en tiempo real o desde sensores
        binding.tvFrecuencia.text = "72 bpm"
        binding.tvRespiracion.text = "Normal"
        binding.tvOxigenacion.text = "96%"
        binding.tvEstres.text = "Calmado"

        binding.btnRegresar.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Opcional: cierra la pantalla actual para evitar volver atrás con el botón de retroceso
        }


    }
}
