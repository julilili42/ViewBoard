package com.example.viewboard.ui.screens.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import com.example.viewboard.components.profile.SectionCard
import com.example.viewboard.ui.navigation.BackButton

@Composable
fun HelpSupportScreen(modifier: Modifier = Modifier, navController: NavController) {
    var faqExpanded by remember { mutableStateOf(false) }
    var passwordExpanded by remember { mutableStateOf(false) }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header back button and title
        Box(modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
        ) {
            BackButton(
                text = "Back",
                onClick = { navController.popBackStack() },
                modifier = Modifier.align(Alignment.CenterStart)
            )
            Text(
                text = "Help & Support",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Spacer(Modifier.size(24.dp))

        // faq section
        SectionCard(title = "FAQ") {
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { faqExpanded = !faqExpanded }
                    .padding(vertical = 12.dp, horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "How to use ViewBoard?",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = if (faqExpanded) "Collapse" else "Expand",
                    modifier = Modifier
                        .size(24.dp)
                        // rotate icon if clicked
                        .rotate(if (faqExpanded) 90f else 180f)
                )
            }

            AnimatedVisibility(
                visible = faqExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp)
                ) {
                    Text(
                        text = "1. Start the app and sign in.\n" +
                                "2. On the Home screen, choose Global or Personal boards.\n" +
                                "3. Tap the \"+\" icon to add an issue; enter title, description, labels, and deadline.\n" +
                                "4. Tap an issue to edit or delete it.\n" +
                                "5. Use the filter menu to sort by label, deadline, or assignee",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(Modifier.size(8.dp))
                    Divider()
                }
            }


            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { passwordExpanded = !passwordExpanded }
                    .padding(vertical = 12.dp, horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Reseting your password",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = if (passwordExpanded) "Collapse" else "Expand",
                    modifier = Modifier
                        .size(24.dp)
                        // rotate icon if clicked
                        .rotate(if (passwordExpanded) 90f else 180f)
                )
            }
            AnimatedVisibility(
                visible = passwordExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp)
                ) {
                    Text(
                        text = "1. Start the app and login.\n" +
                                "2. Go to your Profile using the Navigation Bar.\n" +
                                "3. Click change password.\n" +
                                "4. Enter current and new password.\n" +
                                "5. Now you can login using your new password.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(Modifier.size(8.dp))
                    Divider()
                }
            }
        }

        Spacer(Modifier.size(24.dp))

        // App-Version
        Text(
            text = "Version 1.0.0",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}
