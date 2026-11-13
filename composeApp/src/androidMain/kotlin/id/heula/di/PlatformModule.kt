package id.heula.di

import id.heula.auth.AndroidGoogleAuthProvider
import id.heula.domain.repository.CloudSyncService
import id.heula.domain.repository.GoogleAuthProvider
import id.heula.domain.repository.HealthSyncService
import id.heula.domain.repository.ShareExporter
import id.heula.domain.repository.StravaService
import id.heula.health.AndroidHealthSyncService
import id.heula.share.AndroidShareExporter
import id.heula.strava.AndroidStravaService
import id.heula.sync.AndroidCloudSyncService
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single<GoogleAuthProvider> { AndroidGoogleAuthProvider(context = get()) }
    single<CloudSyncService> { AndroidCloudSyncService() }
    single<HealthSyncService> { AndroidHealthSyncService(context = get()) }
    single<ShareExporter> { AndroidShareExporter() }
    single<StravaService> { AndroidStravaService() }
}
