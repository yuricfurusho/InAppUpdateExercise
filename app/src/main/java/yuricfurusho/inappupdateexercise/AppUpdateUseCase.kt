package yuricfurusho.inappupdateexercise

import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.install.model.AppUpdateType.FLEXIBLE
import com.google.android.play.core.install.model.AppUpdateType.IMMEDIATE
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
import com.google.android.play.core.install.model.UpdateAvailability.UPDATE_AVAILABLE
import com.google.android.play.core.tasks.Task
import javax.inject.Inject
import kotlin.coroutines.suspendCoroutine

class AppUpdateUseCase @Inject constructor(
        private val appUpdateManager: AppUpdateManager
) {
    private val appUpdateInfoTask: Task<AppUpdateInfo> = appUpdateManager.appUpdateInfo
    private val lastUpdateTypeTriggered: Int? = null

    suspend fun isUpdateAvailable(): String = suspendCoroutine { continuation ->
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            val isUpdateAvailable = appUpdateInfo.updateAvailability() == UPDATE_AVAILABLE && (
                    appUpdateInfo.isUpdateTypeAllowed(IMMEDIATE) ||
                            appUpdateInfo.isUpdateTypeAllowed(FLEXIBLE)
                    )
            continuation.resumeWith(Result.success(
                    "$isUpdateAvailable"
            ))
        }
    }

    suspend fun getLatestVersionCodeAvailable(): String = suspendCoroutine { continuation ->
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            val isUpdateAvailable = appUpdateInfo.updateAvailability() == UPDATE_AVAILABLE
            val latestAvailableVersionCode = if (isUpdateAvailable) {
                "${appUpdateInfo.availableVersionCode()}"
            } else {
                "${BuildConfig.VERSION_CODE}"
            }
            continuation.resumeWith(Result.success(
                    latestAvailableVersionCode
            ))
        }
    }

    suspend fun isImmediateUpdateAllowed(): Boolean = suspendCoroutine { continuation ->
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            val isImmediateUpdateAllowed = appUpdateInfo.updateAvailability() == UPDATE_AVAILABLE &&
                    appUpdateInfo.isUpdateTypeAllowed(IMMEDIATE)
            continuation.resumeWith(Result.success(
                    isImmediateUpdateAllowed
            ))
        }
    }

    suspend fun isFlexibleUpdateAllowed(): Boolean = suspendCoroutine { continuation ->
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            val isFlexibleUpdateAllowed = appUpdateInfo.updateAvailability() == UPDATE_AVAILABLE &&
                    appUpdateInfo.isUpdateTypeAllowed(FLEXIBLE)
            continuation.resumeWith(Result.success(
                    isFlexibleUpdateAllowed
            ))
        }
    }

    suspend fun getStalenessDays(): Int = suspendCoroutine { continuation ->
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            continuation.resumeWith(Result.success(
                    appUpdateInfo.clientVersionStalenessDays()
            ))
        }
    }

    suspend fun getUpdatePriority(): Int = suspendCoroutine { continuation ->
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            continuation.resumeWith(Result.success(
                    appUpdateInfo.updatePriority())
            )
        }
    }

    private suspend fun getAppUpdateInfo(): AppUpdateInfo = suspendCoroutine { continuation ->
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            continuation.resumeWith(Result.success(
                    appUpdateInfo
            ))
        }
    }

    suspend fun startImmediateUpdate(): Pair<AppUpdateInfo, Int> = (getAppUpdateInfo() to IMMEDIATE)

    suspend fun startFlexibleUpdate(): Pair<AppUpdateInfo, Int> = (getAppUpdateInfo() to FLEXIBLE)

    suspend fun isUpdateInProgress(): Boolean = suspendCoroutine { continuation ->
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            val isUpdateInProgress = appUpdateInfo.updateAvailability() == DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
            continuation.resumeWith(Result.success(
                    isUpdateInProgress
            ))
        }
    }

    suspend fun isDownloadedAndNotInstalled(): Boolean = suspendCoroutine { continuation ->
        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            val isDownloadedAndNotInstalled = appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED
            continuation.resumeWith(Result.success(
                    isDownloadedAndNotInstalled
            ))
        }
    }
}
