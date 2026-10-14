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
    private val context = context.applicationContext
    private val preparationLock = Any()
    private val cache = LruCache<Int, DotMatrixGlyph>(GLYPH_CACHE_SIZE)
    @Volatile
    private var preparedFont: PreparedFont? = null

    /** Idempotently loads the typeface, parses cmap, and rasterizes only the fallback glyph. */
    internal fun prepare() {
        preparedFont()
    }

    /** Warms only glyphs needed by the initial visible Dot Matrix Preview. */
    internal fun prepareGlyphs(text: String) {
        prepare()
        text.codePoints()
            .filter { it != '\n'.code && it != '\r'.code }
            .distinct()
            .forEach(::glyphFor)
    }

    override fun glyphFor(codePoint: Int): DotMatrixGlyph {
        val font = preparedFont()
        synchronized(cache) {
            cache.get(codePoint)?.let { return it }
            val glyph = when {
                codePoint == '?'.code -> font.fallbackGlyph
                font.coverage.contains(codePoint) -> rasterize(codePoint, font.paint)
                else -> font.fallbackGlyph
            }
            cache.put(codePoint, glyph)
            return glyph
        }
    }

    private fun preparedFont(): PreparedFont {
        preparedFont?.let { return it }
        return synchronized(preparationLock) {
            preparedFont?.let { return@synchronized it }
            val typeface = requireNotNull(ResourcesCompat.getFont(context, R.font.galmuri11)) {
                "Bundled Galmuri11 font could not be loaded"
            }
            val coverage = readFontCoverage()
            require(coverage.contains('?'.code)) { "Galmuri11 is missing the fallback glyph" }
            require(coverage.contains('한'.code)) { "Galmuri11 is missing modern Hangul" }
            require(coverage.contains('ㄱ'.code) && coverage.contains('ㅏ'.code)) {
                "Galmuri11 is missing Hangul Compatibility Jamo"
            }
            val paint = createPaint(typeface)
            PreparedFont(
                coverage = coverage,
                paint = paint,
                fallbackGlyph = rasterize('?'.code, paint),
            ).also { preparedFont = it }
        }
    }

    private fun createPaint(typeface: android.graphics.Typeface): Paint = Paint().apply {
        color = Color.WHITE
        textSize = GLYPH_TEXT_SIZE_PX
        this.typeface = typeface
        isAntiAlias = false
        isSubpixelText = false
        isDither = false
        hinting = Paint.HINTING_OFF
    }

    private fun rasterize(codePoint: Int, paint: Paint): DotMatrixGlyph {
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
    private fun readFontCoverage(): TrueTypeCodePointCoverage =
        // Font resources are binary resources and can be opened for cmap validation.
        context.resources.openRawResource(R.font.galmuri11).use { input ->
            TrueTypeCodePointCoverage.fromTrueType(input.readBytes())
        }

    private data class PreparedFont(
        val coverage: TrueTypeCodePointCoverage,
        val paint: Paint,
        val fallbackGlyph: DotMatrixGlyph,
    )

    private companion object {
        const val GLYPH_TEXT_SIZE_PX = 12f
        const val GLYPH_CACHE_SIZE = 512
        const val ACTIVE_ALPHA_THRESHOLD = 128
    }
}
