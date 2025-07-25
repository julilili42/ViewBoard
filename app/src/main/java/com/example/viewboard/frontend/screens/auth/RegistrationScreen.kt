package com.example.viewboard.frontend.screens.auth

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.viewboard.backend.auth.impl.AuthAPI

/**
 * Top section of the registration screen displaying the background shape, logo, and app title.
 *
 * @param modifier Optional [Modifier] for layout adjustments.
 */
@Composable
fun RegisterTopSection(modifier: Modifier = Modifier) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        val uiColor = uiColor()

        Box(contentAlignment = Alignment.TopCenter, modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.padding(top = 80.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
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
    }
}

/**
 * Section containing the registration form fields and submit button.
 *
 * @param navController Controller used to navigate upon successful registration.
 * @param modifier Optional [Modifier] for layout adjustments.
 */
@Composable
fun RegisterSection(navController: NavController, modifier: Modifier = Modifier) {
    val uiColor = uiColor()
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.Registration),
            style = MaterialTheme.typography.headlineLarge,
            color = uiColor
        )
        Spacer(modifier = Modifier.height(40.dp))

        LoginTextField(
            modifier = modifier,
            label = "Name",
            text = name,
            onTextChange = { name = it })
        Spacer(modifier = Modifier.height(15.dp))
        LoginTextField(
            modifier = modifier,
            label = "Email",
            text = email,
            onTextChange = { email = it })
        Spacer(modifier = Modifier.height(15.dp))
        LoginTextField(
            modifier = modifier,
            label = "Password",
            text = password,
            onTextChange = { password = it })
        Spacer(modifier = Modifier.height(70.dp))

        Button(
            onClick = {
                AuthAPI.register(
                    name = name,
                    email = email,
                    password = password,
                    onSuccess = {
                        Toast.makeText(context, "Registration successful", Toast.LENGTH_SHORT)
                            .show()
                        navController.navigate(NavScreens.HomeNavScreens.route)
                    },
                    onError = { msg ->
                        Toast.makeText(context, "Error: $msg", Toast.LENGTH_SHORT).show()
                    }
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .padding(horizontal = 50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isSystemInDarkTheme()) BlueGray else Black,
                contentColor = Color.White,
            ),
            shape = RoundedCornerShape(size = 4.dp)
        ) {
            Text(
                text = stringResource(R.string.Register),
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium)
            )
        }
    }
}


/**
 * Bottom section offering navigation back to the login screen.
 *
 * @param navController Controller used to navigate back to login.
 * @param modifier Optional [Modifier] for layout adjustments.
 */
@Composable
fun RegisterLoginSection(navController: NavController, modifier: Modifier = Modifier) {
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
            append("Already have an account?")
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
            append("Log in")
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
            .clickable(onClick = { navController.navigate(NavScreens.LoginNavScreens.route) }),
        text = annotatedText
    )
}

/**
 * Root composable for the registration screen,
 * assembling top, form, and footer sections.
 *
 * @param modifier Optional [Modifier] for layout adjustments.
 * @param navController Controller for screen navigation.
 */
@Composable
fun RegistrationScreen(modifier: Modifier = Modifier, navController: NavController) {
    Surface() {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            RegisterTopSection()
            Spacer(modifier = Modifier.height(70.dp))
            RegisterSection(navController = navController)
            RegisterLoginSection(navController = navController)
        }
    }
}