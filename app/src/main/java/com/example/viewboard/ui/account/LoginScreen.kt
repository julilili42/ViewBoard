package com.example.viewboard.ui.account

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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
import com.example.viewboard.ui.navigation.Screen
import com.example.viewboard.ui.theme.Black
import com.example.viewboard.ui.theme.BlueGray
import com.example.viewboard.ui.theme.Roboto
import com.example.viewboard.ui.theme.uiColor
import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

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
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = null,
                    modifier = Modifier.size(42.dp),
                    tint = uiColor
                )


                Spacer(modifier = Modifier.width(15.dp))
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

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(activity) { authResult ->
                    if (authResult.isSuccessful) {
                        Toast.makeText(context, "Welcome ${authResult.result.user?.displayName}", Toast.LENGTH_SHORT).show()
                        navController.navigate(Screen.HomeScreen.route)
                    } else {
                        Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
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
        // Email/Passwort-Eingabe
        LoginTextField(label = "Email", trailing = "", modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(15.dp))
        LoginTextField(
            label = "Password",
            trailing = "Forgot?",
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(20.dp))

        // Klassischer Login-Button
        Button(
            onClick = { navController.navigate(Screen.HomeScreen.route) },
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isSystemInDarkTheme()) BlueGray else Black,
                contentColor = Color.White,
            ),
            shape = RoundedCornerShape(size = 4.dp)
        ) {
            Text("Login")
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 🟢 Google-Login-Button
        Button(
            onClick = { launcher.launch(signInClient.signInIntent) },
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4285F4), // Google-Blau
                contentColor = Color.White,
            ),
            shape = RoundedCornerShape(size = 4.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_google), // ❗ Eigenes Icon in res/drawable/
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
    ) { }
    Text(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentWidth(Alignment.CenterHorizontally)
            .padding(bottom = 24.dp)
            .clickable(onClick = {navController.navigate(Screen.RegistrationScreen.route)})
        ,
        text = annotatedText
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
    Surface() {
        Column(modifier = Modifier.fillMaxSize()) {
            LoginTopSection()
            Spacer(modifier = Modifier.height(36.dp))
            LoginSection(navController = navController)
            LoginRegisterSection(navController = navController)
        }
    }
}

