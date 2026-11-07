package id.heula.di

import id.heula.auth.WasmGoogleAuthProvider
import id.heula.domain.repository.CloudSyncService
import id.heula.domain.repository.GoogleAuthProvider
import id.heula.domain.repository.HealthSyncService
import id.heula.domain.repository.ShareExporter
import id.heula.health.WasmHealthSyncService
import id.heula.share.WasmShareExporter
import id.heula.sync.WasmCloudSyncService
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single<GoogleAuthProvider> { WasmGoogleAuthProvider() }
    single<CloudSyncService> { WasmCloudSyncService() }
    single<HealthSyncService> { WasmHealthSyncService() }
    single<ShareExporter> { WasmShareExporter() }
}
