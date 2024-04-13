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

package land.sungbin.navermap.ui.modifier.viewportoverlay

import androidx.compose.runtime.Stable
import land.sungbin.navermap.runtime.contributor.ContributionKind
import land.sungbin.navermap.runtime.contributor.Contributor
import land.sungbin.navermap.runtime.contributor.Contributors.Overlay
import land.sungbin.navermap.runtime.contributor.OverlayContributor
import land.sungbin.navermap.runtime.modifier.MapModifierContributionNode
import land.sungbin.navermap.ui.modifier.viewportoverlay.ViewportOverlayDelegate.Companion.NoOp

@Stable
private data class ViewportOverlayMaxZoomInclusiveModifierNode(
  private val arg0: Boolean,
  override var delegator: ViewportOverlayDelegate = NoOp,
) : ViewportOverlayModifier {
  override fun getContributionNode(): MapModifierContributionNode =
    ViewportOverlayMaxZoomInclusiveContributionNode(arg0, delegator)

  override fun <R : Any> fold(initial: R, operation: (R, ViewportOverlayModifier) -> R): R =
    operation(initial, this)
}

@Stable
private data class ViewportOverlayMaxZoomInclusiveContributionNode(
  public val arg0: Boolean,
  public val `delegate`: ViewportOverlayDelegate = NoOp,
) : MapModifierContributionNode {
  override val kindSet: ContributionKind = Overlay

  override fun create(): Contributor = ViewportOverlayMaxZoomInclusiveContributor(arg0, delegate)

  override fun update(contributor: Contributor) {
    require(contributor is ViewportOverlayMaxZoomInclusiveContributor)
    contributor.arg0 = arg0
    contributor.delegate = delegate
  }
}

private class ViewportOverlayMaxZoomInclusiveContributor(
  public var arg0: Boolean,
  public var `delegate`: ViewportOverlayDelegate,
) : OverlayContributor {
  override fun Any.contribute() {
    delegate.setMaxZoomInclusive(this, arg0)
  }
}

/**
 * See
 * [official document](https://navermaps.github.io/android-map-sdk/reference/com/naver/maps/map/overlay/Overlay.html#setMaxZoomInclusive(boolean))
 */
@Stable
public fun ViewportOverlayModifier.maxZoomInclusive(arg0: Boolean): ViewportOverlayModifier =
  this then ViewportOverlayMaxZoomInclusiveModifierNode(arg0)
