package au.org.ala.mobile.ozatlas.login

import android.app.Activity
import android.os.Bundle
import au.org.ala.mobile.ozatlas.dagger.AlaAuthenticatorActivityScope
import dagger.Module
import dagger.Provides

@Module()
class AlaAuthenticatorActivityModule(val activity: AlaAuthenticatorActivity, arguments: Bundle, savedInstance: Bundle?) {

    val viewModel : AlaAuthenticatorViewModel.VMParcel

    init {
        viewModel = savedInstance?.let { si ->
            si.getParcelable<AlaAuthenticatorViewModel.VMParcel>(AlaAuthenticatorActivity.RETAINED_VIEWMODEL)
        } ?: AlaAuthenticatorViewModel.VMParcel(arguments.getString(AlaAuthenticatorActivity.ARG_ACCOUNT_NAME).orEmpty(), "", null, null, false, false)
    }

    @Provides
    @AlaAuthenticatorActivityScope
    fun provideVmParcel() = viewModel

    @Provides
    @AlaAuthenticatorActivityScope
    fun provideVmCallbacks() : AlaAuthenticatorViewModel.VMCallbacks = activity

    @Provides
    @AlaAuthenticatorActivityScope
    fun provideActivityContext() : Activity = activity
}