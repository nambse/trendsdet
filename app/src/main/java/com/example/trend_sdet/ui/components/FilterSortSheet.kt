package com.example.trend_sdet.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.trend_sdet.domain.model.PriceFilter
import com.example.trend_sdet.domain.model.SortOption

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterSortSheet(
    currentSort: SortOption,
    currentPriceFilter: PriceFilter,
    onSortSelected: (SortOption) -> Unit,
    onPriceFilterChanged: (PriceFilter) -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var sliderRange by remember {
        val min = currentPriceFilter.minPrice.toFloat().coerceIn(0f, 1000f)
        val max = if (currentPriceFilter.maxPrice >= Double.MAX_VALUE / 2) 1000f
        else currentPriceFilter.maxPrice.toFloat().coerceIn(0f, 1000f)
        mutableStateOf(min..max)
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = Modifier.testTag("filter_sort_sheet"),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
        ) {
            Text(
                text = "Sort By",
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(modifier = Modifier.height(8.dp))

            SortOption.entries.forEach { option ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSortSelected(option) }
                        .padding(vertical = 4.dp)
                        .testTag("sort_option_${option.name}"),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RadioButton(
                        selected = currentSort == option,
                        onClick = { onSortSelected(option) },
                    )
                    Text(
                        text = option.displayLabel,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 8.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Price Range",
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "${"$"}${sliderRange.start.toInt()}",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    text = if (sliderRange.endInclusive >= 1000f) "${"$"}1000+"
                    else "${"$"}${sliderRange.endInclusive.toInt()}",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            RangeSlider(
                value = sliderRange,
                onValueChange = { sliderRange = it },
                onValueChangeFinished = {
                    val maxPrice = if (sliderRange.endInclusive >= 1000f) Double.MAX_VALUE
                    else sliderRange.endInclusive.toDouble()
                    onPriceFilterChanged(
                        PriceFilter(
                            minPrice = sliderRange.start.toDouble(),
                            maxPrice = maxPrice,
                        )
                    )
                },
                valueRange = 0f..1000f,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("filter_price_slider"),
            )
        }
    }
}
