package com.example.mercado

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen() {
    var categoriaSelecionada by remember { mutableStateOf<String?>(null) }
    var textoBusca by remember { mutableStateOf("") }

    if (categoriaSelecionada != null) {
        CategoriaDetalheScreen(categoriaSelecionada!!, onBack = { categoriaSelecionada = null })
    } else {
        Column {
            HeaderSection(valorBusca = textoBusca, onBuscaChange = { textoBusca = it })
            Text(
                text = "Categorias",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(12.dp)
            )
            val todasCategorias = listOf(
                "🍎" to "Hortifruti", "🍭" to "Doce",
                "🧀" to "Laticínios", "🥩" to "Açougue",
                "🥖" to "Padaria", "🥤" to "Bebidas",
                "❄️" to "Congelados", "🧼" to "Limpeza"
            )
            val categoriasFiltradas = todasCategorias.filter {
                it.second.contains(textoBusca, ignoreCase = true)
            }
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(categoriasFiltradas.size) { index ->
                    CategoriaCard(categoriasFiltradas[index].first, categoriasFiltradas[index].second) {
                        categoriaSelecionada = categoriasFiltradas[index].second
                    }
                }
            }
            if (categoriasFiltradas.isEmpty()) {
                Text("Nenhuma categoria encontrada.", modifier = Modifier.padding(16.dp), color = Color.Gray)
            }
        }
    }
}

@Composable
fun HeaderSection(valorBusca: String, onBuscaChange: (String) -> Unit) {
    Box(modifier = Modifier.fillMaxWidth().background(Color(0xFF673AB7)).padding(16.dp)) {
        Column {
            Text(
                text = if (nomeUsuario.isBlank()) "Olá 👋" else "Olá, $nomeUsuario 👋",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Text("Sua compra de mercado com facilidade", color = Color.White)
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = valorBusca,
                onValueChange = onBuscaChange,
                placeholder = { Text("Buscar categorias...") },
                modifier = Modifier.fillMaxWidth().background(Color.White),
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                trailingIcon = {
                    if (valorBusca.isNotEmpty()) {
                        IconButton(onClick = { onBuscaChange("") }) {
                            Icon(Icons.Default.Close, contentDescription = "Limpar")
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun CategoriaCard(emoji: String, titulo: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().height(120.dp).clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(Modifier.fillMaxSize(), Arrangement.Center, Alignment.CenterHorizontally) {
            Text(emoji, fontSize = 32.sp)
            Text(titulo, fontWeight = FontWeight.Medium)
        }
    }
}