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

package land.sungbin.navermap.ui.modifier.marker

import androidx.compose.runtime.Stable
import land.sungbin.navermap.runtime.contributor.ContributionKind
import land.sungbin.navermap.runtime.contributor.Contributor
import land.sungbin.navermap.runtime.contributor.Contributors.Overlay
import land.sungbin.navermap.runtime.contributor.OverlayContributor
import land.sungbin.navermap.runtime.modifier.MapModifierContributionNode
import land.sungbin.navermap.ui.modifier.marker.MarkerDelegate.Companion.NoOp

@Stable
private data class MarkerVisibleModifierNode(
  private val arg0: Boolean,
  override var delegator: MarkerDelegate = NoOp,
) : MarkerModifier {
  override fun getContributionNode(): MapModifierContributionNode =
    MarkerVisibleContributionNode(arg0, delegator)

  override fun <R : Any> fold(initial: R, operation: (R, MarkerModifier) -> R): R =
    operation(initial, this)
}

@Stable
private data class MarkerVisibleContributionNode(
  public val arg0: Boolean,
  public val `delegate`: MarkerDelegate = NoOp,
) : MapModifierContributionNode {
  override val kindSet: ContributionKind = Overlay

  override fun create(): Contributor = MarkerVisibleContributor(arg0, delegate)

  override fun update(contributor: Contributor) {
    require(contributor is MarkerVisibleContributor)
    contributor.arg0 = arg0
    contributor.delegate = delegate
  }
}

private class MarkerVisibleContributor(
  public var arg0: Boolean,
  public var `delegate`: MarkerDelegate,
) : OverlayContributor {
  override fun Any.contribute() {
    delegate.setVisible(this, arg0)
  }
}

/**
 * See
 * [official document](https://navermaps.github.io/android-map-sdk/reference/com/naver/maps/map/overlay/Overlay.html#setVisible(boolean))
 */
@Stable
public fun MarkerModifier.visible(arg0: Boolean): MarkerModifier =
  this then MarkerVisibleModifierNode(arg0)
