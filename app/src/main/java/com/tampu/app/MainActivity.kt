package com.tampu.app


import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.tampu.app.databinding.ActivityMainBinding
import android.content.Intent


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val nombreUsuario = "Juan" // reemplazar por nombre real si lo tienes guardado

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvSaludo.text = "¡Hola, $nombreUsuario! Estamos aquí para ayudarte a sentirte mejor. ¿Cómo te sientes hoy?"
        binding.tvEstado.text = "Todo parece tranquilo"

        binding.btnVerMetricas.setOnClickListener {
            val intent = Intent(this, MetricasActivity::class.java)
            startActivity(intent)
        }

        binding.btnContactoSeguro.setOnClickListener {
            Toast.makeText(this, "Contacto seguro", Toast.LENGTH_SHORT).show()
        }

        binding.btnPerfil.setOnClickListener {
            Toast.makeText(this, "Perfil", Toast.LENGTH_SHORT).show()
        }
        binding.btnContactoSeguro.setOnClickListener {
            val intent = Intent(this, ContactoSeguroActivity::class.java)
            startActivity(intent)
        }
        binding.btnPerfil.setOnClickListener {
            val intent = Intent(this, PerfilActivity::class.java)
            startActivity(intent)
        }


    }
}
