package com.publication.dealer.util

import android.app.Dialog
import com.publication.dealer.login.model.LoginResponseModel


object AppConstants {


    var URL = "https://pubdealersapi.cyberasol.com/"



    var BASE_URL = URL
    var ASSET_BASE_URL = URL + "/"



    var AUTH_TOKEN = ""
    var Bearer = "Bearer"
    const val SHARED_PREF_NAME = "publication_shared_prefs"

    var progressLoader: Dialog? = null

    var userData: LoginResponseModel? = null

}