package com.example.skillforce

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class Congratulations : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private var estiloAprendizaje: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)

            // Obtén el estilo de aprendizaje de los argumentos
            estiloAprendizaje = it.getString("LEARNING_STYLE")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_congratulations, container, false)

        val estiloAprendizajeTextView = view.findViewById<TextView>(R.id.estiloAprendizajeTextView)

        // Verificar que estiloAprendizaje no sea nulo antes de asignar el texto
        estiloAprendizaje?.let {
            estiloAprendizajeTextView.text = " $it"
        }

        val btnNext = view.findViewById<Button>(R.id.btnNextC)

        btnNext.setOnClickListener {
            val navController = findNavController()
            // Asegurarse de que se están pasando los argumentos correctamente
            navController.navigate(
                R.id.action_Congratulations_to_chat,
                bundleOf("LEARNING_STYLE" to estiloAprendizaje)
            )
        }

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String, estiloAprendizaje: String) =
            Congratulations().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                    putString("LEARNING_STYLE", estiloAprendizaje)
                }
            }
    }
}