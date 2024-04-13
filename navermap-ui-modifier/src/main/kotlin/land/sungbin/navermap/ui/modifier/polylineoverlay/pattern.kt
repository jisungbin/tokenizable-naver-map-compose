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

package land.sungbin.navermap.ui.modifier.polylineoverlay

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import kotlin.Any
import kotlin.Int
import land.sungbin.navermap.runtime.contributor.ContributionKind
import land.sungbin.navermap.runtime.contributor.Contributor
import land.sungbin.navermap.runtime.contributor.Contributors.Overlay
import land.sungbin.navermap.runtime.contributor.OverlayContributor
import land.sungbin.navermap.runtime.modifier.MapModifierContributionNode
import land.sungbin.navermap.ui.modifier.polylineoverlay.PolylineOverlayDelegate.Companion.NoOp

@Immutable
private data class PolylineOverlayPatternModifierNode(
  private val arg0: IntArray,
  override var delegator: PolylineOverlayDelegate = NoOp,
) : PolylineOverlayModifier {
  override fun getContributionNode(): MapModifierContributionNode =
    PolylineOverlayPatternContributionNode(arg0, delegator)

  override fun <R : Any> fold(initial: R, operation: (R, PolylineOverlayModifier) -> R): R =
    operation(initial, this)

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is PolylineOverlayPatternModifierNode) return false

    if (!arg0.contentEquals(other.arg0)) return false
    if (delegator != other.delegator) return false

    return true
  }

  override fun hashCode(): Int {
    var result = arg0.contentHashCode()
    result = 31 * result + delegator.hashCode()
    return result
  }
}

@Stable
private data class PolylineOverlayPatternContributionNode(
  public val arg0: IntArray,
  public val `delegate`: PolylineOverlayDelegate = NoOp,
) : MapModifierContributionNode {
  override val kindSet: ContributionKind = Overlay

  override fun create(): Contributor = PolylineOverlayPatternContributor(arg0, delegate)

  override fun update(contributor: Contributor) {
    require(contributor is PolylineOverlayPatternContributor)
    contributor.arg0 = arg0
    contributor.delegate = delegate
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is PolylineOverlayPatternContributionNode) return false

    if (!arg0.contentEquals(other.arg0)) return false
    if (`delegate` != other.`delegate`) return false

    return true
  }

  override fun hashCode(): Int {
    var result = arg0.contentHashCode()
    result = 31 * result + `delegate`.hashCode()
    return result
  }
}

private class PolylineOverlayPatternContributor(
  public var arg0: IntArray,
  public var `delegate`: PolylineOverlayDelegate,
) : OverlayContributor {
  override fun Any.contribute() {
    delegate.setPattern(this, arg0)
  }
}

@Stable
public fun PolylineOverlayModifier.pattern(arg0: IntArray): PolylineOverlayModifier =
  this then PolylineOverlayPatternModifierNode(arg0)
