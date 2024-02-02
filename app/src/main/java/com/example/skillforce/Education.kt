package com.example.skillforce

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.fragment.findNavController


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class Education : Fragment() {
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
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_education, container, false)

        val btnNext = view.findViewById<Button>(R.id.btnNextE)

        // Agrega un listener de clic al botón
        btnNext.setOnClickListener {
            // Realiza la transacción de fragmentos
            val navController = findNavController()
            // Llama a la acción para ir a Questions
            navController.navigate(R.id.action_Education_to_questions)
        }

        return view
    }

    companion object {


        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Education().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}


