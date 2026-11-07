package id.heula.di

import id.heula.auth.IosGoogleAuthProvider
import id.heula.domain.repository.GoogleAuthProvider
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single<GoogleAuthProvider> { IosGoogleAuthProvider() }
}
