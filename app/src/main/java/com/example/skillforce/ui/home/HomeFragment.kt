package com.example.skillforce.ui.home

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.skillforce.R
import com.example.skillforce.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions
import com.google.firebase.ml.common.modeldownload.FirebaseModelManager
import com.google.firebase.ml.custom.FirebaseCustomRemoteModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val Id = auth.currentUser?.uid

    companion object {
        private const val MY_PERMISSIONS_REQUEST_NOTIFICATION = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        //machine learning
        val remoteModel = FirebaseCustomRemoteModel.Builder("skillforce").build()
        val conditions = FirebaseModelDownloadConditions.Builder()
            .requireWifi()
            .build()
        FirebaseModelManager.getInstance().download(remoteModel, conditions)
            .addOnCompleteListener {
                // Success.
            }


        // Inflar el diseño antes de acceder a las vistas
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        // Acceder al botón después de inflar el diseño
        val btnAgenda = root.findViewById<Button>(R.id.btnAgenda)

        val txtListTask = root.findViewById<TextView>(R.id.lisTask)

        // Realizar la consulta a Firestore
        Id?.let { uid ->
            val documentReference = firestore
                .collection("chat")
                .document(uid)
                .collection("Outstanding Task")
                .document(uid)

            documentReference.get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        // Obtener los campos "Task" y "Date" y mostrarlos en el TextView
                        val camposTask = mutableListOf<String>()
                        val camposDate = mutableListOf<String>()

                        for (i in 1..Int.MAX_VALUE) {
                            val task = document.getString("Task$i")
                            val date = document.getString("Date$i")

                            if (task != null && task.isNotEmpty()) {
                                camposTask.add("Task$i: $task")
                            } else {
                                // Si el campo "Task" no existe o está vacío, salimos del bucle
                                break
                            }

                            if (date != null && date.isNotEmpty()) {
                                camposDate.add("Deadline: $date")

                                // Agregar la fecha a la notificación
                                showNotification(date)
                            } else {
                                // Si el campo "Date" no existe o está vacío, salimos del bucle
                                break
                            }
                        }

                        // Combina los resultados de "Task" y "Date"
                        val camposCombinados = mutableListOf<String>()
                        for (i in camposTask.indices) {
                            camposCombinados.add(camposTask[i] + "\n" + camposDate.getOrElse(i) { "" })
                        }

                        // Actualizar el TextView con los datos obtenidos
                        val textoMostrado =
                            if (camposCombinados.isNotEmpty()) camposCombinados.joinToString("\n")
                            else "No data available"
                        txtListTask.text = textoMostrado

                    } else {
                        // El documento no existe
                        txtListTask.text = "Add a new task"
                    }
                }
                .addOnFailureListener { e ->
                    // Manejar errores en la consulta
                    txtListTask.text = "Error getting data: ${e.message}"
                }
        }

        btnAgenda.setOnClickListener {
            val navController = findNavController()
            navController.navigate(R.id.action_nav_home_to_agenda)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Método para mostrar una notificación de acción
    private fun showNotification(expiryDate: String) {

        val channelId = "MyChannelId"
        val notificationId = 1

        // Verificar permisos de notificación
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Solicitar permisos si no están otorgados
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                MY_PERMISSIONS_REQUEST_NOTIFICATION
            )
        } else {
            // Tienes permisos, procede con la creación de la notificación
            // Crear un canal de notificación para dispositivos con Android Oreo y superior
            createNotificationChannel()

            // Crear un intent para la acción de la notificación (puedes personalizar esto según tus necesidades)
            val intent = Intent(requireContext(), HomeFragment::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent = PendingIntent.getActivity(
                requireContext(),
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

            // Construir la notificación
            val builder = NotificationCompat.Builder(requireContext(), channelId)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle("Welcome to Skillforce")
                .setContentText("You have a pending task that expires in $expiryDate")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)  // Cierra la notificación al hacer clic en ella

            // Mostrar la notificación
            with(NotificationManagerCompat.from(requireContext())) {
                notify(notificationId, builder.build())
            }
        }
    }

    // Crear un canal de notificación para Android Oreo y superior
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Nombre del canal"
            val descriptionText = "Descripción del canal"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("MyChannelId", name, importance).apply {
                description = descriptionText
            }

            // Registrar el canal con el sistema
            val notificationManager =
                requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}