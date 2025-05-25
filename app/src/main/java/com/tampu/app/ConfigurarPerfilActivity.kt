package com.tampu.app

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.tampu.app.databinding.ActivityConfigurarPerfilBinding

class ConfigurarPerfilActivity : AppCompatActivity() {

    private lateinit var binding: ActivityConfigurarPerfilBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfigurarPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnAceptar.setOnClickListener {
            val nombre = binding.etNombre.text.toString()
            val edad = binding.etEdad.text.toString()
            val sexo = binding.etSexo.text.toString()
            val carrera = binding.etCarrera.text.toString()

            if (nombre.isBlank() || edad.isBlank() || sexo.isBlank() || carrera.isBlank()) {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
            } else {
                // Aquí puedes guardar los datos con SharedPreferences más adelante
                startActivity(Intent(this, PerfilActivity::class.java))
                finish()
            }
        }

        binding.btnCancelar.setOnClickListener {
            finish()
        }
    }
}
