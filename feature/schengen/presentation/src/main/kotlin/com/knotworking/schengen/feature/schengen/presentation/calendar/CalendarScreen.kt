package com.knotworking.schengen.feature.schengen.presentation.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.knotworking.schengen.core.designsystem.component.SchengenTopAppBar
import com.knotworking.schengen.core.designsystem.theme.SchengenAppTheme
import com.knotworking.schengen.core.presentation.ObserveAsEvents
import kotlinx.coroutines.launch
import kotlinx.datetime.toKotlinLocalDate
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CalendarRoot(
    onAddTripClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: CalendarViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is CalendarEvent.OpenAddTripSheet -> onAddTripClick()
        }
    }

    CalendarScreen(
        state = state,
        onAction = viewModel::onAction,
        modifier = modifier
    )
}

@Composable
fun CalendarScreen(
    state: CalendarState,
    onAction: (CalendarAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val calendarState = rememberCalendarState(
        startMonth = state.currentYearMonth.toJavaYearMonth().minusMonths(24),
        endMonth = state.currentYearMonth.toJavaYearMonth().plusMonths(24),
        firstVisibleMonth = state.currentYearMonth.toJavaYearMonth()
    )

    LaunchedEffect(state.currentYearMonth) {
        scope.launch {
            calendarState.animateScrollToMonth(state.currentYearMonth.toJavaYearMonth())
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = { SchengenTopAppBar(title = "Calendar") },
        floatingActionButton = {
            FloatingActionButton(onClick = { onAction(CalendarAction.OnAddTripClick) }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add trip")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            MonthHeader(
                yearMonth = state.currentYearMonth,
                onPreviousClick = { onAction(CalendarAction.OnPreviousMonthClick) },
                onNextClick = { onAction(CalendarAction.OnNextMonthClick) }
            )

            DaysOfWeekHeader()

            HorizontalCalendar(
                state = calendarState,
                dayContent = { calendarDay ->
                    val kotlinDate = calendarDay.date.toKotlinLocalDate()
                    val isInTrip = calendarDay.position == DayPosition.MonthDate &&
                        state.trips.any { trip ->
                            kotlinDate >= trip.entryLocalDate && kotlinDate <= trip.exitLocalDate
                        }
                    val isEntryOrExit = calendarDay.position == DayPosition.MonthDate &&
                        state.trips.any { it.entryLocalDate == kotlinDate || it.exitLocalDate == kotlinDate }

                    CalendarDayCell(
                        day = calendarDay,
                        isInTrip = isInTrip,
                        isEntryOrExit = isEntryOrExit,
                        onClick = {
                            if (calendarDay.position == DayPosition.MonthDate) {
                                onAction(CalendarAction.OnDayClick(kotlinDate))
                            }
                        }
                    )
                }
            )
        }
    }
}

@Composable
private fun MonthHeader(
    yearMonth: YearMonth,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPreviousClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Previous month"
            )
        }
        Text(
            text = "${yearMonth.month.name.lowercase().replaceFirstChar { it.uppercase() }} ${yearMonth.year}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        IconButton(onClick = onNextClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Next month"
            )
        }
    }
}

@Composable
private fun DaysOfWeekHeader(modifier: Modifier = Modifier) {
    val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    Row(modifier = modifier.fillMaxWidth()) {
        daysOfWeek.forEach { day ->
            Text(
                text = day,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun CalendarDayCell(
    day: CalendarDay,
    isInTrip: Boolean,
    isEntryOrExit: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isCurrentMonth = day.position == DayPosition.MonthDate
    val bgColor = when {
        isEntryOrExit -> MaterialTheme.colorScheme.primary
        isInTrip -> MaterialTheme.colorScheme.primaryContainer
        else -> MaterialTheme.colorScheme.surface
    }
    val textColor = when {
        isEntryOrExit -> MaterialTheme.colorScheme.onPrimary
        !isCurrentMonth -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
        else -> MaterialTheme.colorScheme.onSurface
    }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .clip(CircleShape)
            .background(bgColor)
            .clickable(enabled = isCurrentMonth, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.date.dayOfMonth.toString(),
            style = MaterialTheme.typography.bodySmall,
            color = textColor
        )
    }
}

@Preview
@Composable
private fun CalendarScreenPreview() {
    SchengenAppTheme {
        CalendarScreen(
            state = CalendarState(isLoading = false),
            onAction = {}
        )
    }
}
