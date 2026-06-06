package com.example.mercado.model

data class Produto(
    val id: Int,
    val nome: String,
    val preco: Double,
    val emoji: String,
    val categoria: String
)