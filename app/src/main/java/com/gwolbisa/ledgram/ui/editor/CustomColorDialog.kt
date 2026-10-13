package com.gwolbisa.ledgram.ui.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.gwolbisa.ledgram.R
import com.gwolbisa.ledgram.util.HsvColor
import com.gwolbisa.ledgram.util.argbToHsv
import com.gwolbisa.ledgram.util.formatHexColor
import com.gwolbisa.ledgram.util.hsvToArgb
import com.gwolbisa.ledgram.util.parseHexColor
import kotlin.math.roundToInt

@Composable
fun CustomColorDialog(
    initialColor: Long,
    targetLabel: String,
    onDismiss: () -> Unit,
    onApply: (Long) -> Unit,
) {
    val initialHsv = remember(initialColor) { argbToHsv(initialColor) }
    var hue by rememberSaveable(initialColor) { mutableFloatStateOf(initialHsv.hue) }
    var saturation by rememberSaveable(initialColor) {
        mutableFloatStateOf(initialHsv.saturation)
    }
    var brightness by rememberSaveable(initialColor) { mutableFloatStateOf(initialHsv.value) }
    var hexInput by rememberSaveable(initialColor) {
        mutableStateOf(formatHexColor(initialColor))
    }
    val parsedHex = parseHexColor(hexInput)
    val currentColor = hsvToArgb(HsvColor(hue, saturation, brightness))
    val previewDescription = stringResource(
        R.string.custom_color_preview_description,
        formatHexColor(currentColor),
    )

    fun updateFromSliders(
        nextHue: Float = hue,
        nextSaturation: Float = saturation,
        nextBrightness: Float = brightness,
    ) {
        hue = nextHue
        saturation = nextSaturation
        brightness = nextBrightness
        hexInput = formatHexColor(
            hsvToArgb(HsvColor(nextHue, nextSaturation, nextBrightness)),
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.custom_color)) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 460.dp)
                    .verticalScroll(rememberScrollState()),
            ) {
                Text(
                    text = stringResource(R.string.custom_color_target, targetLabel),
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(72.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(currentColor))
                        .semantics { contentDescription = previewDescription },
                )
                Spacer(Modifier.height(16.dp))
                Text(stringResource(R.string.hue_value, hue.roundToInt()))
                Slider(
                    value = hue,
                    onValueChange = { updateFromSliders(nextHue = it) },
                    valueRange = 0f..360f,
                )
                Text(
                    stringResource(
                        R.string.saturation_value,
                        (saturation * 100f).roundToInt(),
                    ),
                )
                Slider(
                    value = saturation,
                    onValueChange = { updateFromSliders(nextSaturation = it) },
                    valueRange = 0f..1f,
                )
                Text(
                    stringResource(
                        R.string.brightness_value,
                        (brightness * 100f).roundToInt(),
                    ),
                )
                Slider(
                    value = brightness,
                    onValueChange = { updateFromSliders(nextBrightness = it) },
                    valueRange = 0f..1f,
                )
                OutlinedTextField(
                    value = hexInput,
                    onValueChange = { input ->
                        hexInput = input
                        parseHexColor(input)?.let { color ->
                            val hsv = argbToHsv(color)
                            hue = hsv.hue
                            saturation = hsv.saturation
                            brightness = hsv.value
                            hexInput = formatHexColor(color)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    label = { Text(stringResource(R.string.hex_color)) },
                    singleLine = true,
                    isError = parsedHex == null,
                    supportingText = {
                        if (parsedHex == null) {
                            Text(stringResource(R.string.invalid_hex_color))
                        }
                    },
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onApply(currentColor) },
                enabled = parsedHex != null,
            ) {
                Text(stringResource(R.string.apply))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        },
    )
}
