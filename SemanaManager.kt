package com.example.weekly168

import java.util.Calendar

class SemanaManager {
    // Cria exatamente as 168 horas da semana (7 dias * 24 horas)
    val blocos168Horas: MutableList<BlocoHorario> = mutableListOf()

    init {
        inicializar168Horas()
    }

    private fun inicializar168Horas() {
        blocos168Horas.clear()
        for (dia in DiaDaSemana.values()) {
            for (hora in 0..23) {
                blocos168Horas.add(
                    BlocoHorario(dia = dia, hora = hora, tarefa = "Não preenchido")
                )
            }
        }
    }

    // Busca o bloco de horário atual do sistema
    fun getBlocoAtual(): BlocoHorario? {
        val calendar = Calendar.getInstance()
        val horaAtual = calendar.get(Calendar.HOUR_OF_DAY)
        
        // Mapeia o dia da semana do Calendar para nosso Enum
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
