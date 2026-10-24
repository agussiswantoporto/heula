package id.heula

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import id.heula.di.appModule
import id.heula.presentation.navigation.App
import id.heula.presentation.store.AppStore
import kotlinx.browser.document
import org.koin.core.context.startKoin
import org.koin.mp.KoinPlatformTools

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    if (KoinPlatformTools.defaultContext().getOrNull() == null) {
        startKoin {
            modules(appModule)
        }
    }
    val store = KoinPlatformTools.defaultContext().get().get<AppStore>()

    ComposeViewport(document.body!!) {
        App(store = store, enableSplashScreen = false)
    }
}
