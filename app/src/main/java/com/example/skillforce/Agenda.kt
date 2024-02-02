package com.example.skillforce

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import android.widget.TextView
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class Agenda : Fragment() {
    private lateinit var firestore: FirebaseFirestore
    private lateinit var TaskRef: DocumentReference


    private var param1: String? = null
    private var param2: String? = null



    private lateinit var selectedDateTextView: TextView

    private var selectedDate: Calendar = Calendar.getInstance()
    private var taskText: String? = null
    private lateinit var taskEditText: TextInputEditText

    private lateinit var navController: NavController

    val auth = FirebaseAuth.getInstance()
    // Obtener la identificación única del usuario actual
    val userId = auth.currentUser?.uid

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
        val view = inflater.inflate(R.layout.fragment_agenda, container, false)

        // Inicializar Firestore
        firestore = FirebaseFirestore.getInstance()

        val selectDateButton: Button = view.findViewById(R.id.btnSelectDate)
        taskEditText = view.findViewById(R.id.taskEditText)
        val btnSave: Button = view.findViewById(R.id.btnGuardar)
        selectedDateTextView = view.findViewById(R.id.selectedDateTextView)


        // Verificar si el usuario está autenticado
        if (userId != null) {
            // Crear una referencia a la colección específica del usuario
            val userChatCollectionRef = firestore.collection("chat").document(userId).collection("Outstanding Task")

            // Verificar si el documento con el UID del usuario existe
            userChatCollectionRef.document(userId).get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        // El documento existe, puedes crear la referencia al documento
                        TaskRef = userChatCollectionRef.document(userId)
                    } else {
                        // El documento no existe, créalo primero
                        val data = hashMapOf<String, Any>() // Puedes agregar datos iniciales si lo deseas
                        userChatCollectionRef.document(userId)
                            .set(data)
                            .addOnSuccessListener {
                                // Documento creado exitosamente, ahora puedes crear la referencia al documento
                                TaskRef = userChatCollectionRef.document(userId)
                            }
                            .addOnFailureListener { e ->
                                // Manejar el error al crear el documento
                                Log.e("AgendaFragment", "Error al crear el documento", e)
                            }
                    }
                }
                .addOnFailureListener { e ->
                    // Manejar el error al obtener el documento
                    Log.e("AgendaFragment", "Error al obtener el documento", e)
                }
        } else {
            // Manejar el caso en el que el usuario no está autenticado
            Log.e("ChatFragment", "Usuario no autenticado")
        }


        selectDateButton.setOnClickListener {
            showDatePickerDialog()
        }

        btnSave.setOnClickListener {
            saveTask()
        }

// Inicializar el NavController
        navController = findNavController()

        return view
    }

    private fun enviarTask(documentSnapshot: DocumentSnapshot, mensaje: String, Date: String) {
        // Incrementar el contador
        val counter = (documentSnapshot.get("Counter") as? Long ?: 0) + 1

        // Crear un nuevo mapa con el campo 'TaskX' y 'DateX', donde X es el valor del contador
        val data = hashMapOf(
            "Task$counter" to mensaje,
            "Date$counter" to Date,
            "Counter" to counter
        )

        // Agregar el nuevo campo 'TaskX', 'DateX' y 'Counter' al documento
        TaskRef.set(data, SetOptions.merge())
            .addOnSuccessListener {
                // Éxito al agregar los campos 'TaskX', 'DateX' y 'Counter'
                Log.d("AgendaFragment", "Tarea y fecha guardadas exitosamente")
            }
            .addOnFailureListener { e ->
                // Manejar el error
                Log.e("AgendaFragment", "Error al guardar tarea y fecha", e)
            }
    }


    private fun agregarTaskYFechaAlDocumento(mensaje: String, Date:String) {
        // Crear un nuevo mapa con el campo 'Task'
        val data = hashMapOf(

            "Task" to mensaje,
            "Date" to Date
        )

        // Agregar el nuevo campo 'prompt' al documento
        TaskRef.set(data, SetOptions.merge())
            .addOnSuccessListener {
                // Éxito al agregar el campo 'prompt'
            }
            .addOnFailureListener {
                // Manejar el error
            }
    }


    private fun actualizarTaskYFechaEnDocumento(mensaje: String, Date: String) {
        // Actualizar solo el campo 'prompt' en el documento existente
        TaskRef.update(
            mapOf(
                "Task" to mensaje,
                "Date" to Date
            )
        )
            .addOnSuccessListener {
                // Éxito al actualizar los campos
            }
            .addOnFailureListener {
                // Manejar el error
            }
    }



    // En la función saveTask() del archivo AgendaFragment.kt
    private fun saveTask() {
        taskText = taskEditText.text?.toString()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(selectedDate.time)

        // Verificar si el texto de la tarea no es nulo o vacío
        if (!taskText.isNullOrEmpty()) {
            // Obtén el DocumentSnapshot correcto
            TaskRef.get()
                .addOnSuccessListener { documentSnapshot ->
                    // Llama al método enviarTask para guardar la tarea en Firestore
                    enviarTask(documentSnapshot, taskText!!, formattedDate)

                    // Comenta temporalmente la navegación al fragmento HomeFragment
                    // val navController = findNavController()
                    navController.navigate(R.id.action_agenda_to_nav_home)
                }
                .addOnFailureListener { e ->
                    // Manejar el error al obtener el DocumentSnapshot
                    Log.e("AgendaFragment", "Error al obtener el DocumentSnapshot", e)
                }
        } else {
            // Manejar el caso en que el texto de la tarea es nulo o vacío
            Log.d("AgendaFragment", "El texto de la tarea es nulo o vacío")
        }
    }








    private fun showDatePickerDialog() {
        val datePicker = DatePickerDialog(
            requireContext(),
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                selectedDate.set(year, month, dayOfMonth)
                updateSelectedDateText()
            },
            selectedDate.get(Calendar.YEAR),
            selectedDate.get(Calendar.MONTH),
            selectedDate.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }

    private fun updateSelectedDateText() {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(selectedDate.time)
        selectedDateTextView.text = "Selected Date: $formattedDate"

        // Imprime la fecha seleccionada en Logcat
        Log.d("AgendaFragment", "Selected Date: $formattedDate")
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Agenda().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}