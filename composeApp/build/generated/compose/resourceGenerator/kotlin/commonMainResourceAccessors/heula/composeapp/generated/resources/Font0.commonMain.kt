@file:OptIn(org.jetbrains.compose.resources.InternalResourceApi::class)

package heula.composeapp.generated.resources

import kotlin.OptIn
import kotlin.String
import kotlin.collections.MutableMap
import org.jetbrains.compose.resources.FontResource
import org.jetbrains.compose.resources.InternalResourceApi

private object CommonMainFont0 {
  public val inter_bold: FontResource by 
      lazy { init_inter_bold() }

  public val inter_medium: FontResource by 
      lazy { init_inter_medium() }

  public val inter_regular: FontResource by 
      lazy { init_inter_regular() }

  public val inter_semibold: FontResource by 
      lazy { init_inter_semibold() }
}

@InternalResourceApi
internal fun _collectCommonMainFont0Resources(map: MutableMap<String, FontResource>) {
  map.put("inter_bold", CommonMainFont0.inter_bold)
  map.put("inter_medium", CommonMainFont0.inter_medium)
  map.put("inter_regular", CommonMainFont0.inter_regular)
  map.put("inter_semibold", CommonMainFont0.inter_semibold)
}

internal val Res.font.inter_bold: FontResource
  get() = CommonMainFont0.inter_bold

private fun init_inter_bold(): FontResource = org.jetbrains.compose.resources.FontResource(
  "font:inter_bold",
    setOf(
      org.jetbrains.compose.resources.ResourceItem(setOf(),
    "composeResources/heula.composeapp.generated.resources/font/inter_bold.ttf", -1, -1),
    )
)

internal val Res.font.inter_medium: FontResource
  get() = CommonMainFont0.inter_medium

private fun init_inter_medium(): FontResource = org.jetbrains.compose.resources.FontResource(
  "font:inter_medium",
    setOf(
      org.jetbrains.compose.resources.ResourceItem(setOf(),
    "composeResources/heula.composeapp.generated.resources/font/inter_medium.ttf", -1, -1),
    )
)

internal val Res.font.inter_regular: FontResource
  get() = CommonMainFont0.inter_regular

private fun init_inter_regular(): FontResource = org.jetbrains.compose.resources.FontResource(
  "font:inter_regular",
    setOf(
      org.jetbrains.compose.resources.ResourceItem(setOf(),
    "composeResources/heula.composeapp.generated.resources/font/inter_regular.ttf", -1, -1),
    )
)

internal val Res.font.inter_semibold: FontResource
  get() = CommonMainFont0.inter_semibold

private fun init_inter_semibold(): FontResource = org.jetbrains.compose.resources.FontResource(
  "font:inter_semibold",
    setOf(
      org.jetbrains.compose.resources.ResourceItem(setOf(),
    "composeResources/heula.composeapp.generated.resources/font/inter_semibold.ttf", -1, -1),
    )
)
