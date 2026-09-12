package it.gr85.android.apps.em.ui.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import it.gr85.android.apps.em.ui.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionDialog(
    onDismiss: () -> Unit,
    onConfirm: (description: String, amount: String, category: String, transactionType: String) -> Unit,
    categories: List<String>,
    transactionTypes: List<String>,
) {
    var description by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") } // TODO: maybe the category domain object?
    var transactionType by remember { mutableStateOf("") } // TODO: maybe the transaction type domain object?

    var dropdownCategoryExpanded by remember { mutableStateOf(false) }
    var dropdownTransactionTypeExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuova transazione") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descrizione") }
                )

                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Importo") }
                )

                // region TRANSACTION TYPE DROPDOWN
                ExposedDropdownMenuBox(
                    expanded = dropdownTransactionTypeExpanded,
                    onExpandedChange = { dropdownTransactionTypeExpanded = !dropdownTransactionTypeExpanded }
                ) {
                    OutlinedTextField(
                        value = transactionType,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Tipo di transazione") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownTransactionTypeExpanded)
                        },
                        modifier = Modifier.menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = dropdownTransactionTypeExpanded,
                        onDismissRequest = { dropdownTransactionTypeExpanded = false }
                    ) {
                        transactionTypes.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(item) },
                                onClick = {
                                    transactionType = item
                                    dropdownTransactionTypeExpanded = false
                                }
                            )
                        }
                    }
                }
                // endregion TRANSACTION TYPE DROPDOWN

                ExposedDropdownMenuBox(
                    expanded = dropdownCategoryExpanded,
                    onExpandedChange = { dropdownCategoryExpanded = !dropdownCategoryExpanded }
                ) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Categoria") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownCategoryExpanded)
                        },
                        modifier = Modifier.menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = dropdownCategoryExpanded,
                        onDismissRequest = { dropdownCategoryExpanded = false }
                    ) {
                        categories.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(item) },
                                onClick = {
                                    category = item
                                    dropdownCategoryExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(description, amount, category, transactionType) }
            ) {
                Text("Salva")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annulla")
            }
        }
    )
}

@Composable
@Preview
fun AddTransactionDialogPreview() {
    AppTheme {
        Surface {
            AddTransactionDialog(
                onDismiss = {},
                onConfirm = { description, amount, category, transactionType -> },
                categories = listOf("Categoria 1", "Categoria 2", "Categoria 3"),
                transactionTypes = listOf("Entrata", "Uscita")
            )
        }
    }
}
