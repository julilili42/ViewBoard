package com.example.viewboard.frontend.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.viewboard.backend.auth.impl.AuthAPI
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(
    navController: NavController,
) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showCurrent by remember { mutableStateOf(false) }
    var showNew by remember { mutableStateOf(false) }
    var showConfirm by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Change Password") },
                navigationIcon = {
                    TextButton(onClick = { navController.popBackStack() }) {
                        Text("Cancel")
                    }
                },
                actions = {
                    val enabled = newPassword.isNotBlank() && newPassword == confirmPassword
                    TextButton(
                        onClick = {
                            AuthAPI.updatePassword(
                                oldPassword = currentPassword,
                                newPassword = newPassword,
                                onSuccess = { navController.popBackStack() },
                                onError = { msg ->
                                    scope.launch {
                                        snackbarHostState.showSnackbar(msg)
                                    }
                                }
                            )
                        },
                        enabled = enabled
                    ) {
                        Text("Done")
                    }
                }
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = padding.calculateTopPadding() + 8.dp,
                        start = 16.dp,
                        end = 16.dp
                    ),
                verticalArrangement = Arrangement.Top
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                PasswordField(
                    label = "Current Password",
                    password = currentPassword,
                    onValueChange = { currentPassword = it },
                    visible = showCurrent,
                    onVisibilityChange = { showCurrent = it }
                )
                Spacer(modifier = Modifier.height(16.dp))
                PasswordField(
                    label = "New Password",
                    password = newPassword,
                    onValueChange = { newPassword = it },
                    visible = showNew,
                    onVisibilityChange = { showNew = it }
                )
                Spacer(modifier = Modifier.height(16.dp))
                PasswordField(
                    label = "Confirm Password",
                    password = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    visible = showConfirm,
                    onVisibilityChange = { showConfirm = it }
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PasswordField(
    label: String,
    password: String,
    onValueChange: (String) -> Unit,
    visible: Boolean,
    onVisibilityChange: (Boolean) -> Unit
) {
    OutlinedTextField(
        value = password,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = { onVisibilityChange(!visible) }) {
                Icon(
                    imageVector = if (visible) Icons.Filled.Edit else Icons.Filled.Edit,
                    contentDescription = if (visible) "Hide" else "Show"
                )
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
}
