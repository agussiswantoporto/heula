@file:OptIn(org.jetbrains.compose.resources.InternalResourceApi::class)

package heula.composeapp.generated.resources

import kotlin.OptIn
import kotlin.String
import kotlin.collections.MutableMap
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.InternalResourceApi

private object CommonMainDrawable0 {
  public val ic_logo: DrawableResource by 
      lazy { init_ic_logo() }

  public val ic_splash_logo: DrawableResource by 
      lazy { init_ic_splash_logo() }
}

@InternalResourceApi
internal fun _collectCommonMainDrawable0Resources(map: MutableMap<String, DrawableResource>) {
  map.put("ic_logo", CommonMainDrawable0.ic_logo)
  map.put("ic_splash_logo", CommonMainDrawable0.ic_splash_logo)
}

internal val Res.drawable.ic_logo: DrawableResource
  get() = CommonMainDrawable0.ic_logo

private fun init_ic_logo(): DrawableResource = org.jetbrains.compose.resources.DrawableResource(
  "drawable:ic_logo",
    setOf(
      org.jetbrains.compose.resources.ResourceItem(setOf(),
    "composeResources/heula.composeapp.generated.resources/drawable/ic_logo.png", -1, -1),
    )
)

internal val Res.drawable.ic_splash_logo: DrawableResource
  get() = CommonMainDrawable0.ic_splash_logo

private fun init_ic_splash_logo(): DrawableResource =
    org.jetbrains.compose.resources.DrawableResource(
  "drawable:ic_splash_logo",
    setOf(
      org.jetbrains.compose.resources.ResourceItem(setOf(),
    "composeResources/heula.composeapp.generated.resources/drawable/ic_splash_logo.png", -1, -1),
    )
)
