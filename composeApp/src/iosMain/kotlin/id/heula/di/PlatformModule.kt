package id.heula.di

import id.heula.auth.IosGoogleAuthProvider
import id.heula.domain.repository.CloudSyncService
import id.heula.domain.repository.GoogleAuthProvider
import id.heula.domain.repository.HealthSyncService
import id.heula.domain.repository.ShareExporter
import id.heula.health.IosHealthSyncService
import id.heula.share.IosShareExporter
import id.heula.sync.IosCloudSyncService
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single<GoogleAuthProvider> { IosGoogleAuthProvider() }
    single<CloudSyncService> { IosCloudSyncService() }
    single<HealthSyncService> { IosHealthSyncService() }
    single<ShareExporter> { IosShareExporter() }
}
