package id.heula

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import id.heula.di.appModule
import id.heula.presentation.navigation.App
import id.heula.presentation.store.AppStore
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.mp.KoinPlatformTools

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(android.R.style.Theme_Material_NoActionBar)
        super.onCreate(savedInstanceState)

        if (KoinPlatformTools.defaultContext().getOrNull() == null) {
            startKoin {
                androidContext(this@MainActivity)
                modules(appModule)
            }
        }

        val store = KoinPlatformTools.defaultContext().get().get<AppStore>()

        setContent {
            App(store = store, enableSplashScreen = true)
        }
    }
}
