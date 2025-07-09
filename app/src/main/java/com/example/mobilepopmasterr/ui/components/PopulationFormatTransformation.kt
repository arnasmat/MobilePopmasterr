package com.example.mobilepopmasterr.ui.components

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

// I'll be honest, this was written by claude
class PopulationFormatTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val original = text.text

        // Format with spaces every 3 digits from right
        val formatted = original.reversed()
            .chunked(3)
            .joinToString(" ")
            .reversed()

        // Simple offset mapping that accounts for spaces
        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                // Add number of spaces before this position
                return offset + (offset / 3).coerceAtMost((original.length - 1) / 3)
            }

            override fun transformedToOriginal(offset: Int): Int {
                // Remove number of spaces
                return offset - (offset / 4).coerceAtMost((formatted.length - 1) / 4)
            }
        }

        return TransformedText(AnnotatedString(formatted), offsetMapping)
    }
}

fun formatPopulationString(input: String): String {
    if (input.isEmpty()) return ""

    return input.reversed()
        .chunked(3)
        .joinToString(" ")
        .reversed()
}