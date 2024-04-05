package com.nara.mia.mobile.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.nara.mia.mobile.R
import com.nara.mia.mobile.ui.components.TopBar

@Composable
fun AboutPage(navController: NavController, drawerState: DrawerState) {
    val uriHandler = LocalUriHandler.current

    Scaffold(
        topBar = {
            TopBar(
                navController = navController,
                drawerState = drawerState,
                title = {
                    Text(text = "About")
                }
            )
        }) { padding ->
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            Spacer(modifier = Modifier.weight(2.0f))

            Icon(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.height(60.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Movies and TV series tracker",
                style = MaterialTheme.typography.headlineSmall
            )
            Text(text = "Created by Adam Tomc")
            Text(text = "Licenced under the terms of AGPL")

            val issuesString = buildAnnotatedString {
                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onBackground)) {
                    append("Report issues on ")
                }
                pushStringAnnotation(tag = "GitHub", annotation = "https://github.com/MiaTracker/mia-mobile")
                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                    append("GitHub")
                }
                pop()
            }
            ClickableText(text = issuesString, style = MaterialTheme.typography.bodyLarge, onClick = { offset ->
                issuesString.getStringAnnotations(tag = "GitHub", start = offset, end = offset).firstOrNull()?.let {
                    uriHandler.openUri(it.item)
                }
            })

            Spacer(modifier = Modifier.weight(2.0f))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.tmdb_logo),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.height(10.dp)
                )
                Text(text = "This product uses the TMDB API but is not endorsed or certified by TMDB.", textAlign = TextAlign.Center, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}