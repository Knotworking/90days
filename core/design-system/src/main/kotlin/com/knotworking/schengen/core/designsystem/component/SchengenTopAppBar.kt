package com.knotworking.schengen.core.designsystem.component

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.knotworking.schengen.core.designsystem.theme.SchengenAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchengenTopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable () -> Unit = {}
) {
    TopAppBar(
        title = { Text(text = title) },
        modifier = modifier,
        navigationIcon = navigationIcon,
        actions = { actions() }
    )
}

@Preview
@Composable
private fun SchengenTopAppBarPreview() {
    SchengenAppTheme {
        SchengenTopAppBar(title = "Home")
    }
}
