package com.knotworking.schengen.feature.schengen.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.knotworking.schengen.core.designsystem.component.DaysRemainingCard
import com.knotworking.schengen.core.designsystem.component.SchengenTopAppBar
import com.knotworking.schengen.core.designsystem.theme.SchengenAppTheme
import com.knotworking.schengen.core.presentation.ObserveAsEvents
import com.knotworking.schengen.core.presentation.asString
import com.knotworking.schengen.feature.schengen.presentation.component.AddEditTripBottomSheet
import com.knotworking.schengen.feature.schengen.presentation.model.TripUi
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeRoot(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is HomeEvent.ShowError -> scope.launch {
                snackbarHostState.showSnackbar(event.message.asString(context))
            }
            is HomeEvent.TripSaved -> Unit
            is HomeEvent.TripDeleted -> Unit
        }
    }

    HomeScreen(
        state = state,
        snackbarHostState = snackbarHostState,
        onAction = viewModel::onAction,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    state: HomeState,
    snackbarHostState: SnackbarHostState,
    onAction: (HomeAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = { SchengenTopAppBar(title = "Schengen Tracker") },
        floatingActionButton = {
            FloatingActionButton(onClick = { onAction(HomeAction.OnAddTripClick) }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add trip")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                DaysRemainingCard(
                    daysUsed = state.status?.daysUsed ?: 0,
                    daysRemaining = state.status?.daysRemaining ?: 90
                )
            }

            state.status?.let { status ->
                if (status.currentEntryDate != null) {
                    item {
                        StatusBanner(
                            entryDate = status.currentEntryDate.toString(),
                            latestExitDate = status.latestExitDate?.toString() ?: "–"
                        )
                    }
                }
            }

            if (state.trips.isNotEmpty()) {
                item {
                    Text(
                        text = "Trips",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                items(items = state.trips, key = { it.id }) { trip ->
                    TripItem(
                        trip = trip,
                        onEditClick = { onAction(HomeAction.OnEditTripClick(trip.id)) },
                        onDeleteClick = { onAction(HomeAction.OnDeleteTripClick(trip.id)) }
                    )
                }
            }
        }

        if (state.showBottomSheet) {
            AddEditTripBottomSheet(
                editingTripId = state.editingTripId,
                initialEntryDate = state.sheetEntryDate,
                initialExitDate = state.sheetExitDate,
                label = state.sheetLabel,
                onDismiss = { onAction(HomeAction.OnDismissBottomSheet) },
                onEntryDateSelected = { onAction(HomeAction.OnEntryDateSelected(it)) },
                onExitDateSelected = { onAction(HomeAction.OnExitDateSelected(it)) },
                onLabelChanged = { onAction(HomeAction.OnLabelChanged(it)) },
                onSaveClick = { onAction(HomeAction.OnSaveTripClick) }
            )
        }
    }
}

@Composable
private fun StatusBanner(
    entryDate: String,
    latestExitDate: String,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Inside Schengen since $entryDate",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Latest exit: $latestExitDate",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TripItem(
    trip: TripUi,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) onDeleteClick()
            true
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        modifier = modifier,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete trip",
                    modifier = Modifier.padding(end = 16.dp).size(24.dp),
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    ) {
        Card(
            onClick = onEditClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${trip.entryDate} – ${trip.exitDate}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    if (trip.label.isNotBlank()) {
                        Text(
                            text = trip.label,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Text(
                    text = "${trip.durationDays}d",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview
@Composable
private fun HomeScreenPreview() {
    SchengenAppTheme {
        HomeScreen(
            state = HomeState(isLoading = false),
            snackbarHostState = SnackbarHostState(),
            onAction = {}
        )
    }
}
