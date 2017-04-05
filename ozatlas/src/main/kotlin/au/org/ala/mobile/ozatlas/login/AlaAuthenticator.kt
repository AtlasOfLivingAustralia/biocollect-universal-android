package au.org.ala.mobile.ozatlas.login

import android.accounts.AbstractAccountAuthenticator
import android.accounts.Account
import android.accounts.AccountAuthenticatorResponse
import android.accounts.AccountManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import au.org.ala.mobile.ozatlas.OzAtlasApp
import au.org.ala.mobile.ozatlas.R
import retrofit2.adapter.rxjava.HttpException
import rx.schedulers.Schedulers
import timber.log.Timber
import java.io.IOException

class AlaAuthenticator(private val context: Context) : AbstractAccountAuthenticator(context) {

    companion object {
        val HTTP_AUTHENTICATION_FAILED_CODES = setOf(401,403)
    }

    val mobileAuthClient = OzAtlasApp[context].component.alaMobileLoginClient()

    /**
     * Adds an account of the specified accountType.
     * @param response to send the result back to the AccountManager, will never be null
     * @param accountType the type of account to add, will never be null
     * @param authTokenType the type of auth token to retrieve after adding the account, may be null
     * @param requiredFeatures a String array of authenticator-specific features that the added
     * account must support, may be null
     * @param options a Bundle of authenticator-specific options, may be null
     * @return a Bundle result or null if the result is to be returned via the response. The result
     * will contain either:
     * <ul>
     * <li> {@link AccountManager#KEY_INTENT}, or
     * <li> {@link AccountManager#KEY_ACCOUNT_NAME} and {@link AccountManager#KEY_ACCOUNT_TYPE} of
     * the account that was added, or
     * <li> {@link AccountManager#KEY_ERROR_CODE} and {@link AccountManager#KEY_ERROR_MESSAGE} to
     * indicate an error
     * </ul>
     * @throws NetworkErrorException if the authenticator could not honor the request due to a
     * network error
     */
    override fun addAccount(response: AccountAuthenticatorResponse, accountType: String, authTokenType: String?, requiredFeatures: Array<out String>?, options: Bundle?) =
            Bundle().apply {
                putParcelable(AccountManager.KEY_INTENT, authenticatorActivityIntent(accountType, authTokenType, true, null, response))
            }

    /**
     * Checks that the user knows the credentials of an account.
     * @param response to send the result back to the AccountManager, will never be null
     * @param account the account whose credentials are to be checked, will never be null
     * @param options a Bundle of authenticator-specific options, may be null
     * @return a Bundle result or null if the result is to be returned via the response. The result
     * will contain either:
     * <ul>
     * <li> {@link AccountManager#KEY_INTENT}, or
     * <li> {@link AccountManager#KEY_BOOLEAN_RESULT}, true if the check succeeded, false otherwise
     * <li> {@link AccountManager#KEY_ERROR_CODE} and {@link AccountManager#KEY_ERROR_MESSAGE} to
     * indicate an error
     * </ul>
     * @throws NetworkErrorException if the authenticator could not honor the request due to a
     * network error
     */
    override fun confirmCredentials(response: AccountAuthenticatorResponse, account: Account, options: Bundle?): Bundle? {
        Timber.d("Confirm Credentials(%s, %s)", account.name)
        if (options != null && options.containsKey(AccountManager.KEY_PASSWORD)) {
            val password = options.getString(AccountManager.KEY_PASSWORD)
            mobileAuthClient.login(account.name, password).subscribeOn(Schedulers.io()).subscribe({ loginResponse ->
                when (loginResponse?.authKey) {
                    null -> response.onError(AccountManager.ERROR_CODE_INVALID_RESPONSE, context.getString(R.string.invalid_response))
                    else -> response.onResult(booleanBundle(true))
                }
            }, { throwable ->
                if (throwable is HttpException && HTTP_AUTHENTICATION_FAILED_CODES.contains(throwable.code())) {
                    response.onResult(booleanBundle(false))
                } else {
                    respondWithError(response, throwable)
                }
            })
            return null
        }
        return Bundle().apply {
            putParcelable(AccountManager.KEY_INTENT, authenticatorActivityIntent(account.type, null, false, account.name, response))
        }
    }

    /**
     * Returns a Bundle that contains the Intent of the activity that can be used to edit the
     * properties. In order to indicate success the activity should call response.setResult()
     * with a non-null Bundle.
     * @param response used to set the result for the request. If the Constants.INTENT_KEY
     *   is set in the bundle then this response field is to be used for sending future
     *   results if and when the Intent is started.
     * @param accountType the AccountType whose properties are to be edited.
     * @return a Bundle containing the result or the Intent to start to continue the request.
     *   If this is null then the request is considered to still be active and the result should
     *   sent later using response.
     */
    override fun editProperties(response: AccountAuthenticatorResponse, accountType: String): Bundle? = throw UnsupportedOperationException("editProperties")

    /**
     * Ask the authenticator for a localized label for the given authTokenType.
     * @param authTokenType the authTokenType whose label is to be returned, will never be null
     * @return the localized label of the auth token type, may be null if the type isn't known
     */
    override fun getAuthTokenLabel(authTokenType: String): String? = context.getString(R.string.auth_token_label)

    /**
     * Checks if the account supports all the specified authenticator specific features.
     * @param response to send the result back to the AccountManager, will never be null
     * @param account the account to check, will never be null
     * @param features an array of features to check, will never be null
     * @return a Bundle result or null if the result is to be returned via the response. The result
     * will contain either:
     * <ul>
     * <li> {@link AccountManager#KEY_INTENT}, or
     * <li> {@link AccountManager#KEY_BOOLEAN_RESULT}, true if the account has all the features,
     * false otherwise
     * <li> {@link AccountManager#KEY_ERROR_CODE} and {@link AccountManager#KEY_ERROR_MESSAGE} to
     * indicate an error
     * </ul>
     * @throws NetworkErrorException if the authenticator could not honor the request due to a
     * network error
     */
    override fun hasFeatures(response: AccountAuthenticatorResponse, account: Account, features: Array<out String>): Bundle? =
            Bundle().apply {
                putBoolean(AccountManager.KEY_BOOLEAN_RESULT, false)
            }

    /**
     * Update the locally stored credentials for an account.
     * @param response to send the result back to the AccountManager, will never be null
     * @param account the account whose credentials are to be updated, will never be null
     * @param authTokenType the type of auth token to retrieve after updating the credentials,
     * may be null
     * @param options a Bundle of authenticator-specific options, may be null
     * @return a Bundle result or null if the result is to be returned via the response. The result
     * will contain either:
     * <ul>
     * <li> {@link AccountManager#KEY_INTENT}, or
     * <li> {@link AccountManager#KEY_ACCOUNT_NAME} and {@link AccountManager#KEY_ACCOUNT_TYPE} of
     * the account whose credentials were updated, or
     * <li> {@link AccountManager#KEY_ERROR_CODE} and {@link AccountManager#KEY_ERROR_MESSAGE} to
     * indicate an error
     * </ul>
     * @throws NetworkErrorException if the authenticator could not honor the request due to a
     * network error
     */
    override fun updateCredentials(response: AccountAuthenticatorResponse, account: Account, authTokenType: String?, options: Bundle?): Bundle {
        Timber.d("Update Credentials(%s, %s)", account.name, authTokenType)
        return Bundle().apply {
            putParcelable(AccountManager.KEY_INTENT, authenticatorActivityIntent(account.type, authTokenType.orEmpty(), false, account.name, response))
        }
    }

    /**
     * Gets an authtoken for an account.
     *
     * If not {@code null}, the resultant {@link Bundle} will contain different sets of keys
     * depending on whether a token was successfully issued and, if not, whether one
     * could be issued via some {@link android.app.Activity}.
     * <p>
     * If a token cannot be provided without some additional activity, the Bundle should contain
     * {@link AccountManager#KEY_INTENT} with an associated {@link Intent}. On the other hand, if
     * there is no such activity, then a Bundle containing
     * {@link AccountManager#KEY_ERROR_CODE} and {@link AccountManager#KEY_ERROR_MESSAGE} should be
     * returned.
     * <p>
     * If a token can be successfully issued, the implementation should return the
     * {@link AccountManager#KEY_ACCOUNT_NAME} and {@link AccountManager#KEY_ACCOUNT_TYPE} of the
     * account associated with the token as well as the {@link AccountManager#KEY_AUTHTOKEN}. In
     * addition {@link AbstractAccountAuthenticator} implementations that declare themselves
     * {@code android:customTokens=true} may also provide a non-negative {@link
     * #KEY_CUSTOM_TOKEN_EXPIRY} long value containing the expiration timestamp of the expiration
     * time (in millis since the unix epoch).
     * <p>
     * Implementers should assume that tokens will be cached on the basis of account and
     * authTokenType. The system may ignore the contents of the supplied options Bundle when
     * determining to re-use a cached token. Furthermore, implementers should assume a supplied
     * expiration time will be treated as non-binding advice.
     * <p>
     * Finally, note that for android:customTokens=false authenticators, tokens are cached
     * indefinitely until some client calls {@link
     * AccountManager#invalidateAuthToken(String,String)}.
     *
     * @param response to send the result back to the AccountManager, will never be null
     * @param account the account whose credentials are to be retrieved, will never be null
     * @param authTokenType the type of auth token to retrieve, will never be null
     * @param options a Bundle of authenticator-specific options, may be null
     * @return a Bundle result or null if the result is to be returned via the response.
     * @throws NetworkErrorException if the authenticator could not honor the request due to a
     * network error
     */
    override fun getAuthToken(response: AccountAuthenticatorResponse, account: Account, authTokenType: String, options: Bundle?): Bundle? {
        // Extract the username and password from the Account Manager, and ask
        // the server for an appropriate AuthToken.
        val am = AccountManager.get(context)

        val authToken = am.peekAuthToken(account, authTokenType)

        // Lets give another try to authenticate the user
        if (TextUtils.isEmpty(authToken)) {
            val password = am.getPassword(account)
            if (password != null) {
                // We have a username and password, get a token asynchronously
                mobileAuthClient.login(account.name, password).subscribeOn(Schedulers.io()).subscribe({ loginResponse ->
                    when (loginResponse?.authKey) {
                        null -> response.onError(AccountManager.ERROR_CODE_INVALID_RESPONSE, context.getString(R.string.invalid_response))
                        else -> response.onResult(authTokenBundle(account, loginResponse.authKey))
                    }
                }, { throwable ->
                    respondWithError(response, throwable)
                })
                return null
            }
        }

        // If we have an authToken - we return it
        if (!TextUtils.isEmpty(authToken)) {
            return authTokenBundle(account, authToken)
        }

        // If we get here, then we couldn't access the user's password - so we
        // need to re-prompt them for their credentials. We do that by creating
        // an intent to display our AuthenticatorActivity.
        return Bundle().apply {
            putParcelable(AccountManager.KEY_INTENT, authenticatorActivityIntent(account.type, authTokenType, false, account.name, response))
        }
    }

    private fun authenticatorActivityIntent(accountType: String, authTokenType: String?, isNewAccount: Boolean, accountName: String?, response: AccountAuthenticatorResponse) =
            Intent(context, AlaAuthenticatorActivity::class.java).apply {
                putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response)
                putExtra(AlaAuthenticatorActivity.ARG_IS_ADDING_NEW_ACCOUNT, isNewAccount)
                putExtra(AlaAuthenticatorActivity.ARG_ACCOUNT_NAME, accountName)
                putExtra(AlaAuthenticatorActivity.ARG_ACCOUNT_TYPE, accountType)
                putExtra(AlaAuthenticatorActivity.ARG_AUTH_TYPE, authTokenType)
            }

    private fun authTokenBundle(account: Account, authToken: String) =
            Bundle().apply {
                putString(AccountManager.KEY_ACCOUNT_NAME, account.name)
                putString(AccountManager.KEY_ACCOUNT_TYPE, account.type)
                putString(AccountManager.KEY_AUTHTOKEN, authToken)
            }

    private fun booleanBundle(result: Boolean) = Bundle().apply { putBoolean(AccountManager.KEY_BOOLEAN_RESULT, result) }

    private fun respondWithError(response: AccountAuthenticatorResponse, throwable: Throwable) {
        val code = when (throwable) {
            is HttpException -> if (setOf(401, 403).contains(throwable.code())) AccountManager.ERROR_CODE_BAD_AUTHENTICATION else AccountManager.ERROR_CODE_REMOTE_EXCEPTION
            is IOException -> AccountManager.ERROR_CODE_NETWORK_ERROR
            else -> AccountManager.ERROR_CODE_INVALID_RESPONSE
        }
        Timber.d(throwable, "Couldn't authenticate")
        response.onError(code, throwable.message) // TODO use a better message
    }

}
