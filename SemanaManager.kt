// Calcula o resumo do total de horas por categoria na semana
fun calcularEstatistica(): Map<String, Int> {
    val resumo = mutableMapOf<String, Int>()
    
    for (bloco in blocos168Horas) {
        val t = bloco.tarefa.trim()
        val categoria = when {
            t.isEmpty() || t.equals("Não preenchido", ignoreCase = true) -> "Não preenchido"
            t.contains("sono", ignoreCase = true) || t.contains("dormir", ignoreCase = true) || t.contains("descanso", ignoreCase = true) -> "Sono"
            t.contains("trabalho", ignoreCase = true) || t.contains("trabalhar", ignoreCase = true) || t.contains("trampo", ignoreCase = true) -> "Trabalho"
            t.contains("estudo", ignoreCase = true) || t.contains("faculdade", ignoreCase = true) || t.contains("curso", ignoreCase = true) -> "Estudo"
            t.contains("lazer", ignoreCase = true) || t.contains("exercicio", ignoreCase = true) || t.contains("academia", ignoreCase = true) -> "Lazer / Saúde"
            t.contains("alimentação", ignoreCase = true) || t.contains("almoço", ignoreCase = true) || t.contains("jantar", ignoreCase = true) -> "Alimentação"
            else -> "Outros"
        }
        resumo[categoria] = resumo.getOrDefault(categoria, 0) + 1
    }
    
    return resumo
}
