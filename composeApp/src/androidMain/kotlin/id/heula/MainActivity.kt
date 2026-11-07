package id.heula

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.microsoft.clarity.Clarity
import com.microsoft.clarity.ClarityConfig
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

        try {
            Clarity.initialize(applicationContext, ClarityConfig("za57e1ucqq"))
        } catch (_: Throwable) {}

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
