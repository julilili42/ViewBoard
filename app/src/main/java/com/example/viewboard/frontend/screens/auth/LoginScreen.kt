package com.example.viewboard.frontend.screens.auth

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.viewboard.R
import com.example.viewboard.frontend.navigation.NavScreens
import com.example.viewboard.frontend.components.theme.Black
import com.example.viewboard.frontend.components.theme.BlueGray
import com.example.viewboard.frontend.components.theme.Roboto
import com.example.viewboard.frontend.components.theme.uiColor
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.material3.TextField
import androidx.compose.ui.text.input.VisualTransformation
import com.example.viewboard.backend.auth.impl.AuthAPI
import com.example.viewboard.backend.auth.impl.FirebaseProvider

/**
 * Top section of the login screen displaying the background shape, logo, and headings.
 *
 * @param modifier optional [Modifier] for layout adjustments
 */
@Composable
fun LoginTopSection(modifier: Modifier = Modifier) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        val uiColor = uiColor()

        Box(contentAlignment = Alignment.TopCenter) {
            Image(
                painter = painterResource(id = R.drawable.shape),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.46f),
                contentScale = ContentScale.FillBounds
            )

            Row(
                modifier = Modifier.padding(top = 80.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.logotest),
                    contentDescription = null,
                    modifier = Modifier.size(45.dp),
                    tint = uiColor
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = stringResource(R.string.viewBoard),
                        style = MaterialTheme.typography.headlineMedium,
                        color = uiColor
                    )
                    Text(
                        text = stringResource(R.string.manageIssues),
                        style = MaterialTheme.typography.titleMedium,
                        color = uiColor
                    )
                }
            }
        }
        Text(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = stringResource(R.string.Login),
            style = MaterialTheme.typography.headlineLarge,
            color = uiColor
        )
    }
}

/**
 * Section containing email and password input fields and the login button.
 *
 * @param modifier optional [Modifier] for layout adjustments
 * @param navController controller used to navigate on successful login
 */
@Composable
fun LoginSection(modifier: Modifier = Modifier, navController: NavController) {
    val context = LocalContext.current
    val activity = context as Activity

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showResetDialog by remember { mutableStateOf(false) }


    if (showResetDialog) {
        PasswordResetDialog(
            email = email,
            onEmailChange = { email = it },
            onConfirm = {
                val normalized = email.trim()
                AuthAPI.sendPasswordResetMail(normalized) { msg ->
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    email = if (email.isEmpty()) "" else email
                    showResetDialog = false
                }
            },
            onDismiss = {
                showResetDialog = false
            }
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            FirebaseProvider.auth.signInWithCredential(credential)
                .addOnCompleteListener(activity) { authResult ->
                    if (authResult.isSuccessful) {
                        Toast.makeText(
                            context,
                            "Willkommen ${authResult.result.user?.displayName}",
                            Toast.LENGTH_SHORT
                        ).show()
                        navController.navigate(NavScreens.HomeNavScreens.route)
                    } else {
                        Toast.makeText(context, "Fehlgeschlagen", Toast.LENGTH_SHORT).show()
                    }
                }
        } catch (e: ApiException) {
            Toast.makeText(context, "Fehler: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    val signInClient = remember {
        GoogleSignIn.getClient(
            context,
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = 30.dp)
    ) {
        LoginTextField(
            label = "Email",
            text = email,
            onTextChange = { email = it },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(15.dp))
        LoginTextField(
            label = "Password",
            trailing = "Forgot?",
            text = password,
            onTextChange = { password = it },
            onTrailingClick = { showResetDialog = true },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()

        )
        Spacer(modifier = Modifier.height(20.dp))

        // Login-Button
        Button(
            onClick = { AuthAPI.loginWithEmail(context, email, password, navController) },
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isSystemInDarkTheme()) BlueGray else Black,
                contentColor = Color.White,
            ),
            shape = RoundedCornerShape(size = 4.dp)
        ) {
            Text("Log in")
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Google-Login-Button
        Button(
            onClick = { launcher.launch(signInClient.signInIntent) },
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4285F4),
                contentColor = Color.White,
            ),
            shape = RoundedCornerShape(size = 4.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_google),
                contentDescription = "Google Login",
                tint = Color.Unspecified,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Login with Google")
        }
    }
}

/**
 * Bottom section offering navigation to the registration screen.
 *
 * @param modifier optional [Modifier] for layout adjustments
 * @param navController controller to navigate to registration
 */
@Composable
fun LoginRegisterSection(modifier: Modifier = Modifier, navController: NavController) {
    val uiColor = uiColor()

    val annotatedText = buildAnnotatedString {
        withStyle(
            style = SpanStyle(
                color = Color(0xFF94A3BB),
                fontSize = 14.sp,
                fontFamily = Roboto,
                fontWeight = FontWeight.Normal
            )
        ) {
            append("Don't have account?")
        }
        withStyle(
            style = SpanStyle(
                color = uiColor,
                fontSize = 14.sp,
                fontFamily = Roboto,
                fontWeight = FontWeight.Medium
            )
        ) {
            append(" ")
            append("Create now")
        }
    }

    Box(
        modifier = Modifier
            .fillMaxHeight(fraction = 0.8f)
            .fillMaxWidth(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Text(
            modifier = modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.CenterHorizontally)
                .padding(bottom = 24.dp)
                .clickable(onClick = { navController.navigate(NavScreens.RegistrationNavScreens.route) }),
            text = annotatedText
        )
    }
}


@Composable
fun PasswordResetDialog(
    email: String,
    onEmailChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Reset Password") },
        text = {
            Column {
                Text("Please enter your email to receive a password reset link:")
                Spacer(modifier = Modifier.height(8.dp))
                LoginTextField(
                    label = "Email",
                    text = email,
                    onTextChange = onEmailChange,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Send")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}


/**
 * A reusable text field for login forms that shows a label and an optional trailing action.
 *
 * @param modifier   Optional [Modifier] for layout adjustments.
 * @param label      The label to display inside the text field (e.g., "Email" or "Password").
 * @param trailing   The text to show in the trailing icon button (e.g., "Forgot?"); pass an empty
 *                   string if no trailing action is needed.
 * @param text       Current input value.
 * @param onTextChange Called when the text changes.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginTextField(
    modifier: Modifier = Modifier.fillMaxWidth(),
    label: String,
    trailing: String = "",
    text: String,
    onTrailingClick: (() -> Unit)? = null,
    onTextChange: (String) -> Unit,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    val uiColor = uiColor()
    TextField(
        modifier = modifier,
        value = text,
        onValueChange = onTextChange,
        label = {
            Text(text = label, style = MaterialTheme.typography.labelMedium, color = uiColor)
        },
        trailingIcon = {
            if (trailing.isNotEmpty()) {
                TextButton(onClick = { onTrailingClick?.invoke() }) {
                    Text(
                        text = trailing,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                        color = uiColor
                    )
                }
            }
        },
        singleLine = true,
        maxLines = 1,
        visualTransformation = visualTransformation
    )
}

/**
 * Root composable for the login screen, assembling top, middle, and bottom sections.
 *
 * @param modifier optional [Modifier] for layout adjustments
 * @param navController controller for screen navigation
 */
@Composable
fun LoginScreen(modifier: Modifier = Modifier, navController: NavController) {
    Surface {
        Column(modifier = Modifier.fillMaxSize()) {
            LoginTopSection()
            Spacer(modifier = Modifier.height(36.dp))
            LoginSection(navController = navController)
            LoginRegisterSection(navController = navController)
        }
    }
}
