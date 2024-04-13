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

package land.sungbin.navermap.ui.modifier.generator.dummy

import com.squareup.kotlinpoet.ClassName
import land.sungbin.navermap.ui.modifier.generator.parse.OverlayClass

val dummyOverlayResult = OverlayClass(
  name = ClassName("my.map.service", "Marker"),
  setters = listOf(
    OverlayClass.Method(
      name = "position",
      parameters = mapOf(
        "position" to ClassName("my.map.service", "LatLng"),
        "position2" to ClassName("my.map.service", "LatLng2"),
        "position3" to ClassName("my.map.service", "LatLng3"),
        "position4" to ClassName("my.map.service", "LatLng4"),
      ).toList(),
      javadocLink = "https://my.javadoc.link/position",
    ),
    OverlayClass.Method(
      name = "icon",
      parameters = mapOf(
        "icon" to ClassName("my.icon.service", "Icon").copy(nullable = true) as ClassName,
        "icon2" to ClassName("my.icon.service", "Icon2").copy(nullable = true) as ClassName,
        "icon3" to ClassName("my.icon.service", "Icon3").copy(nullable = true) as ClassName,
      ).toList(),
      javadocLink = "https://my.javadoc.link/icon",
    ),
  ),
)
