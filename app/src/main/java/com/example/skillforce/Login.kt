package com.example.skillforce

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnIngresar: Button
    private lateinit var BtnforgotPass: TextView
    private lateinit var BtnLogin: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btnIngresar = findViewById<Button>(R.id.btnIngresar)
        etEmail = findViewById<EditText>(R.id.etEmail)
        etPassword = findViewById<EditText>(R.id.etPassword)
        BtnforgotPass = findViewById(R.id.BtnforgotPass)

        BtnLogin = findViewById(R.id.BtnLogin)

        btnIngresar.setOnClickListener { Ingresar() }

        BtnforgotPass.setOnClickListener {
            val intent1 = Intent(applicationContext, RecoverPassword::class.java)
            startActivity(intent1)
        }

        BtnLogin.setOnClickListener {
            val intent1 = Intent(applicationContext, SingUp::class.java)
            startActivity(intent1)
        }

    }


    private fun Ingresar(){
        var email = etEmail.text.toString()
        var password = etPassword.text.toString()
        if(email.isNotEmpty() && password.isNotEmpty()){
            FirebaseAuth.getInstance()
                .signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if(it.isSuccessful){
                        val intent1: Intent = Intent(applicationContext,Menu::class.java).apply{
                            putExtra("email", it.result?.user?.email ?: "")
                        }
                        startActivity(intent1)
                    }
                    else
                        verDialogo("Error", "Error de autenticación de usuario")
                }
        }
        else
            verDialogo("Advertencia", "Debes llenar todos los campos")
    }

    private fun verDialogo(titulo:String, mensaje:String){
        var builder = AlertDialog.Builder(this)
        builder.setTitle(titulo)
        builder.setMessage(mensaje)
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}