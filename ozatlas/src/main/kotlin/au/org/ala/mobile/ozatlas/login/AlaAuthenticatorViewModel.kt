package au.org.ala.mobile.ozatlas.login

import android.app.Activity
import android.databinding.BaseObservable
import android.databinding.Bindable
import android.os.Parcel
import android.os.Parcelable
import android.text.TextUtils
import au.org.ala.mobile.ozatlas.BR
import au.org.ala.mobile.ozatlas.R
import au.org.ala.mobile.ozatlas.util.bindable
import rx.Observable
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import timber.log.Timber
import java.lang.ref.WeakReference
import javax.inject.Inject

class AlaAuthenticatorViewModel @Inject constructor(
        val data: VMParcel,
        callbacks: VMCallbacks,
        context: Activity,
        private val client: AlaMobileLoginClient) {

    private val mCallbacks : WeakReference<VMCallbacks?> = WeakReference(callbacks)
    private val mContext = context.applicationContext

    private var cacheObservable : Observable<LoginResponse>? = context.lastNonConfigurationInstance?.let { instance -> if (instance is Map<*, *>) instance[OBSERVABLE_KEY] as? Observable<LoginResponse> else null }
    private var subscription : Subscription? = null

    init {
        cacheObservable?.let { observable ->
            doLogin(observable, data.username, data.password)
        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    fun attemptLogin() {
        Timber.d("attempting login")
        if (subscription != null) {
            return
        }

        // Reset errors.
        data.usernameError = null
        data.passwordError = null

        // Store values at the time of the login attempt.
        val email = data.username
        val password = data.password

        var cancel = false
//        var focusView: View? = null

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            data.passwordError = mContext.getString(R.string.error_invalid_password)
            mCallbacks.get()?.onPasswordValidationFailed()
            cancel = true
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            data.usernameError = mContext.getString(R.string.error_field_required)
            mCallbacks.get()?.onEmailValidationFailed()
            cancel = true
        } else if (!isEmailValid(email)) {
            data.usernameError = mContext.getString(R.string.error_invalid_email)
            mCallbacks.get()?.onEmailValidationFailed()
            cancel = true
        }

        if (!cancel) {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            data.loading = true
            // Cache so we can retain the observable across config change and get the result even if
            // it arrives while we're not subscribed
            val observable = client.login(email, password).cache()
            cacheObservable = observable

            doLogin(observable, email, password)
        }
    }

    private fun doLogin(observable: Observable<LoginResponse>, email: String, password: String) {
        subscription = observable
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ loginResponse ->

                    data.authKey = loginResponse.authKey
                    mCallbacks.get()?.onAuthenticationSuceeded(email, password, loginResponse.authKey)

                }, { throwable ->

                    data.loadingError = true

                }, {
                    cacheObservable = null
                    subscription = null
                    data.loading = false
                })
    }

    private fun isEmailValid(email: String): Boolean {
        //TODO: Check logic
        return email.contains("@")
    }

    private fun isPasswordValid(password: String): Boolean {
        //TODO: Check logic
        return password.length > 4
    }

    fun onRetain() : Map<String, Any?> {
        return mapOf(OBSERVABLE_KEY to cacheObservable)
    }

    fun onDestroy() {
        subscription?.unsubscribe()
        subscription = null

    }

    companion object {
        private const val OBSERVABLE_KEY = "ALA_AUTHENTICATOR_VM_OBSERVABLE"
    }

    class VMParcel(
            initialUsername: String,
            initialPassword: String,
            initialUsernameError: String? = null,
            initialPasswordError: String? = null,
            initialLoading: Boolean = false,
            initialLoadingError: Boolean = false,
            initialAuthKey: String? = null) : BaseObservable(), Parcelable {

        @get:Bindable var username: String by bindable(initialUsername, BR.username)
        @get:Bindable var usernameError: String? by bindable(initialUsernameError, BR.usernameError)
        @get:Bindable var password: String by bindable(initialPassword, BR.password)
        @get:Bindable var passwordError: String? by bindable(initialPasswordError, BR.passwordError)
        @get:Bindable var loading: Boolean by bindable(initialLoading, BR.loading)
        @get:Bindable var loadingError: Boolean by bindable(initialLoadingError, BR.loadingError)
        @get:Bindable var authKey: String? by bindable(initialAuthKey, BR.authKey)


        override fun writeToParcel(dest: Parcel, flags: Int) {
            dest.writeString(username)
            dest.writeString(password)
            dest.writeString(usernameError)
            dest.writeString(passwordError)
            dest.writeByte(loading.asByte())
            dest.writeByte(loadingError.asByte())
        }

        override fun describeContents(): Int  = 0

        companion object {
            @JvmStatic
            val CREATOR = object : Parcelable.Creator<VMParcel> {
                override fun createFromParcel(source: Parcel): VMParcel {
                    val username = source.readString()
                    val password = source.readString()
                    val usernameError = source.readString()
                    val passwordError = source.readString()
                    val loading =  source.readByte() > 0
                    val error =  source.readByte() > 0
                    return VMParcel(username, password, usernameError, passwordError, loading, error)
                }

                override fun newArray(size: Int): Array<out VMParcel?> = kotlin.arrayOfNulls<VMParcel>(size)

            }
        }

        fun Boolean.asByte() : Byte = if (this) 1 else 0

    }

    /** Callbacks for the view to allow the controller to take actions not supported by Android
     * databinding (eg set focus)
     */
    interface VMCallbacks {

        fun onEmailValidationFailed()
        fun onPasswordValidationFailed()
        fun onAuthenticationSuceeded(username: String, password: String, authToken: String)
    }

}
