package com.mokelab.android.devportal

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mokelab.android.devportal.api.DevPortalFeature
import com.mokelab.android.devportal.api.DevPortalNavigator

@Composable
fun DevPortalScreen(
    navigator: DevPortalNavigator,
    features: List<@JvmSuppressWildcards DevPortalFeature>,
) {
    if (features.isEmpty()) {
        EmptyDevPortalScreen()
    } else {
        FeatureListDevPortalScreen(
            navigator = navigator,
            features = features,
        )
    }
}

@Composable
private fun FeatureListDevPortalScreen(
    navigator: DevPortalNavigator,
    features: List<@JvmSuppressWildcards DevPortalFeature>,
) {
    Scaffold { innerPadding ->
        LazyColumn(
            contentPadding = innerPadding,
        ) {
            items(features) { feature ->
                ListItem(
                    headlineContent = {
                        Text(feature.name)
                    },
                    modifier = Modifier.clickable {
                        navigator.goTo(feature.root)
                    }
                )
                HorizontalDivider()
            }
        }
    }
}

@Composable
private fun EmptyDevPortalScreen() {
    Scaffold { innerPadding ->
        Box(
            modifier = Modifier.padding(innerPadding),
            contentAlignment = Alignment.Center,
        ) {
            Text("No debug features available")
        }
    }
}