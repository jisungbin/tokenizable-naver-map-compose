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

package land.sungbin.navermap.ui.modifier.polylineoverlay

import androidx.compose.runtime.Stable
import com.naver.maps.map.overlay.PolylineOverlay
import land.sungbin.navermap.runtime.contributor.ContributionKind
import land.sungbin.navermap.runtime.contributor.Contributor
import land.sungbin.navermap.runtime.contributor.Contributors.Overlay
import land.sungbin.navermap.runtime.contributor.OverlayContributor
import land.sungbin.navermap.runtime.modifier.MapModifierContributionNode
import land.sungbin.navermap.ui.modifier.polylineoverlay.PolylineOverlayDelegate.Companion.NoOp

@Stable
private data class PolylineOverlayCapTypeModifierNode(
  private val arg0: PolylineOverlay.LineCap,
  override var delegator: PolylineOverlayDelegate = NoOp,
) : PolylineOverlayModifier {
  override fun getContributionNode(): MapModifierContributionNode =
    PolylineOverlayCapTypeContributionNode(arg0, delegator)

  override fun <R : Any> fold(initial: R, operation: (R, PolylineOverlayModifier) -> R): R =
    operation(initial, this)
}

@Stable
private data class PolylineOverlayCapTypeContributionNode(
  public val arg0: PolylineOverlay.LineCap,
  public val `delegate`: PolylineOverlayDelegate = NoOp,
) : MapModifierContributionNode {
  override val kindSet: ContributionKind = Overlay

  override fun create(): Contributor = PolylineOverlayCapTypeContributor(arg0, delegate)

  override fun update(contributor: Contributor) {
    require(contributor is PolylineOverlayCapTypeContributor)
    contributor.arg0 = arg0
    contributor.delegate = delegate
  }
}

private class PolylineOverlayCapTypeContributor(
  public var arg0: PolylineOverlay.LineCap,
  public var `delegate`: PolylineOverlayDelegate,
) : OverlayContributor {
  override fun Any.contribute() {
    delegate.setCapType(this, arg0)
  }
}

/**
 * See
 * [official document](https://navermaps.github.io/android-map-sdk/reference/com/naver/maps/map/overlay/PolylineOverlay.html#setCapType(com.naver.maps.map.overlay.PolylineOverlay.LineCap))
 */
@Stable
public fun PolylineOverlayModifier.capType(arg0: PolylineOverlay.LineCap): PolylineOverlayModifier =
  this then PolylineOverlayCapTypeModifierNode(arg0)
