package id.heula

import androidx.compose.ui.window.ComposeUIViewController
import id.heula.di.appModule
import id.heula.presentation.navigation.App
import id.heula.presentation.store.AppStore
import org.koin.core.context.startKoin
import org.koin.mp.KoinPlatformTools

fun MainViewController() = ComposeUIViewController {
    if (KoinPlatformTools.defaultContext().getOrNull() == null) {
        startKoin {
            modules(appModule)
        }
    }
    val store = KoinPlatformTools.defaultContext().get().get<AppStore>()
    App(store = store)
}
