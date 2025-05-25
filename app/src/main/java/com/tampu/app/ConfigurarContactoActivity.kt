package com.tampu.app

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.tampu.app.databinding.ActivityConfigurarContactoBinding

class ConfigurarContactoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityConfigurarContactoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfigurarContactoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnAceptar.setOnClickListener {
            val nombre = binding.etNombre.text.toString()
            val telefono = binding.etTelefono.text.toString()
            val relacion = binding.etRelacion.text.toString()

            if (nombre.isBlank() || telefono.isBlank() || relacion.isBlank()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            } else {
                // Puedes guardar los datos en SharedPreferences aquí
                val intent = Intent(this, ContactoSeguroActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        binding.btnCancelar.setOnClickListener {
            finish() // volver atrás sin guardar
        }
    }
}
