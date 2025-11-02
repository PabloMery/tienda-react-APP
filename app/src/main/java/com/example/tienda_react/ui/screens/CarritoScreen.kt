package com.example.tienda_react.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.tienda_react.utils.asCLP
import com.example.tienda_react.utils.ellipsize
import com.example.tienda_react.viewmodel.CartViewModel
import com.example.tienda_react.utils.asCLP
import com.example.tienda_react.utils.ellipsize

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarritoScreen(cartVm: CartViewModel) {
    val ui = cartVm.ui.collectAsState().value
    var couponInput  by remember(ui.couponCode) { mutableStateOf(ui.couponCode ?: "") }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Carrito (${ui.totalItems})") }) }
    ) { pad ->
        Column(
            Modifier
                .padding(pad)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(ui.items, key = { it.id }) { row ->
                    ElevatedCard {
                        Row(
                            Modifier.padding(12.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            val shortName = row.product.name.ellipsize(28)

                            Column(Modifier.weight(1f)) {
                                Text(
                                    text = shortName,
                                    style = MaterialTheme.typography.titleMedium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                if (shortName.endsWith("…")) {
                                    Text(
                                        text = row.product.name,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Text(
                                    text = "Precio: ${row.product.price.asCLP()}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                OutlinedButton(
                                    onClick = { cartVm.setQty(row.id, (row.qty - 1).coerceAtLeast(0)) }
                                ) { Text("-") }

                                Text("x${row.qty}", style = MaterialTheme.typography.titleMedium)

                                OutlinedButton(
                                    onClick = { cartVm.setQty(row.id, row.qty + 1) }
                                ) { Text("+") }
                            }

                            Column(horizontalAlignment = androidx.compose.ui.Alignment.End) {
                                val lineTotal = row.product.price * row.qty
                                Text(lineTotal.asCLP(), style = MaterialTheme.typography.titleMedium)
                                TextButton(onClick = { cartVm.remove(row.id) }) { Text("\uD83D\uDDD1") }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
            Text("Cupón", style = MaterialTheme.typography.titleMedium)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    modifier = Modifier.weight(1f),
                    value = couponInput,
                    onValueChange = { couponInput = it },
                    singleLine = true,
                    label = { Text("Ingresa tu cupón") },
                    supportingText = {
                        val msg = ui.couponError ?: when {
                            ui.couponCode.isNullOrBlank() -> "Ej: SAVE10, FIX5000"
                            else -> "Aplicado: ${ui.couponCode}"
                        }
                        Text(msg)
                    },
                    isError = ui.couponError != null
                )
                Button(onClick = { cartVm.applyCoupon(couponInput) }) { Text("Aplicar") }
                if (!ui.couponCode.isNullOrBlank() || ui.discount > 0) {
                    OutlinedButton(onClick = {
                        couponInput = ""
                        cartVm.clearCoupon()
                    }) { Text("Quitar") }
                }
            }

            Spacer(Modifier.height(8.dp))
            Divider()
            Spacer(Modifier.height(8.dp))

            SummaryRow("Subtotal", ui.totalPrice.asCLP())
            if (ui.discount > 0) {
                SummaryRow("Descuento", "-${ui.discount.asCLP()}", emphasis = true)
            }
            SummaryRow("Total", ui.totalAfterDiscount.asCLP(), large = true)

            Spacer(Modifier.height(12.dp))
            Button(
                enabled = ui.totalItems > 0,
                onClick = { /* TODO: checkout */ },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Pagar") }
        }
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: String,
    large: Boolean = false,
    emphasis: Boolean = false
) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(
            text = label,
            style = if (large) MaterialTheme.typography.titleLarge else MaterialTheme.typography.bodyLarge
        )
        val style = when {
            large -> MaterialTheme.typography.titleLarge
            emphasis -> MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.tertiary)
            else -> MaterialTheme.typography.bodyLarge
        }
        Text(text = value, style = style)
    }
}
