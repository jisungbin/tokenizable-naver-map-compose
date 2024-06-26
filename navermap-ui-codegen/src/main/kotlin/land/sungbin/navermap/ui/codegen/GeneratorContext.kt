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

package land.sungbin.navermap.ui.codegen

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.MemberName.Companion.member
import com.squareup.kotlinpoet.buildCodeBlock
import land.sungbin.navermap.ui.codegen.parser.NaverMapClass

@Suppress("ClassName")
internal sealed interface NameFlag {
  sealed interface NoName

  data object NOOP : NameFlag, NoName
  data object COMBINED : NameFlag, NoName
  data object DELEGATE : NameFlag, NoName
  data object REAL_DELEGATE : NameFlag, NoName
  data object MODIFIER : NameFlag, NoName
  data object MODIFIER_NODE : NameFlag
  data object MODIFIER_EXTENSION : NameFlag
  data object MAP_MODIFIER_MAPPER : NameFlag, NoName
  data object CONTRIBUTOR : NameFlag
  data object CONTRIBUTION_NODE : NameFlag
  data object COMPOSITION_LOCAL : NameFlag, NoName
  data object CONTENT_COMPOSABLE_FILE : NameFlag, NoName
}

internal class GeneratorContext(
  val packageName: String,
  val clazz: ClassName,
  val constructors: List<NaverMapClass.Method>,
  val methods: List<NaverMapClass.Method>,
) {
  constructor(packageName: String, overlayResult: NaverMapClass) :
    this(
      packageName = packageName,
      clazz = overlayResult.name,
      constructors = overlayResult.constructors,
      methods = overlayResult.setters,
    )

  fun name(flag: NameFlag.NoName): String {
    require(flag is NameFlag)
    return normalizeName("", flag)
  }

  fun normalizeName(name: String, flag: NameFlag) =
    when (flag) {
      NameFlag.NOOP -> "NoOp" // MarkerDelegate.Companion.[NoOp]
      NameFlag.DELEGATE -> "${clazz.simpleName}Delegate" // MarkerDelegate
      NameFlag.REAL_DELEGATE -> "Real${clazz.simpleName}Delegate" // RealMarkerDelegate
      NameFlag.COMBINED -> "Combined${clazz.simpleName}Modifier" // CombinedOverlayModifier
      NameFlag.MODIFIER -> "${clazz.simpleName}Modifier" // OverlayModifier
      NameFlag.MODIFIER_NODE -> "${clazz.simpleName}${name.normalizeUppercase()}ModifierNode" // MarkerLatLngModifierNode
      NameFlag.MAP_MODIFIER_MAPPER -> "toMapModifier"
      NameFlag.MODIFIER_EXTENSION -> name.normalizeLowercase() // MarkerModifier.[offset]
      NameFlag.CONTRIBUTOR -> "${clazz.simpleName}${name.normalizeUppercase()}Contributor" // MarkerLatLngContributor
      NameFlag.CONTRIBUTION_NODE -> "${clazz.simpleName}${name.normalizeUppercase()}ContributionNode" // MarkerLatLngContributionNode
      NameFlag.COMPOSITION_LOCAL -> "Local${clazz.simpleName}Delegator" // LocalMarkerDelegator
      NameFlag.CONTENT_COMPOSABLE_FILE -> "NaverMap${clazz.simpleName}" // NaverMapMarker
    }

  fun noopDelegator() = buildCodeBlock {
    val delegator = ClassName(packageName, name(NameFlag.DELEGATE))
    addStatement("%M", delegator.nestedClass("Companion").member(name(NameFlag.NOOP)))
  }
}

private fun String.normalizeUppercase() = removePrefix("set").replaceFirstChar(Char::uppercaseChar)
private fun String.normalizeLowercase() = removePrefix("set").replaceFirstChar(Char::lowercaseChar)
