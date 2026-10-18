package com.example.weekly168

enum class DiaDaSemana(val nomeExibicao: String) {
    SEGUNDA("Segunda-feira"),
    TERCA("Terça-feira"),
    QUARTA("Quarta-feira"),
    QUINTA("Quinta-feira"),
    SEXTA("Sexta-feira"),
    SABADO("Sábado"),
    DOMINGO("Domingo")
}

data class BlocoHorario(
    val dia: DiaDaSemana,
    val hora: Int, // Formato 0 a 23
    var tarefa: String = "",
    var categoria: String = "Livre" // Ex: Trabalho, Estudo, Sono, Lazer
)
