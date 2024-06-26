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

package land.sungbin.navermap.runtime.delegate

import land.sungbin.navermap.runtime.node.DelegatedNaverMap

public interface NaverMapDelegator : Delegator {
  override val instance: DelegatedNaverMap

  public companion object {
    @Suppress("NOTHING_TO_INLINE")
    public inline operator fun invoke(map: DelegatedNaverMap): NaverMapDelegator =
      object : NaverMapDelegator {
        override val instance: DelegatedNaverMap = map
      }
  }
}
