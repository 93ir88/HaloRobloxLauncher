package com.halo.roblox.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.halo.roblox.data.model.Game
import com.halo.roblox.ui.components.GameListItem
import com.halo.roblox.viewmodel.DiscoverViewModel

@Composable
fun DiscoverScreen(
    onGameClick: (Game) -> Unit,
    vm: DiscoverViewModel = viewModel()
) {
    val state by vm.state.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(Modifier.height(48.dp))

        // Search bar
        SearchBar(
            query    = state.query,
            onChange = vm::onQueryChange
        )

        Spacer(Modifier.height(8.dp))

        when {
            state.isLoading -> LoadingScreen()
            state.error != null && state.results.isEmpty() ->
                ErrorScreen(msg = state.error!!, onRetry = { vm.onQueryChange(state.query) })
            else -> {
                LazyColumn(
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(
                        items = state.results,
                        key   = { it.universeId }
                    ) { game ->
                        GameListItem(
                            game    = game,
                            onClick = { onGameClick(game) }
                        )
                        HorizontalDivider(
                            modifier  = Modifier.padding(start = 108.dp),
                            thickness = 0.5.dp,
                            color     = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchBar(query: String, onChange: (String) -> Unit) {
    OutlinedTextField(
        value         = query,
        onValueChange = onChange,
        modifier      = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        placeholder   = { Text("Search games…") },
        leadingIcon   = { Icon(Icons.Default.Search, contentDescription = null) },
        trailingIcon  = {
            AnimatedVisibility(visible = query.isNotEmpty(), enter = fadeIn(), exit = fadeOut()) {
                IconButton(onClick = { onChange("") }) {
                    Icon(Icons.Default.Clear, contentDescription = "Clear")
                }
            }
        },
        singleLine  = true,
        shape       = androidx.compose.foundation.shape.CircleShape
    )
}
