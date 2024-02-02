package com.example.skillforce

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.findNavController
import com.example.skillforce.ui.home.HomeFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


/**
 * A simple [Fragment] subclass.
 * Use the [Support.newInstance] factory method to
 * create an instance of this fragment.
 */
class Support : Fragment() {

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_support, container, false)

        auth = FirebaseAuth.getInstance()

        val btnSend = view.findViewById<View>(R.id.btnSend)
        btnSend.setOnClickListener {
            sendEmailVerification()


            btnSend.setOnClickListener {
                val navController = findNavController()
                navController.navigate(R.id.nav_home)
            }
        }
        return view
    }

    private fun sendEmailVerification() {
        val user = auth.currentUser

        user?.sendEmailVerification()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("Support", "Email sent.")
                } else {
                    Log.e("Support", "Error sending email verification", task.exception)
                    // Puedes mostrar un mensaje de error al usuario si lo deseas.
                }
            }
    }



}



