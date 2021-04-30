package yuricfurusho.inappupdateexercise

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.play.core.appupdate.AppUpdateInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CheckForUpdatesViewModel @Inject constructor(
        private val appUpdateUseCase: AppUpdateUseCase
) : ViewModel() {

    val appVersionName = BuildConfig.VERSION_NAME
    val appVersionCode = "${BuildConfig.VERSION_CODE}"

//    private val _randomNumber: MutableStateFlow<Int> = MutableStateFlow(23)
//    val randomNumber = _randomNumber.asLiveData(viewModelScope.coroutineContext)

    private val _updateIsAvailable: MutableStateFlow<String> = MutableStateFlow("unknown")
    val updateIsAvailable = _updateIsAvailable.asLiveData(viewModelScope.coroutineContext)

    private val _daysSinceLastUpdate: MutableStateFlow<String> = MutableStateFlow("unknown")
    val daysSinceLastUpdate = _daysSinceLastUpdate.asLiveData(viewModelScope.coroutineContext)

    private val _lastVersionCodeAvailable: MutableStateFlow<String> = MutableStateFlow("unknown")
    val lastVersionCodeAvailable = _lastVersionCodeAvailable.asLiveData(viewModelScope.coroutineContext)

    private val _startAppUpdateEvent = MutableSharedFlow<Pair<AppUpdateInfo, Int>>(
            replay = 0,
            extraBufferCapacity = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val startAppUpdateEvent = _startAppUpdateEvent.asLiveData(viewModelScope.coroutineContext)

    private val _notifyUserToInstallEvent = MutableSharedFlow<Unit>(
            replay = 0,
            extraBufferCapacity = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val notifyUserToInstallEvent = _notifyUserToInstallEvent.asLiveData(viewModelScope.coroutineContext)

//    fun generateRandomNumber() {
//        _randomNumber.value = (55..100).random()
//    }

    fun checkForUpdates() {
        checkUpdateAvailability()
        getLatestVersionCodeAvailable()
        getDaysSinceLastUpdate()
    }

    private fun checkUpdateAvailability() {
        viewModelScope.launch {
            _updateIsAvailable.value = appUpdateUseCase.isUpdateAvailable()
        }
    }

    private fun getLatestVersionCodeAvailable() {
        viewModelScope.launch {
            _lastVersionCodeAvailable.value = appUpdateUseCase.getLatestVersionCodeAvailable()
        }
    }

    private fun getDaysSinceLastUpdate() {
        viewModelScope.launch {
            _daysSinceLastUpdate.value = appUpdateUseCase.getStalenessDays()
        }
    }

    fun startImmediateAppUpdate() {
        viewModelScope.launch {
            _startAppUpdateEvent.emit(appUpdateUseCase.startImmediateUpdate())
        }
    }

    fun startFlexibleAppUpdate() {
        viewModelScope.launch {
            _startAppUpdateEvent.emit(appUpdateUseCase.startFlexibleUpdate())
        }
    }

    fun resumeUpdateInProgress() {
        viewModelScope.launch {
            if (appUpdateUseCase.isDownloadedAndNotInstalled()) {
                _notifyUserToInstallEvent.emit(Unit)
            }
        }
    }
}

