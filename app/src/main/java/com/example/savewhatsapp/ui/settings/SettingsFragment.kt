package com.example.savewhatsapp.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.savewhatsapp.utils.ConstantVariables
import com.savewhatsapp.BuildConfig
import com.savewhatsapp.R
import com.savewhatsapp.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {
    lateinit var  binding: FragmentSettingsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate te layout for this fragment
        binding=FragmentSettingsBinding.inflate(inflater,container,false)
        binding.shareApp.click.setOnClickListener {
            shareApp()
        }
//        binding.rate.setOnClickListener {
//            rateApp()
//        }
        binding.privacyPolicy.click.setOnClickListener {
            val url = ConstantVariables.Privacy_Policy
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }
        return binding.root
    }
    private fun shareApp() {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(
            Intent.EXTRA_TEXT,
            getString(R.string.hey_check_out_my_app_at) + " https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID
        )
        sendIntent.type = "text/plain"
        startActivity(sendIntent)
    }

}