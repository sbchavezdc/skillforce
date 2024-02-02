package com.example.skillforce

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class Chat : Fragment() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var messageEditText: EditText
    private lateinit var responseTextView: TextView
    private lateinit var chatDocumentRef: DocumentReference

    // Obtener la instancia de FirebaseAuth
    val auth = FirebaseAuth.getInstance()

    // Obtener la identificación única del usuario actual
    val userId = auth.currentUser?.uid
    val email = auth.currentUser?.email
    val displayName = auth.currentUser?.displayName




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chat, container, false)

        // Inicializar Firestore
        firestore = FirebaseFirestore.getInstance()

        // Obtener la referencia del EditText
        messageEditText = view.findViewById(R.id.messageEditText)
        responseTextView = view.findViewById(R.id.responseTextView)

        // Obtener el estilo de aprendizaje de los argumentos
        val estiloAprendizaje = arguments?.getString("LEARNING_STYLE")

        // Mostrar el estilo de aprendizaje en el EditText
       // messageEditText.setText("$estiloAprendizaje")

        // Obtener la referencia de la ProgressBar
        val loadingProgressBar = view.findViewById<ProgressBar>(R.id.loadingProgressBar)
        // Supongamos que tienes una referencia a tu base de datos de Firebase



        // Verificar si el usuario está autenticado
        if (userId != null) {
            // Crear una referencia a la colección específica del usuario
            val userChatCollectionRef = firestore.collection("chat").document(userId)

            // Ejemplo de referencia a un documento específico (puedes ajustarlo según tus necesidades)
            chatDocumentRef = userChatCollectionRef
        } else {
            // Manejar el caso en el que el usuario no está autenticado
            // Puedes redirigir al usuario a la pantalla de inicio de sesión, por ejemplo
            Log.e("ChatFragment", "Usuario no autenticado")
        }

        // Configurar el botón de enviar
        view.findViewById<MaterialButton>(R.id.sendButton).setOnClickListener {

            // Mostrar la ProgressBar antes de enviar la consulta a Firebase
            loadingProgressBar.visibility = View.VISIBLE

            val mensaje = messageEditText.text.toString()
            enviarMensaje(mensaje)
            // Agregar el mensaje al TextView (puedes personalizar el formato)
            responseTextView.append("Tú: $mensaje\n")
            // Limpiar el EditText después de enviar el mensaje
            messageEditText.text.clear()
        }

        // Agregar un listener en tiempo real para obtener actualizaciones
        chatDocumentRef.addSnapshotListener { documentSnapshot, e ->
            if (e != null) {
                Log.e("ChatFragment", "Error al obtener la respuesta en tiempo real: $e")
                return@addSnapshotListener
            }

            if (documentSnapshot != null && documentSnapshot.exists()) {
                // Verificar si el campo 'status.state' existe en el documento
                val statusState = documentSnapshot.getString("status.state")

                if (statusState != null) {
                    activity?.runOnUiThread {
                        // Mostrar el estado en responseTextView
                        val respuesta = documentSnapshot.getString("response")
                        responseTextView.text = respuesta

                        // Mostrar/Ocultar la ProgressBar según sea necesario
                        if (statusState == "COMPLETED") {
                            loadingProgressBar.visibility = View.GONE
                        } else {
                            loadingProgressBar.visibility = View.VISIBLE
                        }
                    }
                } else {
                    // Si 'status.state' no existe, puedes manejarlo según tus necesidades.
                    // Por ejemplo, podrías decidir no mostrar el ProgressBar en este caso.
                    activity?.runOnUiThread {
                        loadingProgressBar.visibility = View.GONE
                    }
                }
            } else {
                // Si el documento no existe, puedes manejarlo según tus necesidades.
                // Por ejemplo, podrías decidir no mostrar el ProgressBar en este caso.
                activity?.runOnUiThread {
                    loadingProgressBar.visibility = View.GONE
                }
            }
            // Puedes agregar más lógica o manejo de errores aquí si es necesario
        }


        return view
    }


    private fun enviarMensaje(mensaje: String) {

        // Obtener el estilo de aprendizaje de los argumentos
        val estiloAprendizaje = arguments?.getString("LEARNING_STYLE")

        // Concatenar el mensaje con el estilo de aprendizaje
        val mensajeConEstilo = "Mi estilo de aprendizaje es :$estiloAprendizaje " +
                "Estoy buscando ayuda con el siguiente problema: $mensaje " +
                "Me sería de gran ayuda si pudieras proporcionarme una explicación detallada" +
                " o un enfoque que se alinee con mi estilo de aprendizaje.   " +
                "Gracias de antemano por tu ayuda."



        // Verificar si el campo 'prompt' ya existe en el documento
        chatDocumentRef.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    // El documento ya existe, verificar si 'prompt' está presente
                    if (!documentSnapshot.contains("prompt")) {
                        // El campo 'prompt' no existe, agregarlo al documento
                        agregarPromptAlDocumento(mensajeConEstilo)
                    } else {
                        // El campo 'prompt' ya existe, simplemente actualizar el mensaje
                        actualizarPromptEnDocumento(mensajeConEstilo)
                    }
                    // Después de agregar o actualizar el prompt, puedes borrar el campo 'status'
                    borrarStatusEnDocumento()
                    borrarResponseEnDocumento()
                }

            }
            .addOnFailureListener {
                // Manejar el error al obtener el documento
            }
    }

    private fun borrarStatusEnDocumento() {
        // Actualizar el campo 'status' a null para borrarlo
        chatDocumentRef.update("status","")
            .addOnSuccessListener {
                // Éxito al borrar el campo 'status'
            }
            .addOnFailureListener {
                // Manejar el error
            }
    }

    private fun borrarResponseEnDocumento() {
        // Actualizar el campo 'status' a null para borrarlo
        chatDocumentRef.update("response","")
            .addOnSuccessListener {
                // Éxito al borrar el campo 'status'
            }
            .addOnFailureListener {
                // Manejar el error
            }
    }

    private fun agregarPromptAlDocumento(mensaje: String) {
        // Crear un nuevo mapa con el campo 'prompt'
        val data = hashMapOf(
            "email" to email,
            "displayName" to displayName,
            "prompt" to mensaje,

        )

        // Agregar el nuevo campo 'prompt' al documento
        chatDocumentRef.set(data, SetOptions.merge())
            .addOnSuccessListener {
                // Éxito al agregar el campo 'prompt'
            }
            .addOnFailureListener {
                // Manejar el error
            }
    }

    private fun actualizarPromptEnDocumento(mensaje: String) {
        // Actualizar solo el campo 'prompt' en el documento existente
        chatDocumentRef.update("prompt", mensaje)
            .addOnSuccessListener {
                // Éxito al actualizar el campo 'prompt'
            }
            .addOnFailureListener {
                // Manejar el error
            }
    }


}
