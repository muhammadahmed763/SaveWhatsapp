package com.example.savewhatsapp.utils

import android.content.Context
import android.widget.Toast


fun Context.showToast(m:String){
    Toast.makeText(this, m, Toast.LENGTH_SHORT).show()
}