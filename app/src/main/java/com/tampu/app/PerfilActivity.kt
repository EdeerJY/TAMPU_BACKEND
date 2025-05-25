package com.tampu.app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tampu.app.databinding.ActivityPerfilBinding

class PerfilActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPerfilBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Datos simulados (pueden venir de SharedPreferences luego)
        binding.tvNombre.text = "Juan"
        binding.tvEdad.text = "25"
        binding.tvSexo.text = "Hombre"
        binding.tvCarrera.text = "Ing. Sistemas"

        binding.btnRegresar.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.btnConfigurar.setOnClickListener {
            val intent = Intent(this, ConfigurarPerfilActivity::class.java)
            startActivity(intent)
        }

    }
}
