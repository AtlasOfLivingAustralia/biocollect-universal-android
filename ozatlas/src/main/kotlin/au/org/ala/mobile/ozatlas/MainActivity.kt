package au.org.ala.mobile.ozatlas

import android.accounts.Account
import android.accounts.AccountManager
import android.accounts.AccountManagerCallback
import android.accounts.AccountManagerFuture
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import au.org.ala.mobile.ozatlas.biocollect.SightingSyncAdapter
import au.org.ala.mobile.ozatlas.login.AlaAuthenticator
import com.roughike.bottombar.BottomBar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import timber.log.Timber

class MainActivity : AppCompatActivity(), SightingListFragment.OnListFragmentInteractionListener {

    lateinit var account: Account

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTheme(R.style.AppTheme)
        setContentView(R.layout.activity_main)

        supportActionBar!!.elevation = 0f

//        main_bottom_tab_bar.setOnNavigationItemSelectedListener { menuItem ->
//            val fragment = when(menuItem.itemId) {
//                R.id.tab_explore -> { Timber.i("Switching to SightingFragment"); SightingFragment() }
//                R.id.tab_sightings -> { Timber.i("Switching to SightingFragment"); SightingFragment() }
//                R.id.tab_browse -> { Timber.i("Switching to SightingFragment"); SightingFragment() }
//                else -> throw IllegalStateException("Menu Item $menuItem not implemented")
//            }
//
//            supportFragmentManager.beginTransaction().replace(R.id.main_tab_fragment, fragment).commit()
//            true
//        }
//
//        main_bottom_tab_bar.menu.getItem(0).isChecked = true

        main_bottom_tab_bar.setOnTabSelectListener { tabId ->
            val fragment = when(tabId) {
                R.id.tab_explore -> { Timber.i("Switching to SightingFragment"); SightingFragment() }
                R.id.tab_sightings -> { Timber.i("Switching to SightingFragment"); SightingFragment() }
                R.id.tab_browse -> { Timber.i("Switching to SightingFragment"); SightingFragment() }
                else -> throw IllegalStateException("Tab ID $tabId not implemented")
            }

            if (fragment != null) {
                supportFragmentManager.beginTransaction().replace(R.id.main_tab_fragment, fragment).commit()
            }
        }

        main_bottom_tab_bar.selectTabAtPosition(0)

        ensureLoggedIn()
    }

    private fun ensureLoggedIn(created: Boolean = false) {
        val accountType = getString(R.string.ala_account_type)
        val am = AccountManager.get(applicationContext)
        val accounts = am.getAccountsByType(accountType)
        if (accounts.size == 0) {
            val future = am.addAccount(accountType, "", null, null, this, object : AccountManagerCallback<Bundle> {
                override fun run(future: AccountManagerFuture<Bundle>) {
                    try {
                        val bnd = future.getResult()
//                        showMessage("Account was created")

                        Timber.d("AddNewAccount Bundle is " + bnd)
                        runOnUiThread {
                            ensureLoggedIn(true)
                        }

                    } catch (e: Exception) {
                        Timber.e(e)
//                        showMessage(e.message)
                        runOnUiThread {
                            Toast.makeText(applicationContext, "Couldn't add ALA account", Toast.LENGTH_LONG).show()
                            finish()
                        }
                    }

                }
            }, null)
        } else {
            account = accounts[0]
            if (created) {
                SightingSyncAdapter.onAccountCreated(account, applicationContext)
            } else {
                SightingSyncAdapter.syncImmediately(applicationContext)
            }
        }
    }

    override fun onSightingSelected(sightingUri: Uri) {
        Timber.e("Sighting selected but I don't control them :(")
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.action_about -> true // TODO About Activity
            R.id.action_contact -> true // TODO Contact Activity
            R.id.action_settings -> true // TODO Settings Activity
            else -> super.onOptionsItemSelected(item)
        }
    }
}
