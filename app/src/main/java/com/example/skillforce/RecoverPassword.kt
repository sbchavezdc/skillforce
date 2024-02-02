package com.example.skillforce

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth

class RecoverPassword : AppCompatActivity() {

    
        // Declaración de las vistas, sin inicializarlas aquí
        lateinit var btnSendemail: Button
        lateinit var etEmail: EditText

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_recover_password)


            // Inicialización de las vistas después de setContentView
            btnSendemail = findViewById(R.id.btnSendemail)
            etEmail = findViewById(R.id.etEmail)


            // Configura el listener para el botón
        btnSendemail.setOnClickListener {
            // Obtiene el correo electrónico del input text
            val emailAddress = etEmail.text.toString()

            // Verifica si el correo electrónico no está vacío
            if (emailAddress.isNotEmpty()) {
                // Envía el correo de restablecimiento de contraseña
                FirebaseAuth.getInstance().sendPasswordResetEmail(emailAddress)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d(TAG, "Email sent successfully.")
                            // Puedes agregar aquí cualquier lógica adicional después del envío exitoso
                        } else {
                            Log.e(TAG, "Failed to send email: ${task.exception?.message}")
                            // Maneja el error de envío de correo aquí
                        }
                    }
            } else {
                // Muestra un mensaje o realiza alguna acción si el campo de correo está vacío
                // Puedes agregar lógica adicional aquí según tus necesidades
                Log.e(TAG, "Email address is empty.")
            }
        }
        btnSendemail.setOnClickListener {
            val intent1 = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent1)
        }

    }
}