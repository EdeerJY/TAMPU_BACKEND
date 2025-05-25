package com.tampu.app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tampu.app.databinding.ActivityContactoSeguroBinding

class ContactoSeguroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityContactoSeguroBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactoSeguroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Datos simulados (puedes cargarlos desde SharedPreferences más adelante)
        binding.tvNombre.text = "Juan"
        binding.tvTelefono.text = "987654321"
        binding.tvRelacion.text = "Papá"

        // Botón de regreso
        binding.btnRegresar.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.btnConfigurar.setOnClickListener {
            val intent = Intent(this, ConfigurarContactoActivity::class.java)
            startActivity(intent)
        }

    }
}
