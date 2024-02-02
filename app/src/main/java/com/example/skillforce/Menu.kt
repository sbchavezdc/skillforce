package com.example.skillforce

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.skillforce.databinding.ActivityMenuBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

// Referencia a Firestore
private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

class Menu : AppCompatActivity() {



    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMenuBinding

    private lateinit var auth: FirebaseAuth



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        auth=FirebaseAuth.getInstance()



        // Obtener la instancia de FirebaseAuth
        val auth = FirebaseAuth.getInstance()

        // Obtener la identificación única del usuario actual

        val email = auth.currentUser?.email
        val displayName = auth.currentUser?.displayName

        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMenu.toolbar)



        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_menu)



        // Obtén la referencia a tu ImageButton
        val btnImgView: ImageButton = binding.navView.getHeaderView(0).findViewById(R.id.BtnImgView)
        btnImgView.setOnClickListener {
            // Aquí redirige a tu fragment editar perfil
           // findNavController(R.id.nav_host_fragment_content_menu).navigate(R.id.edit_Perfil)

            // Aquí redirige a tu fragmento editar perfil y pasa la URL de la foto
            findNavController(R.id.nav_host_fragment_content_menu).navigate(R.id.edit_Perfil)

            // Obtén la instancia de FirebaseAuth
            val auth = FirebaseAuth.getInstance()

            // Obtén la identificación única del usuario actual
            val uid = auth.currentUser?.uid

            // Llama a la función cargarFotoDesdeFirestore con la UID del usuario
            if (uid != null) {
                cargarFotoDesdeFirestore(uid)
            }

        }

        // Verificar si el usuario está autenticado
        val currentUser: FirebaseUser? = auth.currentUser
        if (currentUser != null) {

            // Obtener referencia al encabezado
            val headerView = navView.getHeaderView(0)

            headerView.findViewById<TextView>(R.id.textUser).text = displayName ?: "Nombre de Usuario"
            headerView.findViewById<TextView>(R.id.textEmail).text = email ?: "correo@example.com"
        }





        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.Education, R.id.nav_slideshow, R.id.nav_support,R.id.Congratulations,R.id.chat,
                R.id.BtnSalir, R.id.Test, R.id.Chat
            ), drawerLayout



        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        val exitItem = binding.navView.menu.findItem(R.id.BtnSalir)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        val googleSignInClient = GoogleSignIn.getClient(this, gso)
        googleSignInClient.signOut()
        // Agrega un OnClickListener al item de salir
        exitItem.setOnMenuItemClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, MainActivity::class.java) // Reemplaza LoginActivity con la clase de tu actividad de inicio de sesión
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_menu)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }



    private fun cargarFotoDesdeFirestore(uid: String) {
        // Obtener la referencia al documento del usuario en la colección "chat"
        val userDocumentRef = firestore.collection("chat").document(uid)

        // Obtener la URL de la foto desde el documento del usuario
        userDocumentRef.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    // Verificar si el campo "photoURL" existe en el documento
                    if (documentSnapshot.contains("photoURL")) {
                        val photoURL = documentSnapshot.getString("photoURL")

                        // Verificar si la URL de la foto no está vacía
                        if (!photoURL.isNullOrEmpty()) {
                            cargarImagen(photoURL)
                        } else {
                            // Si la URL está vacía, cargar la imagen por defecto
                            cargarImagenPorDefecto()
                        }
                    } else {
                        // Si el campo "photoURL" no existe, cargar la imagen por defecto
                        cargarImagenPorDefecto()
                    }
                }
            }
            .addOnFailureListener { exception ->
                // Manejar la falla, puedes agregar un mensaje de log o notificación según sea necesario
            }
    }

    private fun cargarImagen(url: String) {
        val btnImgView: ImageButton = binding.navView.getHeaderView(0).findViewById(R.id.BtnImgView)
        Glide.with(this)
            .load(url)
            .into(btnImgView)
    }

    private fun cargarImagenPorDefecto() {
        val defaultImageResource = R.drawable.grup1
        val btnImgView: ImageButton = binding.navView.getHeaderView(0).findViewById(R.id.BtnImgView)
        btnImgView.setImageResource(defaultImageResource)
    }




}