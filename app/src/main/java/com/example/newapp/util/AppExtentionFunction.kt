package com.example.newapp.util

import android.app.Activity
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment


fun Activity.showToast(msg: String) {
    Toast.makeText(this,msg, Toast.LENGTH_SHORT).show()


}
fun Activity.showLongToast(msg: String) {
    Toast.makeText(this,msg, Toast.LENGTH_SHORT).show()


}

fun Fragment.showToast(msg: String) {
    Toast.makeText(requireContext(),msg, Toast.LENGTH_SHORT).show()
}

fun Fragment.showLongToast(msg: String) {
    Toast.makeText(requireContext(),msg, Toast.LENGTH_SHORT).show()
}

