package com.gwolbisa.ledgram.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.gwolbisa.ledgram.model.MotionEffect
import com.gwolbisa.ledgram.model.RenderStyle
import com.gwolbisa.ledgram.model.VisualEffect
import com.gwolbisa.ledgram.ui.theme.BrandColorValue
import com.gwolbisa.ledgram.util.DEFAULT_SPEED_MULTIPLIER
import com.gwolbisa.ledgram.util.normalizeEffectSpeedMultiplier
import com.gwolbisa.ledgram.util.normalizeMessageInput
import com.gwolbisa.ledgram.util.normalizeMotionSpeedMultiplier
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

private const val EDITOR_PREFERENCES_NAME = "editor_preferences"

internal val Context.editorPreferencesDataStore by preferencesDataStore(
    name = EDITOR_PREFERENCES_NAME,
    corruptionHandler = ReplaceFileCorruptionHandler { emptyPreferences() },
)

internal object EditorPreferenceKeys {
    val message = stringPreferencesKey("message")
    val textColor = longPreferencesKey("text_color")
    val backgroundColor = longPreferencesKey("background_color")
    val bold = booleanPreferencesKey("bold")
    val italic = booleanPreferencesKey("italic")
    val underline = booleanPreferencesKey("underline")
    val renderStyle = stringPreferencesKey("render_style")
    val motionEffect = stringPreferencesKey("motion_effect")
    val visualEffect = stringPreferencesKey("visual_effect")
    val motionSpeedMultiplier = floatPreferencesKey("motion_speed_multiplier")
    val effectSpeedMultiplier = floatPreferencesKey("effect_speed_multiplier")
}

class EditorPreferencesRepository internal constructor(
    private val dataStore: DataStore<Preferences>,
) {
    constructor(context: Context) : this(context.applicationContext.editorPreferencesDataStore)

    val preferences: Flow<EditorPreferences> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map(Preferences::toEditorPreferences)

    suspend fun save(preferences: EditorPreferences) {
        val safePreferences = preferences.toEditorUiState().toEditorPreferences()
        dataStore.edit { stored ->
            stored.putEditorPreferences(safePreferences)
        }
    }

    suspend fun reset() {
        dataStore.edit { stored ->
            stored.remove(EditorPreferenceKeys.message)
            stored.remove(EditorPreferenceKeys.textColor)
            stored.remove(EditorPreferenceKeys.backgroundColor)
            stored.remove(EditorPreferenceKeys.bold)
            stored.remove(EditorPreferenceKeys.italic)
            stored.remove(EditorPreferenceKeys.underline)
            stored.remove(EditorPreferenceKeys.renderStyle)
            stored.remove(EditorPreferenceKeys.motionEffect)
            stored.remove(EditorPreferenceKeys.visualEffect)
            stored.remove(EditorPreferenceKeys.motionSpeedMultiplier)
            stored.remove(EditorPreferenceKeys.effectSpeedMultiplier)
        }
    }
}

internal fun MutablePreferences.putEditorPreferences(preferences: EditorPreferences) {
    this[EditorPreferenceKeys.message] = preferences.message
    this[EditorPreferenceKeys.textColor] = preferences.textColor
    this[EditorPreferenceKeys.backgroundColor] = preferences.backgroundColor
    this[EditorPreferenceKeys.bold] = preferences.bold
    this[EditorPreferenceKeys.italic] = preferences.italic
    this[EditorPreferenceKeys.underline] = preferences.underline
    this[EditorPreferenceKeys.renderStyle] = preferences.renderStyle.name
    this[EditorPreferenceKeys.motionEffect] = preferences.motionEffect.name
    this[EditorPreferenceKeys.visualEffect] = preferences.visualEffect.name
    this[EditorPreferenceKeys.motionSpeedMultiplier] = preferences.motionSpeedMultiplier
    this[EditorPreferenceKeys.effectSpeedMultiplier] = preferences.effectSpeedMultiplier
}

internal fun Preferences.toEditorPreferences(): EditorPreferences = EditorPreferences(
    message = normalizeMessageInput(this[EditorPreferenceKeys.message].orEmpty()),
    textColor = normalizeStoredColor(
        this[EditorPreferenceKeys.textColor] ?: BrandColorValue.TEXT,
        BrandColorValue.TEXT,
    ),
    backgroundColor = normalizeStoredColor(
        this[EditorPreferenceKeys.backgroundColor] ?: BrandColorValue.BACKGROUND,
        BrandColorValue.BACKGROUND,
    ),
    bold = this[EditorPreferenceKeys.bold] ?: false,
    italic = this[EditorPreferenceKeys.italic] ?: false,
    underline = this[EditorPreferenceKeys.underline] ?: false,
    renderStyle = enumValueOrDefault(
        storedName = this[EditorPreferenceKeys.renderStyle],
        default = RenderStyle.TEXT_GLOW,
    ),
    motionEffect = enumValueOrDefault(
        storedName = this[EditorPreferenceKeys.motionEffect],
        default = MotionEffect.STATIC,
    ),
    visualEffect = enumValueOrDefault(
        storedName = this[EditorPreferenceKeys.visualEffect],
        default = VisualEffect.NONE,
    ),
    motionSpeedMultiplier = normalizeMotionSpeedMultiplier(
        this[EditorPreferenceKeys.motionSpeedMultiplier] ?: DEFAULT_SPEED_MULTIPLIER,
    ),
    effectSpeedMultiplier = normalizeEffectSpeedMultiplier(
        this[EditorPreferenceKeys.effectSpeedMultiplier] ?: DEFAULT_SPEED_MULTIPLIER,
    ),
)

private inline fun <reified T : Enum<T>> enumValueOrDefault(
    storedName: String?,
    default: T,
): T = enumValues<T>().firstOrNull { it.name == storedName } ?: default
