package yuricfurusho.inappupdateexercise

import androidx.lifecycle.ViewModel

class CheckForUpdatesViewModel : ViewModel() {
    val appVersion = BuildConfig.VERSION_NAME
    val buildNumber = "${BuildConfig.VERSION_CODE}"

    fun checkForUpdates() {
        TODO()
    }
}

