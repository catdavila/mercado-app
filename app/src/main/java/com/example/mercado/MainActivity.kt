package com.example.mercado

//imports (bibliotecas do Android, Compose e Material Design)
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mercado.ui.theme.MercadoTheme

//Representa a estrutura de um produto no sistema
data class Produto(
    val id: Int,
    val nome: String,
    val preco: Double,
    val emoji: String,
    val categoria: String
)

//Representa um item que foi adicionado ao carrinho, incluindo sua quantidade
data class ItemCarrinho(
    val produto: Produto,
    var quantidade: Int
)

//mutableStateListOf é uma lista especial do Compose que, quando alterada, avisa automaticamente as telas para se atualizarem (recomposição)
val listaCarrinho = mutableStateListOf<ItemCarrinho>()
var nomeUsuario by mutableStateOf("")

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // MercadoTheme aplica o estilo visual (cores, tipografia) definido no projeto
            MercadoTheme {
                SupermercadoApp()
            }
        }
    }
}

@Composable
fun SupermercadoApp() {
    //Controla qual aba da parte inferior está selecionada (0: Início, 1: Carrinho, 2: Perfil)
    var selectedTab by remember { mutableStateOf(0) }
    var usuarioLogado by remember { mutableStateOf(false) }
    //Scaffold é o esqueleto da tela, facilitando a colocação da barra inferior
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
                        //BadgedBox cria aquela bolinha de notificação com o número de itens
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
        //O conteúdo muda conforme a aba selecionada
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

@Composable
fun HomeScreen() {
    //Estado para saber se o usuário clicou em uma categoria para ver os produtos dela
    var categoriaSelecionada by remember { mutableStateOf<String?>(null) }
    //Estado para o texto da busca
    var textoBusca by remember { mutableStateOf("") }
    if (categoriaSelecionada != null) {
        CategoriaDetalheScreen(categoriaSelecionada!!, onBack = { categoriaSelecionada = null })
    } else {
        Column {
            //Passa o texto e a função de mudança para o Header
            HeaderSection(
                valorBusca = textoBusca,
                onBuscaChange = { textoBusca = it }
            )
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
            //Filtra as categorias conforme o que o usuário digita
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
            //Mensagem caso não encontre nada
            if (categoriasFiltradas.isEmpty()) {
                Text(
                    "Nenhuma categoria encontrada.",
                    modifier = Modifier.padding(16.dp),
                    color = Color.Gray
                )
            }
        }
    }
}


@Composable
fun HeaderSection(valorBusca: String, onBuscaChange: (String) -> Unit) {
    //Faixa roxa no topo com saudação e barra de busca
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF673AB7))
            .padding(16.dp)
    ) {
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
                //Usa o valor que vem da HomeScreen
                value = valorBusca,
                //Chama a função que atualiza na HomeScreen
                onValueChange = onBuscaChange,
                placeholder = { Text("Buscar categorias...") },
                modifier = Modifier.fillMaxWidth().background(Color.White),
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                //Adiciona um botão de limpar busca se tiver texto
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
    //Card clicável que representa uma categoria
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

@Composable
fun CategoriaDetalheScreen(nome: String, onBack: () -> Unit) {
    //Simula um banco de dados de produtos e filtra pela categoria selecionada
    val produtosFake = remember {
        listOf(
            Produto(1, "Maçã", 2.90, "🍎", "Hortifruti"),
            Produto(2, "Banana", 3.50, "🍌", "Hortifruti"),
            Produto(3, "Laranja", 2.50, "🍊", "Hortifruti"),
            Produto(4, "Bala", 2.90, "🍬", "Doce"),
            Produto(5, "Chocolate", 5.90, "🍫", "Doce"),
            Produto(6, "Donuts", 6.90, "🍩", "Doce"),
            Produto(7, "Leite", 5.20, "🥛", "Laticínios"),
            Produto(8, "Queijo", 3.20, "🧀", "Laticínios"),
            Produto(9, "Manteiga", 4.20, "🧈", "Laticínios"),
            Produto(10, "Carne", 20.00, "🥩", "Açougue"),
            Produto(11, "Frango", 15.00, "🍗", "Açougue"),
            Produto(12, "Camarão", 45.00, "🦐", "Açougue"),
            Produto(13, "Pão Francês", 1.50, "🥖", "Padaria"),
            Produto(14, "Sanduíche", 4.00, "🥪", "Padaria"),
            Produto(15, "Pão de Forma", 6.50, "🍞", "Padaria"),
            Produto(16, "Refrigerante", 8.00, "🥤", "Bebidas"),
            Produto(17, "Suco", 8.00, "🧃", "Bebidas"),
            Produto(18, "Cerveja", 8.00, "🍺", "Bebidas"),
            Produto(19, "Sorvete", 12.20, "🍨", "Congelados"),
            Produto(20, "Batata para Fritar", 16.00, "🍟", "Congelados"),
            Produto(21, "Pacote de Gelo", 10.20, "🧊", "Congelados"),
            Produto(22, "Papel Higiênico", 8.90, "🧻", "Limpeza"),
            Produto(23, "Escova de Dente", 3.20, "🪥", "Limpeza"),
            Produto(24, "Sabonete Líquido", 6.50, "🧴", "Limpeza")
        ).filter { it.categoria == nome }
    }
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Voltar") }
            Text(nome, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        }
        //Exibe os produtos da categoria em um Grid de 2 colunas
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(produtosFake.size) { index ->
                ProdutoItemCard(produtosFake[index])
            }
        }
    }
}

@Composable
fun ProdutoItemCard(produto: Produto) {
    //Card individual do produto com botão de Adicionar ao Carrinho
    Card(elevation = CardDefaults.cardElevation(2.dp)) {
        Column(Modifier.padding(8.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(produto.emoji, fontSize = 40.sp)
            Text(produto.nome, fontWeight = FontWeight.Bold, maxLines = 1)
            Text("R$ ${"%.2f".format(produto.preco)}", color = Color.Gray)
            Button(
                onClick = {
                    //Se o produto já está no carrinho, aumenta a quantidade. Se não, adiciona um novo ItemCarrinho.
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

@Composable
fun CarrinhoScreen() {
    //Calcula o valor total somando (preço * quantidade) de todos os itens
    val total = listaCarrinho.sumOf { it.produto.preco * it.quantidade }
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Meu Carrinho", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        if (listaCarrinho.isEmpty()) {
            //Estado vazio do carrinho
            Box(Modifier.weight(1f).fillMaxWidth(), Alignment.Center) {
                Text("Carrinho vazio 🛒", color = Color.Gray)
            }
        } else {
            //Lista vertical com os itens adicionados
            LazyColumn(Modifier.weight(1f).padding(vertical = 16.dp)) {
                items(listaCarrinho) { item ->
                    LinhaCarrinho(item)
                }
            }
            //Resumo de valores e botão de finalização
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
    //Layout horizontal de cada item dentro do carrinho (Emoji, Nome, Botões -/+)
    Row(
        Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(item.produto.emoji, fontSize = 30.sp)
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(item.produto.nome, fontWeight = FontWeight.Bold)
            Text("R$ ${"%.2f".format(item.produto.preco)} un.")
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            //Botão para diminuir quantidade ou remover se for o último
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
            //Botão para aumentar quantidade
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

@Composable
fun MenuItem(icone: ImageVector, titulo: String, corIcone: Color, onClick: () -> Unit) {
    //Componente reutilizável para as opções do perfil (Informação Pessoal, Pagamento, etc)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icone, contentDescription = null, tint = corIcone, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = titulo, modifier = Modifier.weight(1f), fontSize = 16.sp, fontWeight = FontWeight.Medium)
            Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = Color.LightGray)
        }
    }
}

@Composable
fun PerfilScreen(logado: Boolean, onLoginSuccess: () -> Unit, onLogout: () -> Unit) {
    //Gerencia se o usuário está vendo o menu de login ou sua conta logada
    var tela by remember { mutableStateOf("menu") }
    if (logado) {
        //Layout da conta do usuário logado
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text(text = "Perfil", fontSize = 26.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            //Card de identificação do usuário
            Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(4.dp)) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(60.dp).background(Color.LightGray), contentAlignment = Alignment.Center) {
                        Text("C", fontSize = 24.sp)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(nomeUsuario, fontWeight = FontWeight.Bold)
                        Text("Cliente", color = Color.Gray)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            //Lista de opções de menu usando o componente MenuItem
            MenuItem(Icons.Default.Person, "Informações Pessoais", Color(0xFF673AB7)) {}
            MenuItem(Icons.Default.CreditCard, "Métodos de Pagamento", Color(0xFF03A9F4)) {}
            MenuItem(Icons.Default.LocationOn, "Endereços Salvos", Color(0xFFE91E63)) {}
            MenuItem(Icons.Default.Settings, "Configurações", Color.Gray) {}
            MenuItem(Icons.Default.Help, "Ajuda & Suporte", Color(0xFFF44336)) {}

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { onLogout() },
                modifier = Modifier.fillMaxWidth(),
                //colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Icon(Icons.Default.ExitToApp, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Sair")
            }
        }
        return
    }
    //Controle de fluxo entre as telas de Login/Cadastro/Menu Inicial
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
    //Campos de entrada de texto
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var erroLogin by remember { mutableStateOf(false) }
    Column(modifier = Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center) {
        Text("Login", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(value = senha, onValueChange = { senha = it }, label = { Text("Senha") }, modifier = Modifier.fillMaxWidth())
        if (erroLogin) {
            Text("Email ou senha incorretos", color = Color.Red, fontSize = 12.sp)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            //Verificação simples de credenciais (Teste estático)
            if (email == "teste@teste.com" && senha == "1234") {
                nomeUsuario = email.substringBefore("@")
                onLoginSuccess()
            } else {
                erroLogin = true
            }
        }, modifier = Modifier.fillMaxWidth()) { Text("Entrar") }
        TextButton(onClick = onBack) { Text("Voltar") }
    }
}

@Composable
fun CadastroScreen(onBack: () -> Unit) {
    //Tela de cadastro
    var nome by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    Column(modifier = Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center) {
        Text("Criar Conta", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = nome, onValueChange = { nome = it }, label = { Text("Nome") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(value = senha, onValueChange = { senha = it }, label = { Text("Senha") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            nomeUsuario = nome
        }, modifier = Modifier.fillMaxWidth()) { Text("Cadastrar") }
        TextButton(onClick = onBack) { Text("Voltar") }
    }
}