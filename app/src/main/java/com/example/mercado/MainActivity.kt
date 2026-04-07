package com.example.mercado

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                    icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Carrinho") },
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
                2 -> PerfilScreen(onLoginSuccess = { selectedTab = 2 })
            }
        }
    }
}

@Composable
fun HomeScreen() {
    Column {
        var categoriaSelecionada by remember { mutableStateOf<String?>(null) }

        if (categoriaSelecionada != null) {
            CategoriaDetalheScreen(categoriaSelecionada!!, onBack = {
                categoriaSelecionada = null
            })
            return
        }

        Box(
            modifier = Modifier
                    .fillMaxWidth()                     // ocupa toda a largura
                .wrapContentHeight()                // altura se ajusta ao conteúdo
                .background(Color(0xFF4DB6AC))     // cor de fundo
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = "Olá, Catarina 👋",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Sua compra de mercado com facilidade",
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(16.dp))

                var searchText by remember { mutableStateOf("") }

                OutlinedTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    placeholder = { Text("Buscar") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Categorias",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { CategoriaCard("🍎", "Hortifruti") { categoriaSelecionada = "Hortifruti" } }
            item { CategoriaCard("🍚", "Mercearia") { categoriaSelecionada = "Mercearia" } }
            item { CategoriaCard("🧀", "Perecíveis/Laticínios") { categoriaSelecionada = "Perecíveis/Laticínios" } }
            item { CategoriaCard("🥩", "Açougue e Peixaria") { categoriaSelecionada = "Açougue e Peixaria" } }
            item { CategoriaCard("🥖", "Padaria e Confeitaria") { categoriaSelecionada = "Padaria e Confeitaria" } }
            item { CategoriaCard("🥤", "Bebidas") { categoriaSelecionada = "Bebidas" } }
            item { CategoriaCard("❄️", "Congelados") { categoriaSelecionada = "Congelados" } }
            item { CategoriaCard("🧼", "Higiene e Limpeza") { categoriaSelecionada = "Higiene e Limpeza" } }
        }
    }
}

@Composable
fun CategoriaCard(emoji: String, titulo: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = emoji, fontSize = 40.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = titulo, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun CategoriaDetalheScreen(nome: String, onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = nome,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))



        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = onBack) {
            Text("Voltar")
        }
    }
}

@Composable
fun CarrinhoScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Carrinho vazio 🛒")
    }
}

@Composable
fun PerfilScreen(onLoginSuccess: () -> Unit) {
    var tela by remember { mutableStateOf("menu") }
    var logado by remember { mutableStateOf(false) }

    // Show profile UI if logged in
    if (logado) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Perfil",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .background(Color.LightGray),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("i", fontSize = 24.sp)
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text("Catarina", fontWeight = FontWeight.Bold)
                        Text("Cliente", color = Color.Gray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Informações Pessoais")
            Text("Métodos de Pagamento")
            Text("Endereços Salvos")
            Text("Favoritos")
            Text("Configurações")
            Text("Ajuda & Suporte")

            Spacer(modifier = Modifier.height(24.dp))

            Button(onClick = { logado = false }) {
                Text("Sair")
            }
        }
        return
    }

    when (tela) {
        "menu" -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = { tela = "login" },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Login")
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = { tela = "cadastro" },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Criar Conta")
                }
            }
        }

        "login" -> {
            LoginScreen(
                onBack = { tela = "menu" },
                onLoginSuccess = {
                    logado = true
                    onLoginSuccess()
                }
            )
        }

        "cadastro" -> {
            CadastroScreen(onBack = { tela = "menu" })
        }
    }
}


@Composable
fun LoginScreen(onBack: () -> Unit, onLoginSuccess: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var loginFeito by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Login", fontSize = 22.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = senha,
            onValueChange = { senha = it },
            label = { Text("Senha") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            if (email == "teste@teste.com" && senha == "1234") {
                loginFeito = true
                onLoginSuccess()
            } else {
                loginFeito = false
            }
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Entrar")
        }

        if (email.isNotEmpty() && senha.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (loginFeito) "Login realizado!" else "Email ou senha inválidos",
                color = if (loginFeito) Color(0xFF2E7D32) else Color.Red,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = onBack) {
            Text("Voltar")
        }
    }
}

@Composable
fun CadastroScreen(onBack: () -> Unit) {
    var nome by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var contaCriada by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Criar Conta", fontSize = 22.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = nome,
            onValueChange = { nome = it },
            label = { Text("Nome") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = senha,
            onValueChange = { senha = it },
            label = { Text("Senha") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { contaCriada = true }, modifier = Modifier.fillMaxWidth()) {
            Text("Cadastrar")
        }

        if (contaCriada) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Conta criada com sucesso!",
                color = Color(0xFF2E7D32),
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = onBack) {
            Text("Voltar")
        }
    }
}