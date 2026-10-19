
package com.example.lista_de_compras

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.NumberFormat
import java.util.Locale

private val Verde = Color(0xFF008F95)
private val Azul = Color(0xFF1565C0)
private val Fundo = Color(0xFFF5F7FA)

private fun moeda(valor: Double): String =
    NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(valor)

data class ItemCompra(
    val id: Int,
    val descricao: String,
    val quantidade: Int,
    val valorUnitario: Double
) {
    val valorTotal: Double
        get() = quantidade * valorUnitario
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                ListaComprasApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaComprasApp() {
    var itens by remember {
        mutableStateOf(
            listOf(
                ItemCompra(1, "Arroz", 2, 8.50),
                ItemCompra(2, "Leite", 3, 4.20),
                ItemCompra(3, "Ovos", 1, 12.00),
                ItemCompra(4, "Café", 1, 16.90),
                ItemCompra(5, "Banana", 6, 1.50),
                ItemCompra(6, "Tomate", 4, 2.30),
                ItemCompra(7, "Frango", 1, 18.90),
                ItemCompra(8, "Alface", 1, 3.50)
            )
        )
    }

    var proximoId by remember { mutableIntStateOf(9) }
    var itemEditando by remember {
        mutableStateOf<ItemCompra?>(null)
    }
    var mostrarFormulario by remember {
        mutableStateOf(false)
    }
    var busca by remember { mutableStateOf("") }

    // Guarda o produto que o usuário deseja excluir.
    var itemParaExcluir by remember {
        mutableStateOf<ItemCompra?>(null)
    }

    val totalCompra = itens.sumOf { it.valorTotal }

    val itensFiltrados = itens.filter {
        it.descricao.contains(busca, ignoreCase = true)
    }

    Scaffold(
        containerColor = Fundo,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.ShoppingCart,
                            contentDescription = null,
                            tint = Verde
                        )

                        Spacer(Modifier.width(12.dp))

                        Text(
                            "Lista de Compras",
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Fundo
                )
            )
        },
        floatingActionButton = {
            if (!mostrarFormulario) {
                FloatingActionButton(
                    onClick = {
                        itemEditando = null
                        mostrarFormulario = true
                    },
                    containerColor = Verde,
                    contentColor = Color.White
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Adicionar item"
                    )
                }
            }
        }
    ) { padding ->

        if (mostrarFormulario) {
            FormularioItem(
                item = itemEditando,
                aoCancelar = {
                    mostrarFormulario = false
                    itemEditando = null
                },
                aoSalvar = { descricao, quantidade, valor ->
                    val existente = itemEditando

                    if (existente == null) {
                        itens = itens + ItemCompra(
                            proximoId++,
                            descricao,
                            quantidade,
                            valor
                        )
                    } else {
                        itens = itens.map {
                            if (it.id == existente.id) {
                                existente.copy(
                                    descricao = descricao,
                                    quantidade = quantidade,
                                    valorUnitario = valor
                                )
                            } else {
                                it
                            }
                        }
                    }

                    mostrarFormulario = false
                    itemEditando = null
                },
                modifier = Modifier.padding(padding)
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 12.dp)
            ) {
                OutlinedTextField(
                    value = busca,
                    onValueChange = { busca = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),
                    placeholder = {
                        Text("Buscar produto")
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = null
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp)
                )

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 12.dp)
                ) {
                    items(
                        itensFiltrados,
                        key = { it.id }
                    ) { item ->

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            ),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(
                                    Modifier.weight(1f)
                                ) {
                                    Text(
                                        item.descricao,
                                        fontSize = 19.sp,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Spacer(Modifier.height(4.dp))

                                    Text(
                                        "Qtd: ${item.quantidade}   " +
                                                "Valor unitário: ${moeda(item.valorUnitario)}",
                                        fontSize = 12.sp,
                                        color = Color.DarkGray
                                    )

                                    Text(
                                        "Valor total: ${moeda(item.valorTotal)}",
                                        fontSize = 13.sp,
                                        color = Color.Gray
                                    )
                                }

                                // Botão para editar o produto.
                                IconButton(
                                    onClick = {
                                        itemEditando = item
                                        mostrarFormulario = true
                                    }
                                ) {
                                    Icon(
                                        Icons.Default.Edit,
                                        contentDescription = "Editar",
                                        tint = Azul
                                    )
                                }

                                // Abre a confirmação antes de excluir.
                                IconButton(
                                    onClick = {
                                        itemParaExcluir = item
                                    }
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Excluir",
                                        tint = Color.Red
                                    )
                                }
                            }
                        }
                    }
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Verde
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Total da compra",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            moeda(totalCompra),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 19.sp
                        )
                    }
                }
            }
        }
    }

    // Janela de confirmação de exclusão.
    itemParaExcluir?.let { item ->
        AlertDialog(
            onDismissRequest = {
                itemParaExcluir = null
            },
            title = {
                Text("Confirmar exclusão")
            },
            text = {
                Text(
                    "Deseja realmente excluir \"${item.descricao}\" da lista?"
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        itens = itens.filterNot {
                            it.id == item.id
                        }

                        itemParaExcluir = null
                    }
                ) {
                    Text(
                        "Excluir",
                        color = Color.Red
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        itemParaExcluir = null
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun FormularioItem(
    item: ItemCompra?,
    aoCancelar: () -> Unit,
    aoSalvar: (String, Int, Double) -> Unit,
    modifier: Modifier = Modifier
) {
    var descricao by remember(item?.id) {
        mutableStateOf(item?.descricao ?: "")
    }

    var quantidadeTexto by remember(item?.id) {
        mutableStateOf(item?.quantidade?.toString() ?: "1")
    }

    var valorTexto by remember(item?.id) {
        mutableStateOf(
            item?.valorUnitario?.toString()?.replace(".", ",") ?: ""
        )
    }

    val quantidade = quantidadeTexto.toIntOrNull() ?: 0

    val valor = valorTexto
        .replace(",", ".")
        .toDoubleOrNull() ?: 0.0

    val total = quantidade * valor

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = aoCancelar) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Voltar"
                )
            }

            Text(
                if (item == null) "Adicionar Item" else "Editar Item",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Descrição")

                OutlinedTextField(
                    value = descricao,
                    onValueChange = { descricao = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text("Nome do produto")
                    },
                    singleLine = true
                )

                Text("Quantidade")

                OutlinedTextField(
                    value = quantidadeTexto,
                    onValueChange = {
                        quantidadeTexto = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    singleLine = true
                )

                Text("Valor unitário")

                OutlinedTextField(
                    value = valorTexto,
                    onValueChange = {
                        valorTexto = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text("Ex.: 8,50")
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
                    singleLine = true
                )

                Text("Valor total")

                OutlinedTextField(
                    value = moeda(total),
                    onValueChange = {},
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true
                )

                Button(
                    onClick = {
                        aoSalvar(
                            descricao.trim(),
                            quantidade,
                            valor
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    enabled = descricao.isNotBlank() &&
                            quantidade > 0 &&
                            valor >= 0.0 &&
                            valorTexto.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Verde
                    )
                ) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null
                    )

                    Spacer(Modifier.width(8.dp))

                    Text("Salvar")
                }

                OutlinedButton(
                    onClick = aoCancelar,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = null
                    )

                    Spacer(Modifier.width(8.dp))

                    Text("Cancelar")
                }
            }
        }
    }
}
