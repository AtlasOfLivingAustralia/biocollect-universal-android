package au.org.ala.mobile.ozatlas.login

import android.app.Service
import android.content.Intent
import android.os.IBinder

class AlaAuthenticatorService : Service() {
    override fun onBind(intent: Intent?): IBinder = AlaAuthenticator(this).iBinder
}