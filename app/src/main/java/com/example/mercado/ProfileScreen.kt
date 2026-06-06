package com.example.mercado

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mercado.model.LoginRequest
import com.example.mercado.model.CadastroRequest
import com.example.mercado.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun PerfilScreen(logado: Boolean, onLoginSuccess: () -> Unit, onLogout: () -> Unit) {
    var tela by remember { mutableStateOf("menu") }
    if (logado) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text(text = "Perfil", fontSize = 26.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(4.dp)) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(60.dp).background(Color.LightGray), contentAlignment = Alignment.Center) {
                        Text(if(nomeUsuario.isNotEmpty()) nomeUsuario.first().lowercase() else "u", fontSize = 24.sp)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(nomeUsuario, fontWeight = FontWeight.Bold)
                        Text("Cliente", color = Color.Gray)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            MenuItem(Icons.Default.Person, "Informações Pessoais", Color(0xFF673AB7)) {}
            MenuItem(Icons.Default.CreditCard, "Métodos de Pagamento", Color(0xFF03A9F4)) {}
            MenuItem(Icons.Default.LocationOn, "Endereços Salvos", Color(0xFFE91E63)) {}
            MenuItem(Icons.Default.Settings, "Configurações", Color.Gray) {}
            MenuItem(Icons.Default.Help, "Ajuda & Suporte", Color(0xFFF44336)) {}

            Spacer(modifier = Modifier.height(24.dp))

            Button(onClick = { onLogout() }, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.ExitToApp, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Sair")
            }
        }
        return
    }
    when (tela) {
        "menu" -> {
            Column(Modifier.fillMaxSize().padding(24.dp), Arrangement.Center, Alignment.CenterHorizontally) {
                Button(onClick = { tela = "login" }, Modifier.fillMaxWidth()) { Text("Login") }
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedButton(onClick = { tela = "cadastro" }, Modifier.fillMaxWidth()) { Text("Criar Conta") }
            }
        }
        "login" -> LoginScreen(onBack = { tela = "menu" }, onLoginSuccess = { onLoginSuccess() })
        "cadastro" -> CadastroScreen(onBack = { tela = "menu" })
    }
}

@Composable
fun LoginScreen(onBack: () -> Unit, onLoginSuccess: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var erroLogin by remember { mutableStateOf(false) }

    // ESTADO PARA OCULTAR/DESOCULTAR SENHA
    var senhaVisivel by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center) {
        Text("Login", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = senha,
            onValueChange = { senha = it },
            label = { Text("Senha") },
            modifier = Modifier.fillMaxWidth(),
            // Se for visível, mostra texto plano, se não, aplica máscara de senha
            visualTransformation = if (senhaVisivel) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val imagemIcone = if (senhaVisivel) Icons.Default.Visibility else Icons.Default.VisibilityOff
                IconButton(onClick = { senhaVisivel = !senhaVisivel }) {
                    Icon(imageVector = imagemIcone, contentDescription = "Mudar visibilidade")
                }
            }
        )

        if (erroLogin) {
            Text("Email ou senha incorretos", color = Color.Red, fontSize = 12.sp)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val usuario = RetrofitClient.api.login(LoginRequest(email, senha))
                    withContext(Dispatchers.Main) {
                        nomeUsuario = usuario.nome
                        onLoginSuccess()
                    }
                } catch (e: Exception) {
                    erroLogin = true
                }
            }
        }, modifier = Modifier.fillMaxWidth()) { Text("Entrar") }
        TextButton(onClick = onBack) { Text("Voltar") }
    }
}

@Composable
fun CadastroScreen(onBack: () -> Unit) {
    var nome by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }

    // Estados para controle de feedback visual
    var senhaVisivel by remember { mutableStateOf(false) }
    var cadastrando by remember { mutableStateOf(false) }
    var mensagemErro by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center) {
        Text("Criar Conta", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = nome, onValueChange = { nome = it }, label = { Text("Nome") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = senha,
            onValueChange = { senha = it },
            label = { Text("Senha") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (senhaVisivel) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val imagemIcone = if (senhaVisivel) Icons.Default.Visibility else Icons.Default.VisibilityOff
                IconButton(onClick = { senhaVisivel = !senhaVisivel }) {
                    Icon(imageVector = imagemIcone, contentDescription = "Mudar visibilidade")
                }
            }
        )

        // Exibe mensagem de erro caso o cadastro falhe (ex: email duplicado)
        if (mensagemErro != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(mensagemErro!!, color = Color.Red, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (nome.isBlank() || email.isBlank() || senha.isBlank()) {
                    mensagemErro = "Preencha todos os campos!"
                    return@Button
                }

                // Dispara a chamada assíncrona para o backend
                cadastrando = true
                mensagemErro = null

                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        // Faz a requisição POST para /usuarios/cadastro
                        val resposta = RetrofitClient.api.cadastro(
                            CadastroRequest(nome, email, senha)
                        )

                        withContext(Dispatchers.Main) {
                            cadastrando = false
                            if (resposta.isSuccessful) {
                                // Se deu certo, volta para o menu/login automaticamente
                                onBack()
                            } else {
                                mensagemErro = "Erro ao cadastrar. E-mail já existe?"
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            cadastrando = false
                            mensagemErro = "Não foi possível conectar ao servidor."
                            e.printStackTrace()
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !cadastrando // Desativa o botão enquanto envia para evitar cliques duplos
        ) {
            if (cadastrando) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
            } else {
                Text("Cadastrar")
            }
        }

        TextButton(onClick = onBack) { Text("Voltar") }
    }
}