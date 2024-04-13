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

package land.sungbin.navermap.ui.modifier.generator

import land.sungbin.navermap.ui.codegen.GeneratorContext
import land.sungbin.navermap.ui.codegen.ktDelegate
import land.sungbin.navermap.ui.codegen.ktRealDelegate
import land.sungbin.navermap.ui.modifier.generator.dummy.dummyOverlayResult
import kotlin.test.Test

class DelegateGeneratorRuntimeTest {
  @Test fun ktDelegate() {
    val context = GeneratorContext(
      packageName = "land.sungbin.navermap.ui.modifier.generator",
      overlayResult = dummyOverlayResult,
    )
    val delegate = ktDelegate(context)
    println(delegate)
  }

  @Test fun ktRealDelegate() {
    val context = GeneratorContext(
      packageName = "land.sungbin.navermap.ui.modifier.generator",
      overlayResult = dummyOverlayResult,
    )
    val delegate = ktRealDelegate(context)
    println(delegate)
  }
}