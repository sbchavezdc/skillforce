package com.example.skillforce

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Menu_Perfil : Fragment() {

    // Códigos de solicitud para las actividades de la cámara y la galería
    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_PICK_IMAGE = 2

    // Referencia a Firestore
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_menu__perfil, container, false)

        // Obtener una referencia al botón Camera
        val btnCamera: Button = view.findViewById(R.id.BtnCamera)

        // Configurar OnClickListener para el botón Camera
        btnCamera.setOnClickListener {
            // Lanzar la aplicación de la cámara
            abrirCamara()
        }

        // Obtener una referencia al botón Gallery
        val btnGallery: Button = view.findViewById(R.id.BtnGallery)

        // Configurar OnClickListener para el botón Gallery
        btnGallery.setOnClickListener {
            // Lanzar la aplicación de la galería
            abrirGaleria()
        }

        // Obtener la UID del usuario actual
        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid

        // Cargar la foto del usuario desde Firestore
        if (currentUserUid != null) {
            cargarFotoDesdeFirestore(currentUserUid)
        }

        return view
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
        val imageView: ImageView = requireView().findViewById(R.id.accountImageView)
        Glide.with(this)
            .load(url)
            .into(imageView)
    }

    private fun cargarImagenPorDefecto() {
        val defaultImageResource = R.drawable.account_circle
        val imageView: ImageView = requireView().findViewById(R.id.accountImageView)
        imageView.setImageResource(defaultImageResource)
    }




    private fun abrirCamara() {
        // Crear un Intent para iniciar la aplicación de la cámara
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        // Verificar si hay alguna aplicación de cámara disponible para manejar la intención
        if (intent.resolveActivity(requireContext().packageManager) != null) {
            // Iniciar la aplicación de la cámara con un código de solicitud
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
        } else {
            // Manejar el caso en el que no hay una aplicación de cámara disponible
        }
    }

    private fun abrirGaleria() {
        // Crear un Intent para iniciar la aplicación de la galería
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        // Verificar si hay alguna aplicación de galería disponible para manejar la intención
        if (intent.resolveActivity(requireContext().packageManager) != null) {
            // Iniciar la aplicación de la galería con un código de solicitud
            startActivityForResult(intent, REQUEST_PICK_IMAGE)
        } else {
            // Manejar el caso en el que no hay una aplicación de galería disponible
        }
    }

    // Método llamado cuando las actividades de la cámara o la galería devuelven un resultado
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Verificar si el resultado corresponde a la actividad de la cámara
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            // Obtener la imagen capturada desde la cámara
            val imageBitmap = data?.extras?.get("data") as Bitmap

            // Mostrar la imagen capturada en el ImageView
            val imageView: ImageView = requireView().findViewById(R.id.accountImageView)
            imageView.setImageBitmap(imageBitmap)
        }

        // Verificar si el resultado corresponde a la actividad de la galería
        if (requestCode == REQUEST_PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            // Obtener la URI de la imagen seleccionada desde la galería
            val selectedImageUri = data?.data

            // Convertir la URI a un bitmap (puedes usar Glide u otras bibliotecas para cargar la imagen)
            val imageBitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, selectedImageUri)

            // Mostrar la imagen seleccionada en el ImageView
            val imageView: ImageView = requireView().findViewById(R.id.accountImageView)
            imageView.setImageBitmap(imageBitmap)
        }
    }



}