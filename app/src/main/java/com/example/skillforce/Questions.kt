package com.example.skillforce

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

data class Question(val questionText: String, val options: Array<String>)

class Questions : Fragment() {

    private val questionsList = mutableListOf(
        Question(
            "How you like to learn about new things:", arrayOf(
                "Select an answer.",
                "a) Viewing images or graphics.",
                "b) Listening to explanations or talks.",
                "c) Doing practical things or experimenting.",
                "d) Reading and writing."
            )
        ),
        Question(
            "How you best remember important information:", arrayOf(
                "Select an answer.",
                "a) Looking at images or graphics.",
                "b) Listening carefully to explanations.",
                "c) Doing practical exercises.",
                "d) Take notes and write summaries."
            )
        ),
        Question(
            "When you feel most involved when learning:", arrayOf(
                "Select an answer.",
                "a) When reading calmly.",
                "b) By participating in conversations or discussions.",
                "c) By doing practical things and experimenting.",
                "d) When reviewing and relating the written material."
            )
        ),
        Question(
            "How you approach a new problem or challenge:", arrayOf(
                "Select an answer.",
                "a) Following steps and analyzing.",
                "b) Looking for practical examples.",
                "c) Experimenting and testing solutions.",
                "d) Dividing the problem and writing steps."
            )
        ),
        Question(
            "How you prefer to receive feedback on your work:", arrayOf(
                "Select an answer.",
                "a) In writing and explained in detail.",
                "b) Verbally, in a direct conversation.",
                "c) Applying what has been learned in practice.",
                "d) Reviewing and correcting errors in writing."
            )
        ),
        Question(
            "When faced with a difficult new topic:", arrayOf(
                "Select an answer.",
                "a) You prefer to read in detail about the topic.",
                "b) You like to discuss it with other people.",
                "c) You enjoy making practical examples.",
                "d) You prefer to have it explained to you verbally."
            )
        ),
        Question(
            "When you are studying for an exam, what do you prefer to do first?", arrayOf(
                "Select an answer.",
                "a) Read the study material.",
                "b) Discuss the topic with classmates.",
                "c) Practice solving problems.",
                "d) Take notes and summarize the information."
            )
        ),
        Question(
            "In a group project, how do you prefer to contribute?", arrayOf(
                "Select an answer.",
                "a) Investigating and providing information in writing.",
                "b) Facilitate communication and discussions.",
                "c) Carrying out practical tasks or experiments.",
                "d) Organize and structure the information."
            )
        ),
        Question(
            "When you teach something to someone else:", arrayOf(
                "Select an answer.",
                "a) You use written materials and detailed explanations.",
                "b) You prefer to discuss and answer questions.",
                "c) You do practical demonstrations.",
                "d) Provide written steps and guides."
            )
        ),
        Question(
            "When you are learning something new, how do you prefer to receive support?", arrayOf(
                "Select an answer.",
                "a) Through written materials and visual resources.",
                "b) Through verbal discussions and explanations.",
                "c) With opportunities to put into practice what has been learned.",
                "d) With step-by-step written instructions."
            )
        ),
        Question(
            "How do you prefer to explore a new place or navigate a route?",
            arrayOf(
                "Select an answer.",
                "a) Using maps and visual guides.",
                "b) Following verbal directions.",
                "c) Trusting your instincts and physically exploring.",
                "d) Reading written instructions or guidelines."
            )
        ),

        Question(
            "When memorizing a list of items or concepts:",
            arrayOf(
                "Select an answer.",
                "a) Create visual associations or mind maps.",
                "b) Repeat them aloud or discuss with others.",
                "c) Act out or physically interact with the items.",
                "d) Write the list down and review it."
            )
        ),

        Question(
            "When participating in a workshop or training session:",
            arrayOf(
                "Select an answer.",
                "a) Paying attention to visual presentations.",
                "b) Engaging in discussions and asking questions.",
                "c) Participating in hands-on activities.",
                "d) Taking notes and reviewing written materials."
            )
        ),

        Question(
            "How do you prefer to tackle a complex task or project?",
            arrayOf(
                "Select an answer.",
                "a) Organizing and planning with visual aids.",
                "b) Discussing and brainstorming with others.",
                "c) Taking a hands-on approach and experimenting.",
                "d) Outlining and breaking down the task in writing."
            )
        ),

        Question(
            "When learning a new skill, what helps you the most?",
            arrayOf(
                "Select an answer.",
                "a) Watching demonstrations or instructional videos.",
                "b) Listening to instructions and guidance.",
                "c) Physically practicing and repeating the actions.",
                "d) Reading manuals or written guides."
            )
        ),

        Question(
            "In a group discussion or meeting, how do you actively contribute?",
            arrayOf(
                "Select an answer.",
                "a) Using visual aids like charts or slides.",
                "b) Expressing thoughts verbally and engaging in conversation.",
                "c) Demonstrating ideas through actions.",
                "d) Summarizing key points in writing."
            )
        ),

        Question(
            "When reflecting on your own experiences or learning:",
            arrayOf(
                "Select an answer.",
                "a) Visualizing the events or concepts.",
                "b) Recalling conversations and discussions.",
                "c) Remembering the physical sensations and activities.",
                "d) Writing down thoughts and insights."
            )
        ),

        Question(
            "When problem-solving, what approach do you find most effective?",
            arrayOf(
                "Select an answer.",
                "a) Analyzing the situation visually.",
                "b) Talking through possible solutions.",
                "c) Trying different approaches physically.",
                "d) Writing down potential solutions."
            )
        ),

        Question(
            "When you have to explain a complex concept to someone:",
            arrayOf(
                "Select an answer.",
                "a) Using visual aids or diagrams.",
                "b) Engaging in a conversation and answering questions.",
                "c) Providing hands-on examples or demonstrations.",
                "d) Providing a written explanation with details."
            )
        ),

        Question(
            "How do you prefer to review and reinforce what you've learned?",
            arrayOf(
                "Select an answer.",
                "a) Looking at visual summaries or charts.",
                "b) Discussing key points with others.",
                "c) Engaging in practical applications or exercises.",
                "d) Reading and revisiting written notes."
            )
        )

    )
    private val preguntasMostradas = mutableListOf<Question>()

    private var estiloAprendizaje: String? = null
    private val puntajesPorOpcion = intArrayOf(0,1, 2, 3, 4)

    private val conteoPorOpcion = mutableMapOf<Int, Int>()

    private var totalPuntaje: Int = 0
    private val estiloDeAprendizaje =
        arrayOf("","Visual", "Auditory", "Kinaesthetic", "Verbal or Written")
    private var todasLasRespuestasSeleccionadas = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_questions, container, false)

        val parentLayout = view.findViewById<LinearLayout>(R.id.parentLayout)

        // Añade dinámicamente 10 preguntas y respuestas
        for (i in 0 until 10) {
            val randomQuestion = obtenerPreguntaNoRepetida()
            agregarPregunta(
                view,
                parentLayout,
                randomQuestion,
                i
            )  // Añade el índice como último parámetro
            preguntasMostradas.add(randomQuestion)
            Log.d("Pregunta Generada", "Pregunta: ${randomQuestion.questionText}")
        }

        val btnNextQ = view.findViewById<Button>(R.id.btnNextQ)

        btnNextQ.setOnClickListener {
            // Verifica si todas las respuestas están seleccionadas
            if (verificarTodasLasRespuestasSeleccionadas()) {
                calcularPuntajes()

                val navController = findNavController()

                val bundle = Bundle().apply {
                    putString("LEARNING_STYLE", estiloAprendizaje)
                }
                navController.navigate(
                    R.id.action_Questions_to_congratulations,
                    bundle
                )
            } else {
                Toast.makeText(
                    requireContext(),
                    "Select one answer for each question",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        return view
    }
    private fun obtenerPreguntaNoRepetida(): Question {
        var randomQuestion: Question
        do {
            randomQuestion = questionsList.shuffled().first()
        } while (preguntasMostradas.contains(randomQuestion))

        return randomQuestion
    }

    private fun agregarPregunta(
        view: View,
        parentLayout: LinearLayout,
        question: Question,
        questionIndex: Int
    ) {
        // Crea un nuevo diseño lineal horizontal
        val questionTextView = TextView(requireContext())
        questionTextView.text = question.questionText
        parentLayout.addView(questionTextView)

        // Crea un nuevo Spinner para las respuestas
        val questionSpinner = Spinner(requireContext())
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            question.options
        )
        questionSpinner.adapter = adapter
        questionSpinner.setTag("spinner_$questionIndex")  // Asigna un identificador único al Spinner
        parentLayout.addView(questionSpinner)
    }


    private fun calcularPuntajes() {
        totalPuntaje = 0

        // Reinicia el conteo por cada opción
        conteoPorOpcion.clear()

        for (i in 0 until preguntasMostradas.size) {
            val spinnerId = "spinner_$i"
            val spinner = view?.findViewWithTag<Spinner>(spinnerId)

            // Obtén la respuesta seleccionada
            val selectedOption = spinner?.selectedItemPosition ?: -1

            // Suma el puntaje correspondiente
            if (selectedOption != -1) {
                totalPuntaje += puntajesPorOpcion[selectedOption]

                // Incrementa el conteo por opción
                conteoPorOpcion[selectedOption] =
                    conteoPorOpcion.getOrDefault(selectedOption, 0) + 1
            }
        }

        // Imprimir el puntaje total en la consola
        Log.d("Puntaje Total", "El puntaje total es: $totalPuntaje")

        // Imprimir el conteo por opción en la consola
        conteoPorOpcion.forEach { (opcion, conteo) ->
            Log.d("Conteo Opción $opcion", "Seleccionada $conteo veces")
        }

        // Encuentra el conteo máximo
        val conteoMaximo = conteoPorOpcion.values.maxOrNull() ?: 0

        // Verifica si hay opciones con el conteo máximo
        val estilosAprendizaje = estiloDeAprendizaje.filterIndexed { index, _ ->
            conteoPorOpcion[index] == conteoMaximo
        }

        estiloAprendizaje = if (estilosAprendizaje.isNotEmpty()) {
            estilosAprendizaje.joinToString(", ")
        } else {
            "No learning styles were found."
        }

        // Imprime el estilo de aprendizaje en la consola
        Log.d("Learning Style", "Your learning styles are: $estiloAprendizaje")
    }

    private fun verificarTodasLasRespuestasSeleccionadas(): Boolean {
        for (i in 0 until preguntasMostradas.size) {
            val spinnerId = "spinner_$i"
            val spinner = view?.findViewWithTag<Spinner>(spinnerId)

            // Verifica si todas las respuestas están seleccionadas y no es la opción predeterminada
            if (spinner != null) {
                if (spinner.selectedItemPosition == 0) {
                    return false
                }
            }
        }
        return true
    }

}