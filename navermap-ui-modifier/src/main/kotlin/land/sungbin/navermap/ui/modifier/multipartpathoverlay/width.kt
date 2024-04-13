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

package land.sungbin.navermap.ui.modifier.multipartpathoverlay

import androidx.compose.runtime.Stable
import land.sungbin.navermap.runtime.contributor.ContributionKind
import land.sungbin.navermap.runtime.contributor.Contributor
import land.sungbin.navermap.runtime.contributor.Contributors.Overlay
import land.sungbin.navermap.runtime.contributor.OverlayContributor
import land.sungbin.navermap.runtime.modifier.MapModifierContributionNode
import land.sungbin.navermap.ui.modifier.multipartpathoverlay.MultipartPathOverlayDelegate.Companion.NoOp

@Stable
private data class MultipartPathOverlayWidthModifierNode(
  private val arg0: Int,
  override var delegator: MultipartPathOverlayDelegate = NoOp,
) : MultipartPathOverlayModifier {
  override fun getContributionNode(): MapModifierContributionNode =
    MultipartPathOverlayWidthContributionNode(arg0, delegator)

  override fun <R : Any> fold(initial: R, operation: (R, MultipartPathOverlayModifier) -> R): R =
    operation(initial, this)
}

@Stable
private data class MultipartPathOverlayWidthContributionNode(
  public val arg0: Int,
  public val `delegate`: MultipartPathOverlayDelegate = NoOp,
) : MapModifierContributionNode {
  override val kindSet: ContributionKind = Overlay

  override fun create(): Contributor = MultipartPathOverlayWidthContributor(arg0, delegate)

  override fun update(contributor: Contributor) {
    require(contributor is MultipartPathOverlayWidthContributor)
    contributor.arg0 = arg0
    contributor.delegate = delegate
  }
}

private class MultipartPathOverlayWidthContributor(
  public var arg0: Int,
  public var `delegate`: MultipartPathOverlayDelegate,
) : OverlayContributor {
  override fun Any.contribute() {
    delegate.setWidth(this, arg0)
  }
}

/**
 * See
 * [official document](https://navermaps.github.io/android-map-sdk/reference/com/naver/maps/map/overlay/MultipartPathOverlay.html#setWidth(int))
 */
@Stable
public fun MultipartPathOverlayModifier.width(arg0: Int): MultipartPathOverlayModifier =
  this then MultipartPathOverlayWidthModifierNode(arg0)
