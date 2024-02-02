package com.example.skillforce

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.util.Calendar


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

// Referencia a Firestore
private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()


class Edit_Perfil : Fragment() {

    private var estaRegistradoConGoogle: Boolean = false
    private lateinit var auth: FirebaseAuth

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }


    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit__perfil, container, false)

        // Obtener la UID del usuario actual
        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid

        // Cargar la foto del usuario desde Firestore
        if (currentUserUid != null) {
            cargarFotoDesdeFirestore(currentUserUid)
        }

        val btnSave = view.findViewById<Button>(R.id.btnSave)
        btnSave.setOnClickListener {
            updateData(view)
            saveDataToFirestore()
            updateOrCreateUserDataInFirestore()
        }

        // Obtener la instancia de FirebaseAuth
        this.auth = FirebaseAuth.getInstance()

        // Verificar si el usuario está autenticado y registrado con google
        if (auth.currentUser != null) {
            // El usuario está autenticado, puedes obtener información
            val usuarioActual = auth.currentUser

            // Obtener las vistas de EditText y Button en tu diseño
            val edtUsername = view.findViewById<EditText>(R.id.edtUsername)
            val edtEmail = view.findViewById<EditText>(R.id.edtEmail)
            val edtPassword = view.findViewById<EditText>(R.id.edtPassword)




            // Establecer los valores en las vistas
            edtUsername.setText(usuarioActual?.displayName)
            edtEmail.setText(usuarioActual?.email)

            // Verificar si el usuario se registró con Google
            estaRegistradoConGoogle = usuarioActual?.providerData?.any { it.providerId == "google.com" } ?: false

            // Deshabilitar el campo de contraseña si el usuario se registró con Google
            edtPassword.isEnabled = !estaRegistradoConGoogle

            // Deshabilitar el botón de actualización de contraseña si el usuario se registró con Google
            edtPassword.isEnabled = !estaRegistradoConGoogle
        }


        val btnUpdateImg = view.findViewById<ImageButton>(R.id.BtnUpdateImg)
        // Asignar el clic del botón para la redirección
        btnUpdateImg.setOnClickListener {
            // Navegar a la acción BtnUpdateImg
            findNavController().navigate(R.id.action_edit_Perfil_to_menu_Perfil)
        }

        // Asignar el clic del EditText para mostrar el DatePickerDialog
        val edtCalendario = view.findViewById<TextInputEditText>(R.id.edtCalendario)
        edtCalendario.setOnClickListener {
            showDatePickerDialog(view)
        }

        return view
    }

    private fun updateData(view: View) {
        // Obtener las referencias a los elementos de interfaz de usuario
        val edtUsername = view.findViewById<EditText>(R.id.edtUsername)
        val edtEmail = view.findViewById<EditText>(R.id.edtEmail)
        val edtPassword = view.findViewById<EditText>(R.id.edtPassword)
        val edtCalendario = view.findViewById<TextInputEditText>(R.id.edtCalendario)

        // Obtener los valores actuales de los elementos
        val newDisplayName = edtUsername.text.toString()
        val newEmail = edtEmail.text.toString()
        val newPassword = edtPassword.text.toString()
        val newBirthday = edtCalendario.text.toString()

        // Obtener la instancia actual del usuario
        val currentUser = auth.currentUser

        // Actualizar el display name y el email
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(newDisplayName)
            .build()

        currentUser?.updateProfile(profileUpdates)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Actualización exitosa del display name
                }
            }

        currentUser?.updateEmail(newEmail)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Actualización exitosa del email
                }
            }

        // Actualizar la contraseña solo si se proporciona una nueva contraseña
        if (newPassword.isNotEmpty()) {
            currentUser?.updatePassword(newPassword)
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Actualización exitosa de la contraseña
                    }
                }
        }
    }

    fun showDatePickerDialog(view: View) {
        val calendar: Calendar = Calendar.getInstance()
        val year: Int = calendar.get(Calendar.YEAR)
        val month: Int = calendar.get(Calendar.MONTH)
        val day: Int = calendar.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(requireContext(),
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                val selectedDate = "$dayOfMonth/${month + 1}/$year"
                val editText: TextInputEditText = view.findViewById(R.id.edtCalendario)
                editText.setText(selectedDate)
            }, year, month, day
        )
        datePickerDialog.show()
    }


    private fun saveDataToFirestore() {
        // Obtener las referencias a los elementos de interfaz de usuario
        val edtUsername = view?.findViewById<EditText>(R.id.edtUsername)
        val edtEmail = view?.findViewById<EditText>(R.id.edtEmail)
        val edtCalendario = view?.findViewById<TextInputEditText>(R.id.edtCalendario)

        // Obtener los valores actuales de los elementos
        val newDisplayName = edtUsername?.text.toString()
        val newEmail = edtEmail?.text.toString()
        val newBirthday = edtCalendario?.text.toString()

        // Verificar si el usuario está autenticado
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // Obtener la instancia de Firestore
            val db = FirebaseFirestore.getInstance()

            // Obtener la referencia a la colección "chat" usando el UID del usuario actual
            val userChatRef = db.collection("chat").document(currentUser.uid)

            // Crear un mapa con los datos que deseas actualizar en Firestore
            val data = hashMapOf(
                "displayName" to newDisplayName,
                "email" to newEmail,
                "birthday" to newBirthday
            )

            // Actualizar los datos en Firestore
            userChatRef.set(data)
                .addOnSuccessListener {
                    // Éxito al guardar los datos en Firestore
                }
                .addOnFailureListener { e ->
                    // Error al guardar los datos en Firestore
                    // Puedes agregar un mensaje de log o notificación según sea necesario
                }
        }
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
        val imageView: ImageView = requireView().findViewById(R.id.BtnUpdateImg)
        Glide.with(this)
            .load(url)
            .into(imageView)
    }

    private fun cargarImagenPorDefecto() {
        val defaultImageResource = R.drawable.grup1
        val imageView: ImageView = requireView().findViewById(R.id.BtnUpdateImg)
        imageView.setImageResource(defaultImageResource)
    }



    private fun updateOrCreateUserDataInFirestore() {
        // Obtener las referencias a los elementos de interfaz de usuario
        val edtUsername = view?.findViewById<EditText>(R.id.edtUsername)
        val edtEmail = view?.findViewById<EditText>(R.id.edtEmail)
        val edtCalendario = view?.findViewById<TextInputEditText>(R.id.edtCalendario)

        // Obtener los valores actuales de los elementos
        val newDisplayName = edtUsername?.text.toString()
        val newEmail = edtEmail?.text.toString()
        val newBirthday = edtCalendario?.text.toString()

        // Verificar si el usuario está autenticado
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // Obtener la instancia de Firestore
            val db = FirebaseFirestore.getInstance()

            // Obtener la referencia a la colección "chat" usando el UID del usuario actual
            val userRef = db.collection("chat").document(currentUser.uid)

            // Crear un mapa para almacenar los datos a actualizar o crear
            val userData = hashMapOf(
                "email" to newEmail,
                "displayName" to newDisplayName,
                "birthday" to newBirthday
            )

            // Actualizar los datos en Firestore
            userRef.set(userData, SetOptions.merge())
                .addOnSuccessListener {
                    // Éxito al guardar o actualizar los datos en Firestore
                    Log.d("Firestore", "Datos guardados/actualizados exitosamente")
                    findNavController().navigate(R.id.action_edit_Perfil_to_menu2)

                }
                .addOnFailureListener { e ->
                    // Error al guardar o actualizar los datos en Firestore

                    Log.e("Firestore", "Error al guardar/actualizar datos: ${e.message}")

                }
        }
    }



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Edit_Perfil.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Edit_Perfil().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}