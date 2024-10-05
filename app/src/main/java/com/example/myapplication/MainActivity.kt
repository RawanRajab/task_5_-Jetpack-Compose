package com.example.myapplication
import android.util.Patterns
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Icon
import androidx.compose.ui.res.painterResource


data class Contact(val name: String, val phone: String, val email: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactApp() {
    var contacts by remember { mutableStateOf(listOf<Contact>()) }
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedContact by remember { mutableStateOf<Contact?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Contact List") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(painter = painterResource(id = R.drawable.ic_add), contentDescription = "Add")
            }

        },
        content = { padding ->
            Column(modifier = Modifier.padding(padding).padding(16.dp)) {
                ContactList(contacts) { contact ->
                    selectedContact = contact
                }

                if (showAddDialog) {
                    AddContactDialog(
                        onDismiss = { showAddDialog = false },
                        onConfirm = { newContact ->
                            contacts = contacts + newContact
                            showAddDialog = false
                        }
                    )
                }

                selectedContact?.let { contact ->
                    ContactInteractionDialog(
                        contact = contact,
                        onDismiss = { selectedContact = null },
                        onEdit = { editedContact ->
                            contacts = contacts.map { if (it == contact) editedContact else it }
                            selectedContact = null
                        },
                        onRemove = {
                            contacts = contacts.filter { it != contact }
                            selectedContact = null
                        }
                    )
                }
            }
        }
    )
}

@Composable
fun AddContactDialog(onDismiss: () -> Unit, onConfirm: (Contact) -> Unit) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var isValid by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Contact") },
        text = {
            Column {
                TextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
                TextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone") })
                TextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
                if (!isValid) {
                    Text("Please fill out all fields correctly", color = Color.Red)
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (name.isNotBlank() && phone.isNotBlank() && Patterns.PHONE.matcher(phone).matches() &&
                    email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
                ) {
                    onConfirm(Contact(name, phone, email))
                    onDismiss()
                } else {
                    isValid = false
                }
            }) {
                Text("Confirm")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun ContactList(contacts: List<Contact>, onContactClick: (Contact) -> Unit) {
    LazyColumn {
        items(contacts) { contact ->
            ListItem(
                modifier = Modifier.clickable { onContactClick(contact) },
                headlineContent = { Text(contact.name) }, // Use `headlineContent` for the main text
                supportingContent = { Text(contact.phone) } // Use `supportingContent` for secondary text
            )

            // Add a divider between items for better UI
        }
    }
}

@Composable
fun ContactInteractionDialog(
    contact: Contact,
    onDismiss: () -> Unit,
    onEdit: (Contact) -> Unit,
    onRemove: () -> Unit
) {
    var name by remember { mutableStateOf(contact.name) }
    var phone by remember { mutableStateOf(contact.phone) }
    var email by remember { mutableStateOf(contact.email) }

    var showRemoveConfirmation by remember { mutableStateOf(false) }

    if (showRemoveConfirmation) {
        AlertDialog(
            onDismissRequest = { showRemoveConfirmation = false },
            title = { Text("Remove Contact") },
            text = { Text("Are you sure you want to remove this contact?") },
            confirmButton = {
                Button(onClick = {
                    onRemove()
                    showRemoveConfirmation = false
                    onDismiss()
                }) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                Button(onClick = { showRemoveConfirmation = false }) {
                    Text("Cancel")
                }
            }
        )
    } else {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Edit Contact") },
            text = {
                Column {
                    TextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
                    TextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone") })
                    TextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
                }
            },
            confirmButton = {
                Button(onClick = {
                    onEdit(Contact(name, phone, email))
                    onDismiss()
                }) {
                    Text("Save Changes")
                }
            },
            dismissButton = {
                Button(onClick = { showRemoveConfirmation = true }) {
                    Text("Remove Contact")
                }
            }
        )
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewContactApp() {
    ContactApp()
}
