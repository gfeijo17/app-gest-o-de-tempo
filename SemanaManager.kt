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
}
