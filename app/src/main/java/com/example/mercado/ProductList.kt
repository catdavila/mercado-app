package com.example.mercado

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mercado.model.Produto
import com.example.mercado.network.RetrofitClient

@Composable
fun CategoriaDetalheScreen(nome: String, onBack: () -> Unit) {
    var produtos by remember { mutableStateOf<List<Produto>>(emptyList()) }

    LaunchedEffect(Unit) {
        try {
            val todosOsProdutos = RetrofitClient.api.produtos()
            produtos = todosOsProdutos.filter { it.categoria == nome }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    Column(Modifier.fillMaxSize().padding(all = 16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Voltar")
            }
            Text(nome, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(count = 2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(count = produtos.size) { index ->
                ProdutoItemCard(produtos[index])
            }
        }
    }
}

@Composable
fun ProdutoItemCard(produto: Produto) {
    Card(elevation = CardDefaults.cardElevation(2.dp)) {
        Column(Modifier.padding(8.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(produto.emoji, fontSize = 40.sp)
            Text(produto.nome, fontWeight = FontWeight.Bold, maxLines = 1)
            Text("R$ ${"%.2f".format(produto.preco)}", color = Color.Gray)
            Button(
                onClick = {
                    val index = listaCarrinho.indexOfFirst { it.produto.id == produto.id }
                    if (index != -1) {
                        listaCarrinho[index] = listaCarrinho[index].copy(quantidade = listaCarrinho[index].quantidade + 1)
                    } else {
                        listaCarrinho.add(ItemCarrinho(produto, 1))
                    }
                },
                modifier = Modifier.padding(top = 8.dp).fillMaxWidth(),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text("Adicionar", fontSize = 12.sp)
            }
        }
    }
}