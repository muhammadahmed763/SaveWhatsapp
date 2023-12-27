package com.example.savewhatsapp.ui.businesswhatsapp

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import com.example.savewhatsapp.`interface`.ClickCallBack
import com.example.statussaverapplication.adapters.fragments.BusinessWhatsappViewPagerAdapter
import com.example.savewhatsapp.utils.ConstantVariables
import com.example.statussaverapplication.utils.SharedPrefObj
import com.google.android.material.tabs.TabLayoutMediator
import com.savewhatsapp.R
import com.savewhatsapp.databinding.FragmentBusinessWhataappBinding


class BusinessWhataappFragment : Fragment(), PopupMenu.OnMenuItemClickListener  {
    lateinit var binding: FragmentBusinessWhataappBinding
    private var check_Whatsapp_Click=false
    lateinit var clickCallBack: ClickCallBack
    private var check_Business_Whatsapp_Click=false
    override fun onAttach(context: Context) {
        super.onAttach(context)
        clickCallBack = context as? ClickCallBack ?: throw RuntimeException("$context must implement SampleCallback")
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentBusinessWhataappBinding.inflate(inflater, container, false)
        setupViewPager()
        tabLayoutSetup()
        whatsappSelection()
        return binding.root

    }
    private fun whatsappSelection(){
        check_Business_Whatsapp_Click=false
        check_Whatsapp_Click=false
        binding.whatsappSelectionButton.setOnClickListener {
            showPopupMenu(it)
        }
    }
    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(requireContext(), view)
        popupMenu.setOnMenuItemClickListener(this)
        popupMenu.inflate(R.menu.whatsapp_selection_menu)
        // Find the "First Button" MenuItem and set it as checked

        // Find the "First Button" MenuItem and set its state based on the saved click state
        val whatsappMenuButton = popupMenu.menu.findItem(R.id.menu_whatsapp_status)
        val whatsBusinessButton = popupMenu.menu.findItem(R.id.menu_whatsapp_business_status)
        if (SharedPrefObj.getToken(requireContext())== ConstantVariables.business_Whatsapp_Click){
            whatsBusinessButton.isChecked=true
            binding.imageViewWhatsappIcon.setPadding(3,3,3,3)
            binding.imageViewWhatsappIcon.setImageResource(R.drawable.whatsapp_business)
        }else{
            whatsappMenuButton.isChecked=true
            binding.imageViewWhatsappIcon.setImageResource(R.drawable.whatsapp_icon)
        }

        popupMenu.show()
    }
    override fun onDetach() {
        super.onDetach()
        clickCallBack
    }

    // Method to handle the menu item clicks
    override fun onMenuItemClick(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_whatsapp_status -> {
                // Handle first button click
                if (!check_Whatsapp_Click){
                    check_Whatsapp_Click=true
                    clickCallBack.onButtonClicked()
                    SharedPrefObj.clearData(requireContext())
                    SharedPrefObj.saveAuthToken(requireContext(), ConstantVariables.whatsapp_Click)
                }else{
                    check_Whatsapp_Click=false
                }
                return true
            }
            R.id.menu_whatsapp_business_status -> {
                // Handle second button click
                if (!check_Business_Whatsapp_Click){
                    clickCallBack.onButtonClicked()
                    check_Business_Whatsapp_Click=true
                    SharedPrefObj.clearData(requireContext())
                    SharedPrefObj.saveAuthToken(requireContext(), ConstantVariables.business_Whatsapp_Click)
                }else{
                    check_Business_Whatsapp_Click=false

                }
                return true
            }
            else -> return false
        }
    }
    private fun tabLayoutSetup(){
        val tabNames = listOf("IMAGES","VIDEOS")
        TabLayoutMediator(
            binding.tbLayout, binding.viewPager
        ) { tab, position -> tab.text = tabNames[position] }.attach()
    }
    private fun setupViewPager(){
        val adapter = BusinessWhatsappViewPagerAdapter(requireActivity(), 2)
        binding.viewPager.adapter = adapter

    }
}