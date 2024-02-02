package com.example.skillforce

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth

class SingUp : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnRegistrar: Button
    private lateinit var Btnsingg: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sing_up)


        //enlaces
        etEmail = findViewById<EditText>(R.id.etEmail)
        etPassword = findViewById<EditText>(R.id.etPassword)
        btnRegistrar = findViewById<Button>(R.id.btnRegistrar)


        Btnsingg = findViewById(R.id.Btnsingg)

        //oyentes
        btnRegistrar.setOnClickListener { Registrar() }

        Btnsingg.setOnClickListener {
            val intent1 = Intent(applicationContext, Login::class.java)
            startActivity(intent1)
        }
    }

    private fun Registrar() {
        var email = etEmail.text.toString()
        var password = etPassword.text.toString()
        if (email.isNotEmpty() && password.isNotEmpty()) {
            FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        val intent1: Intent =
                            Intent(applicationContext, Menu::class.java).apply {
                                putExtra("email", it.result?.user?.email ?: "")
                            }
                        startActivity(intent1)
                    } else
                        verDialogo("Error", "Error de autenticación de usuario")
                }
        } else
            verDialogo("Advertencia", "Debes llenar todos los campos")
    }



    private fun verDialogo(titulo: String, mensaje: String) {
        var builder = AlertDialog.Builder(this)
        builder.setTitle(titulo)
        builder.setMessage(mensaje)
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}