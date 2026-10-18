package com.example.weekly168

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

fun corDaTarefa(tarefa: String): Color {
    val t = tarefa.lowercase()
    return when {
        t.contains("sono") || t.contains("dormir") || t.contains("descanso") -> Color(0xFF1E88E5) // Azul
        t.contains("trabalho") || t.contains("trabalhar") || t.contains("trampo") -> Color(0xFFD32F2F) // Vermelho
        t.contains("estudo") || t.contains("faculdade") || t.contains("curso") || t.contains("leitura") -> Color(0xFF7B1FA2) // Roxo
        t.contains("lazer") || t.contains("exercicio") || t.contains("academia") -> Color(0xFF388E3C) // Verde
        t.contains("alimentação") || t.contains("almoço") || t.contains("jantar") || t.contains("comer") -> Color(0xFFF57C00) // Laranja
        else -> Color(0xFF616161) // Cinza
    }
}

@Composable
fun MainScreen(manager: SemanaManager) {
    var diaSelecionado by remember { mutableStateOf(DiaDaSemana.SEGUNDA) }
    var refreshTrigger by remember { mutableStateOf(0) }
    var mostrarModalLote by remember { mutableStateOf(false) }
    var mostrarModalEstatisticas by remember { mutableStateOf(false) }

    val blocoAtual = manager.getBlocoAtual()
    val corAtual = corDaTarefa(blocoAtual?.tarefa ?: "")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // --- DESTAQUE DA HORA ATUAL ---
        Card(
            colors = CardDefaults.cardColors(containerColor = corAtual.copy(alpha = 0.2f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "AGORA (${blocoAtual?.dia?.nomeExibicao ?: ""} - ${blocoAtual?.hora ?: 0}h)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = corAtual
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (blocoAtual?.tarefa.isNullOrEmpty()) "Nenhuma tarefa cadastrada" else blocoAtual!!.tarefa,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = corAtual
                )
            }
        }

        Divider()
        Spacer(modifier = Modifier.height(8.dp))

        // --- BARRA DE FERRAMENTAS ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { mostrarModalLote = true },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Text("Preencher Lote", fontSize = 11.sp)
            }

            Button(
                onClick = { mostrarModalEstatisticas = true },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
            ) {
                Text("📊 Resumo 168h", fontSize = 11.sp)
            }

            OutlinedButton(
                onClick = {
                    manager.copiarDiaParaDiasUteis(diaSelecionado)
                    refreshTrigger++
                }
            ) {
                Text("Copiar p/ Úteis", fontSize = 11.sp)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // --- SELETOR DE DIAS ---
        ScrollableTabRow(selectedTabIndex = diaSelecionado.ordinal) {
            DiaDaSemana.values().forEach { dia ->
                Tab(
                    selected = diaSelecionado == dia,
                    onClick = { diaSelecionado = dia },
                    text = { Text(dia.nomeExibicao.take(3)) }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // --- LISTA DE 24 HORAS DO DIA SELECIONADO ---
        val blocosDoDia = remember(diaSelecionado, refreshTrigger) {
            manager.blocos168Horas.filter { it.dia == diaSelecionado }
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(blocosDoDia) { bloco ->
                ItemHorarioRow(bloco = bloco) {
                    manager.salvarDados()
                    refreshTrigger++
                }
            }
        }
    }

    // MODAL DE PREENCHIMENTO EM LOTE
    if (mostrarModalLote) {
        DialogPreenchimentoLote(
            onDismiss = { mostrarModalLote = false },
            onConfirm = { inicio, fim, tarefa ->
                manager.preencherLote(DiaDaSemana.values().toList(), inicio, fim, tarefa)
                refreshTrigger++
                mostrarModalLote = false
            }
        )
    }

    // MODAL DE ESTATÍSTICAS (RESUMO 168h)
    if (mostrarModalEstatisticas) {
        DialogEstatisticas(
            resumo = manager.calcularEstatistica(),
            onDismiss = { mostrarModalEstatisticas = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemHorarioRow(bloco: BlocoHorario, onAcaoDigitada: () -> Unit) {
    var textoTarefa by remember(bloco.tarefa) { mutableStateOf(bloco.tarefa) }
    val corItem = corDaTarefa(textoTarefa)

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(40.dp)
                    .background(corItem)
            )
            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "${bloco.hora.toString().padStart(2, '0')}:00",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.width(55.dp)
            )
            
            OutlinedTextField(
                value = textoTarefa,
                onValueChange = { 
                    textoTarefa = it
                    bloco.tarefa = it
                    onAcaoDigitada()
                },
                label = { Text("Tarefa / Atividade") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun DialogPreenchimentoLote(
    onDismiss: () -> Unit,
    onConfirm: (horaInicio: Int, horaFim: Int, tarefa: String) -> Unit
) {
    var inicioStr by remember { mutableStateOf("23") }
    var fimStr by remember { mutableStateOf("6") }
    var tarefaInput by remember { mutableStateOf("Sono") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Preencher em Lote (Toda a Semana)") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = tarefaInput,
                    onValueChange = { tarefaInput = it },
                    label = { Text("Nome da Tarefa (Ex: Sono, Trabalho)") }
                )
                OutlinedTextField(
                    value = inicioStr,
                    onValueChange = { inicioStr = it },
                    label = { Text("Hora Inicial (0 a 23)") }
                )
                OutlinedTextField(
                    value = fimStr,
                    onValueChange = { fimStr = it },
                    label = { Text("Hora Final (0 a 23)") }
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val inicio = inicioStr.toIntOrNull() ?: 0
                val fim = fimStr.toIntOrNull() ?: 0
                onConfirm(inicio, fim, tarefaInput)
            }) {
                Text("Aplicar nas 168h")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

@Composable
fun DialogEstatisticas(resumo: Map<String, Int>, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("📊 Resumo Semanal (168h Total)", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                resumo.forEach { (cat, horas) ->
                    val porc = (horas.toFloat() / 168f * 100f)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = cat, fontWeight = FontWeight.Medium)
                        Text(
                            text = "${horas}h (${String.format("%.1f", porc)}%)",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Divider()
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) { Text("Fechar") }
        }
    )
}
