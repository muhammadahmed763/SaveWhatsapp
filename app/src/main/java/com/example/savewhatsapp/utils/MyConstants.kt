package com.example.savewhatsapp.utils

import androidx.lifecycle.MutableLiveData
import com.example.savewhatsapp.modals.StatusModel

object MyConstants {
    val list: ArrayList<StatusModel> by lazy { ArrayList() }
    var position:Int?=null
    var deleteItem:String?=null
    val mainActivityNativeObserver = MutableLiveData(false)
}