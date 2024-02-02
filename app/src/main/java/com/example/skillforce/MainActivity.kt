package com.example.skillforce

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

import com.google.firebase.FirebaseApp

class MainActivity : AppCompatActivity() {



    private lateinit var BtnLogin: Button

    private lateinit var BtnSingUp: TextView
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        FirebaseApp.initializeApp(this)

        auth = FirebaseAuth.getInstance()

        BtnLogin = findViewById(R.id.BtnLogin)
        BtnSingUp = findViewById(R.id.BtnSingUp)


        BtnLogin.setOnClickListener {
            val intent1 = Intent(applicationContext, Login::class.java)
            startActivity(intent1)
        }

        BtnSingUp.setOnClickListener {
            val intent1 = Intent(applicationContext, SingUp::class.java)
            startActivity(intent1)
        }


        // Configuración de Google SignIn
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        // Crear el cliente de Google SignIn con la configuración
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Configurar el botón de inicio de sesión con Google
        findViewById<Button>(R.id.BtnGoogle).setOnClickListener {
            signInGoogle()
        }

    }

    // Método para iniciar sesión con Google
    private fun signInGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    // Definir el launcher para obtener el resultado de la actividad de inicio de sesión
    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleResults(task)
        }
    }

    // Manejar los resultados de la autenticación de Google
    private fun handleResults(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful) {
            // Autenticación con éxito, obtener la cuenta de Google
            val account: GoogleSignInAccount? = task.result
            if (account != null) {
                updateUI(account)
            }
        } else {
            // Manejar errores de autenticación
            Toast.makeText(this, "Error: ${task.exception.toString()}", Toast.LENGTH_SHORT).show()
        }
    }

    // Actualizar la interfaz de usuario después de una autenticación exitosa
    private fun updateUI(account: GoogleSignInAccount) {
        // Crear credencial de Firebase con el token de ID de Google
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)

        // Obtener datos de la cuenta de Google
        val email = account.email
        val name = account.displayName

        // Iniciar sesión en Firebase con la credencial
        auth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                // Inicio de sesión exitoso, redirigir a la actividad principal
                val intent = Intent(this, Menu::class.java)
                intent.putExtra("email", account.email)
                intent.putExtra("name", account.displayName)
                startActivity(intent)
            } else {
                // Manejar errores de inicio de sesión en Firebase
                Toast.makeText(this, "Error: ${it.exception?.toString()}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}