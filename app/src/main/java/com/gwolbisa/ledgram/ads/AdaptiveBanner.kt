package com.gwolbisa.ledgram.ads

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import kotlin.math.roundToInt

@Composable
fun AdaptiveBanner(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val density = LocalDensity.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var availableWidthPx by remember { mutableIntStateOf(0) }
    val availableWidthDp = with(density) {
        availableWidthPx.toDp().value.roundToInt()
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .onSizeChanged { availableWidthPx = it.width },
        contentAlignment = Alignment.Center,
    ) {
        if (availableWidthDp > 0) {
            val adView = remember(context, availableWidthDp) {
                AdView(context).apply {
                    adUnitId = context.getString(AdConfig.TEST_ADAPTIVE_BANNER_ID)
                    setAdSize(
                        AdSize.getLargeAnchoredAdaptiveBannerAdSize(
                            context,
                            availableWidthDp,
                        ),
                    )
                }
            }

            DisposableEffect(adView, lifecycleOwner) {
                val observer = LifecycleEventObserver { _, event ->
                    when (event) {
                        Lifecycle.Event.ON_RESUME -> adView.resume()
                        Lifecycle.Event.ON_PAUSE -> adView.pause()
                        Lifecycle.Event.ON_DESTROY -> adView.destroy()
                        else -> Unit
                    }
                }
                lifecycleOwner.lifecycle.addObserver(observer)
                onDispose {
                    lifecycleOwner.lifecycle.removeObserver(observer)
                    adView.destroy()
                }
            }

            LaunchedEffect(adView) {
                try {
                    adView.loadAd(AdRequest.Builder().build())
                } catch (_: RuntimeException) {
                    // Ads are optional; the Editor remains fully usable.
                }
            }

            AndroidView(
                factory = { adView },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
