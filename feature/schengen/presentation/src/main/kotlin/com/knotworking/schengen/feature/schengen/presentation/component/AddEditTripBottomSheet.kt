package com.knotworking.schengen.feature.schengen.presentation.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTripBottomSheet(
    editingTripId: String?,
    initialEntryDate: LocalDate?,
    initialExitDate: LocalDate?,
    label: String,
    onDismiss: () -> Unit,
    onEntryDateSelected: (Long) -> Unit,
    onExitDateSelected: (Long) -> Unit,
    onLabelChanged: (String) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState()

    val dateRangePickerState = key(editingTripId) {
        rememberDateRangePickerState(
            initialSelectedStartDateMillis = initialEntryDate?.atStartOfDayIn(TimeZone.UTC)?.toEpochMilliseconds(),
            initialSelectedEndDateMillis = initialExitDate?.atStartOfDayIn(TimeZone.UTC)?.toEpochMilliseconds()
        )
    }

    LaunchedEffect(dateRangePickerState) {
        snapshotFlow { dateRangePickerState.selectedStartDateMillis }
            .filterNotNull()
            .distinctUntilChanged()
            .collect(onEntryDateSelected)
    }
    LaunchedEffect(dateRangePickerState) {
        snapshotFlow { dateRangePickerState.selectedEndDateMillis }
            .filterNotNull()
            .distinctUntilChanged()
            .collect(onExitDateSelected)
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp)
        ) {
            DateRangePicker(
                state = dateRangePickerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp),
                showModeToggle = false
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = label,
                onValueChange = onLabelChanged,
                label = { Text("Label (optional)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.align(Alignment.End)) {
                TextButton(onClick = onDismiss) { Text("Cancel") }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = onSaveClick) { Text("Save") }
            }
        }
    }
}
