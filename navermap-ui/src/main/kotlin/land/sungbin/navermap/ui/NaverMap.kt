/*
 * Copyright 2024 SOUP, Ji Sungbin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package land.sungbin.navermap.ui

import android.os.Bundle
import android.widget.ImageView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import androidx.compose.runtime.Composition
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.currentCompositionLocalContext
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCompositionContext
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.node.Ref
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.NaverMapOptions
import land.sungbin.navermap.runtime.MapApplier
import land.sungbin.navermap.runtime.contributor.Contributor
import land.sungbin.navermap.runtime.contributor.Contributors
import land.sungbin.navermap.runtime.contributor.MapViewContributor
import land.sungbin.navermap.runtime.delegate.MapViewDelegator
import land.sungbin.navermap.runtime.delegate.NaverMapDelegator
import land.sungbin.navermap.runtime.modifier.MapModifier
import land.sungbin.navermap.runtime.modifier.MapModifierContributionNode
import land.sungbin.navermap.runtime.node.DelegatedMapView
import land.sungbin.navermap.runtime.node.LayoutNode
import land.sungbin.navermap.ui.contract.NaverMapContentComposable
import land.sungbin.navermap.ui.modifier.delegator.LocalNaverMapDelegator
import land.sungbin.navermap.ui.modifier.navermap.NaverMapDelegate
import land.sungbin.navermap.ui.modifier.navermap.NaverMapModifier
import com.naver.maps.map.R as NaverMapR

private val NoContent: @[Composable NaverMapContentComposable] NaverMapContent.() -> Unit = {}

@Composable
public fun NaverMap(
  modifier: Modifier = Modifier,
  naverMapModifier: NaverMapModifier = NaverMapModifier,
  mapContent: @[Composable NaverMapContentComposable] NaverMapContent.() -> Unit = NoContent,
) {
  val context = LocalContext.current
  var map by remember { mutableStateOf<MapView?>(null) }
  val naverMap = remember { Ref<NaverMap>() }

  if (map != null) AndroidView(modifier = modifier, factory = { map!! })

  val parentCompositionContext = rememberCompositionContext()
  val compositionLocalContext = currentCompositionLocalContext

  val delegator = LocalNaverMapDelegator.current
  val lifecycle = LocalLifecycleOwner.current.lifecycle
  val previousState = remember { mutableStateOf(Lifecycle.Event.ON_CREATE) }
  val savedInstanceState = rememberSaveable { Bundle() }

  val mapComposition = remember { Composition(MapApplier(), parent = parentCompositionContext) }
  val mapLayoutModifier = remember(lifecycle, savedInstanceState) {
    MapModifier
      .then(MapViewInstanceInterceptorNode { instance -> map = instance })
      .mapLifecycle(context, lifecycle, previousState, savedInstanceState)
  }

  val scope = remember(naverMap) {
    object : NaverMapContent {
      override fun moveCamera(update: CameraUpdate) {
        val currentMap = checkNotNull(naverMap.value) { "NaverMap is not ready yet." }
        currentMap.moveCamera(update)
      }
    }
  }

  mapComposition.setContent {
    CompositionLocalProvider(compositionLocalContext) {
      ComposeNode<LayoutNode, MapApplier>(
        factory = {
          LayoutNode(
            modifier = mapLayoutModifier then naverMapModifier.toMapModifier(delegator),
            factory = {
              object : MapViewDelegator {
                private val lazyInstance by lazy { unwrapAppCompat(MapView(context, NaverMapOptions())) }
                override val instance: DelegatedMapView get() = lazyInstance
                override fun getMapAsync(block: (NaverMapDelegator) -> Unit) {
                  lazyInstance.getMapAsync { map ->
                    naverMap.value = map
                    block(NaverMapDelegator(map))
                  }
                }
              }
            },
          )
        },
        update = {
          update(naverMapModifier) {
            this.modifier = mapLayoutModifier then it.toMapModifier(delegator)
          }
        },
      ) {
        scope.mapContent()
      }
    }
  }

  DisposableEffect(mapComposition) {
    onDispose {
      mapComposition.dispose()
      map = null
    }
  }
}

@Stable
private fun NaverMapModifier.toMapModifier(delegator: NaverMapDelegate): MapModifier =
  fold<MapModifier>(MapModifier) { acc, element ->
    element.delegator = delegator
    val node = element.getContributionNode() ?: return@fold acc
    acc then node
  }

@Immutable
private class MapViewInstanceInterceptorNode(
  var onReady: ((map: MapView) -> Unit)?,
) : MapModifierContributionNode {
  override val kindSet = Contributors.MapView

  override fun create() = object : MapViewContributor {
    override fun DelegatedMapView.contribute() {
      val onReady = onReady
      if (onReady != null) {
        onReady.invoke(this as MapView)
        this@MapViewInstanceInterceptorNode.onReady = null
      }
    }
  }

  override fun onDetach(instance: Contributor) {
    onReady = null
  }

  override fun update(contributor: Contributor) {}

  override fun hashCode(): Int = System.identityHashCode(this)
  override fun equals(other: Any?): Boolean = this === other
}

private fun unwrapAppCompat(map: MapView) = map.apply {
  val compassIcon = findViewById<ImageView>(NaverMapR.id.navermap_compass_icon)
  compassIcon.setImageResource(NaverMapR.drawable.navermap_compass)

  val locationIcon = findViewById<ImageView>(NaverMapR.id.navermap_location_icon)
  locationIcon.setImageResource(NaverMapR.drawable.navermap_location_none_normal)

  val zoomInIcon = findViewById<ImageView>(NaverMapR.id.navermap_zoom_in)
  zoomInIcon.setImageResource(NaverMapR.drawable.navermap_zoom_in)

  val zoomOutIcon = findViewById<ImageView>(NaverMapR.id.navermap_zoom_out)
  zoomOutIcon.setImageResource(NaverMapR.drawable.navermap_zoom_out)
}
