package au.org.ala.mobile.ozatlas.login

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.content.pm.PackageManager
import android.support.design.widget.Snackbar
import android.app.LoaderManager.LoaderCallbacks

import android.content.CursorLoader
import android.content.Loader
import android.database.Cursor
import android.net.Uri
import android.os.AsyncTask

import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.view.View

import java.util.ArrayList

import au.org.ala.mobile.ozatlas.R

import android.Manifest.permission.READ_CONTACTS
import android.accounts.Account
import android.accounts.AccountAuthenticatorActivity
import android.accounts.AccountManager
import android.databinding.DataBindingComponent
import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatCallback
import android.support.v7.app.AppCompatDelegate
import android.support.v7.view.ActionMode
import android.text.TextUtils
import android.view.inputmethod.EditorInfo
import android.widget.*
import au.org.ala.mobile.ozatlas.OzAtlasApp
import au.org.ala.mobile.ozatlas.databinding.ActivityAlaAuthenticatorBinding
import au.org.ala.mobile.ozatlas.ui.BindingAdapters
import kotlinx.android.synthetic.main.activity_ala_authenticator.*
import rx.Observable
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject
import kotlin.LazyThreadSafetyMode.NONE

/**
 * A login screen that offers login via email/password.
 */
class AlaAuthenticatorActivity : AccountAuthenticatorActivity(), LoaderCallbacks<Cursor>, AppCompatCallback, AlaAuthenticatorViewModel.VMCallbacks {

    private val delegate: AppCompatDelegate by lazy(NONE) { AppCompatDelegate.create(this, this) }

    // UI references.
    lateinit private var mEmailView: AutoCompleteTextView
    lateinit private var mPasswordView: EditText
    lateinit private var mProgressView: ProgressBar
    lateinit private var mLoginFormView: ScrollView
    lateinit private var mEmailSignInButton: Button

    private var cacheObservable : Observable<LoginResponse>? = null
    private var subscription : Subscription? = null

    @Inject
    lateinit var client: AlaMobileLoginClient

//    init {
//        cacheObservable?.let { observable ->
//            doLogin(observable, data.username, data.password)
//        }
//    }

//    val dbc = object : DataBindingComponent {
//        override fun getCompanion(): BindingAdapters.Companion = BindingAdapters.Companion
//
//    }

//    @Inject
//    lateinit var viewModel: AlaAuthenticatorViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.d("AlaAuthenticatorActivity onCreate")
        super.onCreate(savedInstanceState)
        delegate.onCreate(savedInstanceState)
        delegate.setContentView(R.layout.activity_ala_authenticator)

        val graph = DaggerAlaAuthenticatorActivityComponent.builder()
                .ozAtlasComponent(OzAtlasApp[this].component)
                .alaAuthenticatorActivityModule(AlaAuthenticatorActivityModule(this, intent.extras, savedInstanceState))
                .build()
        graph.inject(this)

        cacheObservable = lastNonConfigurationInstance?.let { instance -> if (instance is Map<*, *>) instance[OBSERVABLE_KEY] as? Observable<LoginResponse> else null }
        cacheObservable?.let { observable ->
            doLogin(observable, emailValue, passwordValue)
        }

//        DataBindingUtil.setContentView(this, R.layout.activity_ala_authenticator)
//        val binding = ActivityAlaAuthenticatorBinding.inflate(layoutInflater, dbc)
//        binding.setVm(this.viewModel)

        // Set up the login form.
//        mEmailView = binding.email
//        mEmailView.error

        mEmailView = email
        mPasswordView = password
        mProgressView = login_progress
        mLoginFormView = login_form
        mEmailSignInButton = email_sign_in_button

        populateAutoComplete()

//        mPasswordView = binding.password
        // TODO
        mPasswordView.setOnEditorActionListener(TextView.OnEditorActionListener { textView, id, keyEvent ->
            if (id == R.id.login || id == EditorInfo.IME_NULL) {
                attemptLogin()
                return@OnEditorActionListener true
            }
            false
        })

//        val mEmailSignInButton = binding.emailSignInButton
        // TODO
        mEmailSignInButton.setOnClickListener { attemptLogin() }

//        mLoginFormView = binding.loginForm
//        mProgressView = binding.loginProgress
    }

    private fun populateAutoComplete() {
        if (!mayRequestContacts()) {
            return
        }

        loaderManager.initLoader(0, null, this)
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun mayRequestContacts(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView!!, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE).setAction(android.R.string.ok, View.OnClickListener { requestPermissions(arrayOf(READ_CONTACTS), REQUEST_READ_CONTACTS) })
        } else {
            requestPermissions(arrayOf(READ_CONTACTS), REQUEST_READ_CONTACTS)
        }
        return false
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete()
            }
        }
    }

    // TODO
//    private fun attemptLoginOnEnter(textView: TextView, id: Int, keyEvent: KeyEvent ): Boolean {
//        if (id == R.id.login || id == EditorInfo.IME_NULL) {
//            attemptLogin()
//            return true
//        }
//        return false
//    }

    private val emailValue: String get() = mEmailView.text.toString()
    private val passwordValue: String get() = mPasswordView.text.toString()

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
        mEmailView.error = null
        mPasswordView.error = null

        // Store values at the time of the login attempt.
        val email = mEmailView.text.toString()
        val password = mPasswordView.text.toString()

        var cancel = false
//        var focusView: View? = null

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.error = getString(R.string.error_invalid_password)
            onPasswordValidationFailed()
            cancel = true
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.error = getString(R.string.error_field_required)
            onEmailValidationFailed()
            cancel = true
        } else if (!isEmailValid(email)) {
            mEmailView.error = getString(R.string.error_invalid_email)
            onEmailValidationFailed()
            cancel = true
        }

        if (!cancel) {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true)
            // Cache so we can retain the observable across config change and get the result even if
            // it arrives while we're not subscribed
            val observable = client.login(email, password).cache()
            cacheObservable = observable

            doLogin(observable, email, password)
        }
    }

    private fun doLogin(observable: Observable<LoginResponse>, email: String, password: String) {
        subscription = observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ loginResponse ->

                    val authKey = loginResponse.authKey



                    onAuthenticationSuceeded(email, password, loginResponse.authKey)
                    showProgress(false)

                }, { throwable ->

                    Timber.e(throwable, "Couldn't log in")
                    Toast.makeText(applicationContext, "An unexpected error occured while logging in", Toast.LENGTH_SHORT).show()
                    cacheObservable = null
                    subscription = null
                    showProgress(false)

                }, {

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

    override fun onEmailValidationFailed() {
        mEmailView.requestFocus()
    }

    override fun onPasswordValidationFailed() {
        mPasswordView.requestFocus()
    }

    override fun onAuthenticationSuceeded(username: String, password: String, authToken: String) {


//        val accountName = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME)
//        val accountPassword = intent.getStringExtra(PARAM_USER_PASS)
        val accountType = intent.getStringExtra(ARG_ACCOUNT_TYPE)
        val authTokenType = intent.getStringExtra(ARG_AUTH_TYPE)
        val account = Account(username, accountType)

        val am = AccountManager.get(this)
        if (intent.getBooleanExtra(ARG_IS_ADDING_NEW_ACCOUNT, false)) {

            Timber.d("onAuthenticationSuceeded > addAccountExplicitly")

            // Creating the account on the device and setting the auth token we got
            // (Not setting the auth token will cause another call to the server to authenticate the user)
            am.addAccountExplicitly(account, password, null)
            am.setAuthToken(account, authTokenType, authToken)
        } else {
            am.setPassword(account, password)
            am.setAuthToken(account, authTokenType, authToken)
        }

        setAccountAuthenticatorResult(Bundle().apply {
            putString(AccountManager.KEY_ACCOUNT_NAME, username)
            putString(AccountManager.KEY_PASSWORD, password)
            putString(AccountManager.KEY_AUTHTOKEN, authToken)
        })
        finish()
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    private fun showProgress(show: Boolean) {
        val shortAnimTime = resources.getInteger(android.R.integer.config_shortAnimTime)

        mLoginFormView.visibility = if (show) View.GONE else View.VISIBLE
        mLoginFormView.animate().setDuration(shortAnimTime.toLong()).alpha(
                (if (show) 0 else 1).toFloat()).setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                mLoginFormView.visibility = if (show) View.GONE else View.VISIBLE
            }
        })

        mProgressView.visibility = if (show) View.VISIBLE else View.GONE
        mProgressView.animate().setDuration(shortAnimTime.toLong()).alpha(
                (if (show) 1 else 0).toFloat()).setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                mProgressView.visibility = if (show) View.VISIBLE else View.GONE
            }
        })
    }

    override fun onCreateLoader(i: Int, bundle: Bundle?): Loader<Cursor> {
        return CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE + " = ?", arrayOf(ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE),

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC")
    }

    override fun onLoadFinished(cursorLoader: Loader<Cursor>, cursor: Cursor) {
        val emails = ArrayList<String>()
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS))
            cursor.moveToNext()
        }

        addEmailsToAutoComplete(emails)
    }

    override fun onLoaderReset(cursorLoader: Loader<Cursor>) {

    }

    private fun addEmailsToAutoComplete(emailAddressCollection: List<String>) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        val adapter = ArrayAdapter(this@AlaAuthenticatorActivity,
                android.R.layout.simple_dropdown_item_1line, emailAddressCollection)

        mEmailView.setAdapter(adapter)
    }

    // AppCompatCallback

    override fun onWindowStartingSupportActionMode(callback: ActionMode.Callback): ActionMode? = null

    override fun onSupportActionModeStarted(mode: ActionMode) {}

    override fun onSupportActionModeFinished(mode: ActionMode) {}


    private interface ProfileQuery {
        companion object {
            val PROJECTION = arrayOf(ContactsContract.CommonDataKinds.Email.ADDRESS, ContactsContract.CommonDataKinds.Email.IS_PRIMARY)

            val ADDRESS = 0
            val IS_PRIMARY = 1
        }
    }

    override fun onRetainNonConfigurationInstance(): Any {
//        return viewModel.onRetain()
        return mapOf(OBSERVABLE_KEY to cacheObservable)

    }

    override fun onDestroy() {
//        viewModel.onDestroy()
        subscription?.unsubscribe()
        subscription = null

        super.onDestroy()
    }

    companion object {

        /**
         * Id to identity READ_CONTACTS permission request.
         */
        private val REQUEST_READ_CONTACTS = 0

        const val OBSERVABLE_KEY = "ALA_AUTHENTICATOR_VM_OBSERVABLE"

        val ARG_ACCOUNT_TYPE = "ALA_ACCOUNT_TYPE"
        val ARG_AUTH_TYPE = "ALA_AUTH_TYPE"
        val ARG_IS_ADDING_NEW_ACCOUNT = "ALA_IS_ADDING_NEW_ACCOUNT"
        val ARG_ACCOUNT_NAME = "ALA_ACCOUNT_NAME"
        val RETAINED_VIEWMODEL = "ALA_AUTHENTICATOR_RETAINED_VIEWMODEL"
    }
}

