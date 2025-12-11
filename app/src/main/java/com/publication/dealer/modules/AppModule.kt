package com.publication.dealer.modules

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import com.publication.dealer.SessionManager
import com.publication.dealer.create_user.viewmodel.SignUpViewModel
import com.publication.dealer.image_upload.viewmodel.UploadImageViewModel
import com.publication.dealer.inactivate_user.viewmodel.InactivateUserViewModel
import com.publication.dealer.user_dashboard.viewmodel.DashBoardViewModel
import com.publication.dealer.login.viewmodel.LoginViewModel
import com.publication.dealer.network.repo.Repository
import com.publication.dealer.network.retofit.provideOkHttpClient
import com.publication.dealer.network.retofit.provideRetrofit
import com.publication.dealer.network.retofit.provideRetrofitInterface
import com.publication.dealer.reset_password.view_model.ResetPasswordViewModel
import com.publication.dealer.update_user_password.viewmodel.UpdateUserPasswordViewModel
import com.publication.dealer.update_user_profile.viewmodel.UpdateUserViewModel
import com.publication.dealer.util.AppConstants.SHARED_PREF_NAME
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val repoModule = module {
    single {
        Repository(get())
    }
}

val viewModelModule= module{
    viewModel { LoginViewModel(get()) }
    viewModel { DashBoardViewModel(get()) }
    viewModel { SignUpViewModel(get()) }
    viewModel { ResetPasswordViewModel(get()) }
    viewModel { InactivateUserViewModel(get()) }
    viewModel { UpdateUserViewModel(get()) }
    viewModel { UploadImageViewModel(get()) }
    viewModel { UpdateUserPasswordViewModel(get()) }
    }

val networkModule = module {
    factory { provideOkHttpClient(get()) }
    factory { provideRetrofitInterface(get()) }
    single { provideRetrofit(get()) }
}

val sharedPreferenceModule = module {
    single {
        provideSharedPreference(get())
    }
    single {
        provideEditor(get())
    }
    single {
        SessionManager(get())
    }
}

fun provideSharedPreference(appContext: Context): SharedPreferences {
    return appContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
}

fun provideEditor(sharedPreferences: SharedPreferences): Editor {
    return sharedPreferences.edit()
}
