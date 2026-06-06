package com.example.mercado

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.RemoveCircleOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CarrinhoScreen() {
    val total = listaCarrinho.sumOf { it.produto.preco * it.quantidade }
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Meu Carrinho", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        if (listaCarrinho.isEmpty()) {
            Box(Modifier.weight(1f).fillMaxWidth(), Alignment.Center) {
                Text("Carrinho vazio 🛒", color = Color.Gray)
            }
        } else {
            LazyColumn(Modifier.weight(1f).padding(vertical = 16.dp)) {
                items(listaCarrinho) { item ->
                    LinhaCarrinho(item)
                }
            }
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
            ) {
                Column(Modifier.padding(16.dp)) {
                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                        Text("Subtotal", fontSize = 16.sp)
                        Text("R$ ${"%.2f".format(total)}", fontSize = 16.sp)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { /* Checkout */ },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                    ) {
                        Text("FINALIZAR COMPRA (R$ ${"%.2f".format(total)})")
                    }
                }
            }
        }
    }
}

@Composable
fun LinhaCarrinho(item: ItemCarrinho) {
    Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(item.produto.emoji, fontSize = 30.sp)
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(item.produto.nome, fontWeight = FontWeight.Bold)
            Text("R$ ${"%.2f".format(item.produto.preco)} un.")
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = {
                val index = listaCarrinho.indexOf(item)
                if (index != -1) {
                    if (item.quantidade > 1) {
                        listaCarrinho[index] = item.copy(quantidade = item.quantidade - 1)
                    } else {
                        listaCarrinho.removeAt(index)
                    }
                }
            }) {
                Icon(Icons.Outlined.RemoveCircleOutline, contentDescription = null, tint = Color.Red)
            }
            Text(item.quantidade.toString(), fontWeight = FontWeight.Bold)
            IconButton(onClick = {
                val index = listaCarrinho.indexOf(item)
                if (index != -1) {
                    listaCarrinho[index] = item.copy(quantidade = item.quantidade + 1)
                }
            }) {
                Icon(Icons.Outlined.AddCircleOutline, contentDescription = null, tint = Color(0xFF4DB6AC))
            }
        }
    }
}