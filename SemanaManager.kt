package com.example.weekly168

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject
import java.util.Calendar

class SemanaManager(private val context: Context) {
    val blocos168Horas: MutableList<BlocoHorario> = mutableListOf()
    private val prefs: SharedPreferences = context.getSharedPreferences("weekly168_prefs", Context.MODE_PRIVATE)

    init {
        inicializar168Horas()
        carregarDados()
    }

    private fun inicializar168Horas() {
        blocos168Horas.clear()
        for (dia in DiaDaSemana.values()) {
            for (hora in 0..23) {
                blocos168Horas.add(
                    BlocoHorario(dia = dia, hora = hora, tarefa = "")
                )
            }
        }
    }

    // Salva toda a matriz das 168h no dispositivo
    fun salvarDados() {
        val jsonArray = JSONArray()
        for (bloco in blocos168Horas) {
            val jsonObject = JSONObject().apply {
                put("dia", bloco.dia.name)
                put("hora", bloco.hora)
                put("tarefa", bloco.tarefa)
            }
            jsonArray.put(jsonObject)
        }
        prefs.edit().putString("semana_dados", jsonArray.toString()).apply()
    }

    // Carrega os dados salvos anteriormente
    private fun carregarDados() {
        val jsonString = prefs.getString("semana_dados", null) ?: return
        try {
            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val diaStr = obj.getString("dia")
                val hora = obj.getInt("hora")
                val tarefa = obj.getString("tarefa")

                blocos168Horas.find { it.dia.name == diaStr && it.hora == hora }?.let {
                    it.tarefa = tarefa
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Preenchimento em lote para horários contínuos (Ex: 23h às 7h)
    fun preencherLote(dias: List<DiaDaSemana>, horaInicio: Int, horaFim: Int, tarefa: String) {
        for (dia in dias) {
            if (horaInicio <= horaFim) {
                for (h in horaInicio..horaFim) {
                    blocos168Horas.find { it.dia == dia && it.hora == h }?.tarefa = tarefa
                }
            } else {
                // Caso atravesse a madrugada (Ex: 22h até as 06h)
                for (h in horaInicio..23) {
                    blocos168Horas.find { it.dia == dia && it.hora == h }?.tarefa = tarefa
                }
                for (h in 0..horaFim) {
                    blocos168Horas.find { it.dia == dia && it.hora == h }?.tarefa = tarefa
                }
            }
        }
        salvarDados()
    }

    fun copiarDiaParaDiasUteis(diaOrigem: DiaDaSemana) {
        val tarefasOrigem = blocos168Horas.filter { it.dia == diaOrigem }
        val diasUteis = listOf(DiaDaSemana.SEGUNDA, DiaDaSemana.TERCA, DiaDaSemana.QUARTA, DiaDaSemana.QUINTA, DiaDaSemana.SEXTA)

        for (blocoOrigem in tarefasOrigem) {
            for (diaDestino in diasUteis) {
                if (diaDestino != diaOrigem) {
                    blocos168Horas.find { it.dia == diaDestino && it.hora == blocoOrigem.hora }?.let {
                        it.tarefa = blocoOrigem.tarefa
                    }
                }
            }
        }
        salvarDados()
    }

    fun getBlocoAtual(): BlocoHorario? {
        val calendar = Calendar.getInstance()
        val horaAtual = calendar.get(Calendar.HOUR_OF_DAY)
        
        val diaAtualEnum = when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> DiaDaSemana.SEGUNDA
            Calendar.TUESDAY -> DiaDaSemana.TERCA
            Calendar.WEDNESDAY -> DiaDaSemana.QUARTA
            Calendar.THURSDAY -> DiaDaSemana.QUINTA
            Calendar.FRIDAY -> DiaDaSemana.SEXTA
            Calendar.SATURDAY -> DiaDaSemana.SABADO
            Calendar.SUNDAY -> DiaDaSemana.DOMINGO
            else -> DiaDaSemana.SEGUNDA
        }

        return blocos168Horas.find { it.dia == diaAtualEnum && it.hora == horaAtual }
    }
}
