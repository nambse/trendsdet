package com.example.trend_sdet.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.trend_sdet.ui.screens.productdetail.OptionGroup

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun VariantSelector(
    optionGroups: List<OptionGroup>,
    selectedOptions: Map<String, String>,
    onOptionSelected: (groupName: String, value: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        optionGroups.forEach { group ->
            Text(
                text = group.name,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.testTag("variant_group_${group.name}"),
            )
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                group.values.forEach { value ->
                    FilterChip(
                        selected = selectedOptions[group.name] == value,
                        onClick = { onOptionSelected(group.name, value) },
                        label = { Text(value) },
                        modifier = Modifier.testTag("variant_option_${group.name}_$value"),
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
