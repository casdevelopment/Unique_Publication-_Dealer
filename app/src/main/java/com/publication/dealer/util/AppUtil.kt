package com.publication.dealer.util

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.Window

import com.publication.dealer.R
import com.publication.dealer.network.retofit.APIError
import com.publication.dealer.util.AppConstants.progressLoader

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

import okhttp3.ResponseBody

object AppUtil {
    var  notificationType :String=""
    /*var mutableLiveDataNotification : MutableLiveData<String>?=null*/
    fun startLoader(activity: Activity) {
        stopLoader()
        if (!activity.isFinishing) {
            progressLoader = progressDialog(activity)
        }
    }

    fun stopLoader() {
        if (progressLoader != null) {
            progressLoader!!.dismiss()

        }
    }

    fun progressDialog(context: Context): Dialog? {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.loader)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(false)
        dialog.show()
        return dialog
    }

    fun setApiErrorResponse(response: ResponseBody?, code: Int = 200): String {
        val gson = Gson()
        val type = object : TypeToken<APIError?>() {}.type
        val errorResponse: APIError
        if (response != null) {
            if (code == 401) {
                return "Unauthenticated"
            }
            return try {
                errorResponse = gson.fromJson(response.charStream(), type)
                errorResponse.message
            }catch (e:Exception){
                Log.v("setApiErrorResponse","Exception "+ e.message.toString())
                "Server not Responding"
            }

        }
        return "Server not Responding"
    }



}