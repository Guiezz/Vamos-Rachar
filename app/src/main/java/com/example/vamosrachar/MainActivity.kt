package com.example.vamosrachar

import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.*

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var tts: TextToSpeech
    private lateinit var valorConta: EditText
    private lateinit var numbPessoas: EditText
    private lateinit var resultadoFinal: TextView
    private var ttsSucess: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        tts = TextToSpeech(this, this)

        valorConta = findViewById(R.id.valorConta)
        numbPessoas = findViewById(R.id.numbPessoas)
        resultadoFinal = findViewById(R.id.resultadoFinal)

        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                calcularDivisao()
            }
            override fun afterTextChanged(s: Editable?) {}
        }

        valorConta.addTextChangedListener(watcher)
        numbPessoas.addTextChangedListener(watcher)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts.language = Locale("pt", "BR")
            ttsSucess = true
        } else {
            Log.e("TTS", "Erro na inicialização do TextToSpeech")
        }
    }

    fun clickCompartilhar(v: View) {
        val texto = resultadoFinal.text.toString()

        if (texto.isNotEmpty()) {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, texto)
            }
            startActivity(Intent.createChooser(intent, "Compartilhar valor via"))
        }
    }

    fun clickFalar(v: View) {
        if (tts.isSpeaking) {
            tts.stop()
        }
        if (ttsSucess) {
            val texto = resultadoFinal.text.toString()
            tts.speak(texto, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    private fun calcularDivisao() {
        val valor = valorConta.text.toString().replace(",", ".")
        val pessoas = numbPessoas.text.toString()

        val valorDouble = valor.toDoubleOrNull()
        val pessoasInt = pessoas.toIntOrNull()

        if (valorDouble != null && pessoasInt != null && pessoasInt > 0) {
            val resultado = valorDouble / pessoasInt
            resultadoFinal.text = "Valor por pessoa: R$ %.2f".format(resultado)
        } else {
            resultadoFinal.text = "Valor por pessoa: R$ 0,00"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        tts.stop()
        tts.shutdown()
    }
}
