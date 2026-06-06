package com.example.mercado

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.mercado.ui.theme.MercadoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MercadoTheme {
                SupermercadoApp()
            }
        }
    }
}

@Composable
fun SupermercadoApp() {
    var selectedTab by remember { mutableStateOf(0) }
    var usuarioLogado by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Início") },
                    label = { Text("Início") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = {
                        BadgedBox(badge = {
                            if (listaCarrinho.isNotEmpty()) {
                                Badge { Text(listaCarrinho.sumOf { it.quantidade }.toString()) }
                            }
                        }) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = "Carrinho")
                        }
                    },
                    label = { Text("Carrinho") }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
                    label = { Text("Perfil") }
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (selectedTab) {
                0 -> HomeScreen()
                1 -> CarrinhoScreen()
                2 -> PerfilScreen(
                    logado = usuarioLogado,
                    onLoginSuccess = { usuarioLogado = true; selectedTab = 2 },
                    onLogout = { usuarioLogado = false }
                )
            }
        }
    }
}