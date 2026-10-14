package com.gwolbisa.ledgram.ui.components

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.LruCache
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.createBitmap
import com.gwolbisa.ledgram.R
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

internal class PixelFontGlyphProvider(context: Context) : DotMatrixGlyphProvider {
    private val typeface = requireNotNull(ResourcesCompat.getFont(context, R.font.galmuri11)) {
        "Bundled Galmuri11 font could not be loaded"
    }
    private val coverage = readFontCoverage(context)
    private val paint = Paint().apply {
        color = Color.WHITE
        textSize = GLYPH_TEXT_SIZE_PX
        typeface = this@PixelFontGlyphProvider.typeface
        isAntiAlias = false
        isSubpixelText = false
        isDither = false
        hinting = Paint.HINTING_OFF
    }
    private val cache = LruCache<Int, DotMatrixGlyph>(GLYPH_CACHE_SIZE)
    private val fallbackGlyph: DotMatrixGlyph by lazy { rasterize('?'.code) }

    init {
        require(coverage.contains('?'.code)) { "Galmuri11 is missing the fallback glyph" }
        require(coverage.contains('한'.code)) { "Galmuri11 is missing modern Hangul" }
        require(coverage.contains('ㄱ'.code) && coverage.contains('ㅏ'.code)) {
            "Galmuri11 is missing Hangul Compatibility Jamo"
        }
    }

    override fun glyphFor(codePoint: Int): DotMatrixGlyph {
        synchronized(cache) {
            cache.get(codePoint)?.let { return it }
        }
        val glyph = if (coverage.contains(codePoint)) rasterize(codePoint) else fallbackGlyph
        synchronized(cache) { cache.put(codePoint, glyph) }
        return glyph
    }

    private fun rasterize(codePoint: Int): DotMatrixGlyph {
        val text = String(Character.toChars(codePoint))
        val bounds = Rect()
        paint.getTextBounds(text, 0, text.length, bounds)
        val metrics = paint.fontMetricsInt
        val left = min(0, bounds.left)
        val right = max(ceil(paint.measureText(text)).toInt(), bounds.right)
        val width = (right - left).coerceIn(1, Int.SIZE_BITS)
        val height = (metrics.bottom - metrics.top).coerceAtLeast(1)
        val baseline = (-metrics.top).coerceIn(0, height)
        val bitmap = createBitmap(width, height, Bitmap.Config.ARGB_8888)
        Canvas(bitmap).drawText(text, -left.toFloat(), baseline.toFloat(), paint)
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
        bitmap.recycle()
        return croppedAlphaMaskGlyph(
            pixels = pixels,
            width = width,
            height = height,
            baseline = baseline,
            activeAlphaThreshold = ACTIVE_ALPHA_THRESHOLD,
        )
    }

    @SuppressLint("ResourceType")
    private fun readFontCoverage(context: Context): TrueTypeCodePointCoverage =
        // Font resources are binary resources and can be opened for cmap validation.
        context.resources.openRawResource(R.font.galmuri11).use { input ->
            TrueTypeCodePointCoverage.fromTrueType(input.readBytes())
        }

    private companion object {
        const val GLYPH_TEXT_SIZE_PX = 12f
        const val GLYPH_CACHE_SIZE = 512
        const val ACTIVE_ALPHA_THRESHOLD = 128
    }
}
