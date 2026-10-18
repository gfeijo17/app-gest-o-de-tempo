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

@Composable
fun MainScreen(manager: SemanaManager = remember { SemanaManager() }) {
    var diaSelecionado by remember { mutableStateOf(DiaDaSemana.SEGUNDA) }
    val blocoAtual = manager.getBlocoAtual()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // --- DESTAQUE DA HORA ATUAL ---
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "AGORA (${blocoAtual?.dia?.nomeExibicao ?: ""} - ${blocoAtual?.hora ?: 0}h)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = blocoAtual?.tarefa ?: "Nenhuma tarefa",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Divider()
        Spacer(modifier = Modifier.height(8.dp))

        // --- SELETOR DE DIAS DA SEMANA ---
        Text(text = "Planejamento Semanal (168h)", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(8.dp))
        
        ScrollableTabRow(selectedTabIndex = diaSelecionado.ordinal) {
            DiaDaSemana.values().forEach { dia ->
                Tab(
                    selected = diaSelecionado == dia,
                    onClick = { diaSelecionado = dia },
                    text = { Text(dia.nomeExibicao.take(3)) } // Exibe "Seg", "Ter", etc.
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // --- LISTA DAS 24 HORAS DO DIA SELECIONADO ---
        val blocosDoDia = manager.blocos168Horas.filter { it.dia == diaSelecionado }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(blocosDoDia) { bloco ->
                ItemHorarioRow(bloco = bloco)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemHorarioRow(bloco: BlocoHorario) {
    var textoTarefa by remember { mutableStateOf(bloco.tarefa) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${bloco.hora.toString().padStart(2, '0')}:00",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.width(60.dp)
            )
            OutlinedTextField(
                value = textoTarefa,
                onValueChange = { 
                    textoTarefa = it
                    bloco.tarefa = it
                },
                label = { Text("Tarefa / Atividade") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
