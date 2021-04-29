package yuricfurusho.inappupdateexercise

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import yuricfurusho.inappupdateexercise.databinding.CheckForUpdatesFragmentBinding

class CheckForUpdatesFragment : Fragment() {

    private lateinit var binding: CheckForUpdatesFragmentBinding
    private lateinit var viewModel: CheckForUpdatesViewModel

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
        viewModel = ViewModelProvider(this).get(CheckForUpdatesViewModel::class.java)
        binding.vm = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }
}
