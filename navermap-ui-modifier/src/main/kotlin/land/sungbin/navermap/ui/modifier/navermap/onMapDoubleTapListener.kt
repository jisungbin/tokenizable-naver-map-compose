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

@file:Suppress("RedundantVisibilityModifier")

package land.sungbin.navermap.ui.modifier.navermap

import androidx.compose.runtime.Stable
import com.naver.maps.map.NaverMap
import land.sungbin.navermap.runtime.contributor.ContributionKind
import land.sungbin.navermap.runtime.contributor.Contributor
import land.sungbin.navermap.runtime.contributor.Contributors.NaverMap
import land.sungbin.navermap.runtime.contributor.NaverMapContributor
import land.sungbin.navermap.runtime.modifier.MapModifierContributionNode
import land.sungbin.navermap.ui.modifier.navermap.NaverMapDelegate.Companion.NoOp

@Stable
private data class NaverMapOnMapDoubleTapListenerModifierNode(
  private val arg0: NaverMap.OnMapDoubleTapListener?,
  override var delegator: NaverMapDelegate = NoOp,
) : NaverMapModifier {
  override fun getContributionNode(): MapModifierContributionNode =
    NaverMapOnMapDoubleTapListenerContributionNode(arg0, delegator)

  override fun <R : Any> fold(initial: R, operation: (R, NaverMapModifier) -> R): R =
    operation(initial, this)
}

@Stable
private data class NaverMapOnMapDoubleTapListenerContributionNode(
  public val arg0: NaverMap.OnMapDoubleTapListener?,
  public val `delegate`: NaverMapDelegate = NoOp,
) : MapModifierContributionNode {
  override val kindSet: ContributionKind = NaverMap

  override fun create(): Contributor = NaverMapOnMapDoubleTapListenerContributor(arg0, delegate)

  override fun update(contributor: Contributor) {
    require(contributor is NaverMapOnMapDoubleTapListenerContributor)
    contributor.arg0 = arg0
    contributor.delegate = delegate
  }

  override fun onDetach(instance: Contributor) {
    require(instance is NaverMapOnMapDoubleTapListenerContributor)
    instance.clear?.invoke()
    instance.clear = null
  }
}

private class NaverMapOnMapDoubleTapListenerContributor(
  public var arg0: NaverMap.OnMapDoubleTapListener?,
  public var `delegate`: NaverMapDelegate,
) : NaverMapContributor {
  public var clear: (() -> Unit)? = null

  override fun Any.contribute() {
    delegate.setOnMapDoubleTapListener(this, arg0)
    clear = { delegate.setOnMapDoubleTapListener(this, null) }
  }
}

/**
 * See
 * [official document](https://navermaps.github.io/android-map-sdk/reference/com/naver/maps/map/NaverMap.html#setOnMapDoubleTapListener(com.naver.maps.map.NaverMap.OnMapDoubleTapListener))
 */
@Stable
public fun NaverMapModifier.onMapDoubleTapListener(arg0: NaverMap.OnMapDoubleTapListener?): NaverMapModifier = this then NaverMapOnMapDoubleTapListenerModifierNode(arg0)
