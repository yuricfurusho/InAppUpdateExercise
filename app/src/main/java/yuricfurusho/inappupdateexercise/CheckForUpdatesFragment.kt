package yuricfurusho.inappupdateexercise

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.ActivityResult
import com.google.android.play.core.install.model.InstallStatus
import dagger.hilt.android.AndroidEntryPoint
import yuricfurusho.inappupdateexercise.databinding.CheckForUpdatesFragmentBinding
import javax.inject.Inject

@AndroidEntryPoint
class CheckForUpdatesFragment : Fragment(), InstallStateUpdatedListener {

    private lateinit var binding: CheckForUpdatesFragmentBinding
    private val vm by viewModels<CheckForUpdatesViewModel>()

    @Inject
    lateinit var appUpdateManager: AppUpdateManager

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == APP_UPDATE_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    //  handle user's approval
                }
                Activity.RESULT_CANCELED -> {
                    //  handle user's rejection
                }
                ActivityResult.RESULT_IN_APP_UPDATE_FAILED -> {
                    //  handle update failure
                }
            }
        }
    }

    override fun onStateUpdate(installState: InstallState?) {
        if (installState?.installStatus() == InstallStatus.DOWNLOADED) {
            notifyUser()
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.check_for_updates_fragment,
                container,
                false
        )
        binding.vm = vm
        binding.lifecycleOwner = viewLifecycleOwner

        initObservers()

        return binding.root
    }

    private fun initObservers() {
        vm.startAppUpdateEvent.observe { (appUpdateInfo, appUpdateType) ->
            appUpdateManager.registerListener(this)
            appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    appUpdateType,
                    requireActivity(),
                    APP_UPDATE_REQUEST_CODE
            )
        }
        vm.notifyUserToInstallEvent.observe {
            notifyUser()
        }
    }

    override fun onResume() {
        super.onResume()
        vm.resumeUpdateInProgress()
    }

    private fun notifyUser() {
        Snackbar
                .make(binding.root, R.string.restart_to_update, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.action_restart) {
                    appUpdateManager.completeUpdate()
                    appUpdateManager.unregisterListener(this)
                }
                .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        appUpdateManager.unregisterListener(this)
    }

    companion object {
        const val APP_UPDATE_REQUEST_CODE = 1001
    }

    private fun <T> LiveData<T>.observe(
            owner: LifecycleOwner = viewLifecycleOwner,
            onChanged: (T) -> Unit
    ) = observe(owner, Observer(onChanged))
}
