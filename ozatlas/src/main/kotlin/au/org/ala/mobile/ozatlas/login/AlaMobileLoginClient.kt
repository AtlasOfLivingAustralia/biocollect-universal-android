package au.org.ala.mobile.ozatlas.login

import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query
import rx.Observable

interface AlaMobileLoginClient {

    @POST("mobileKey/generateKey")
    fun login(@Query("userName") userName: String, @Query("password") password: String): Observable<LoginResponse>

    @POST("mobileKey/checkKey")
    fun checkKey(@Header("userName") userName: String, @Header("authKey") authKey: String): Observable<Void>
}

data class LoginResponse(val authKey: String)