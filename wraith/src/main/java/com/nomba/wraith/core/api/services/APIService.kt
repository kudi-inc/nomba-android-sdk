package com.nomba.wraith.core.api.services

import android.database.Observable
import com.nomba.wraith.core.api.models.accesstoken.AccessTokenRequest
import com.nomba.wraith.core.api.models.accesstoken.AccessTokenResponse
import com.nomba.wraith.core.api.models.fetchbanks.FetchBanksResponse
import com.nomba.wraith.core.api.models.fetchparentaccount.FetchParentAccountResponse
import com.nomba.wraith.core.api.models.refreshtoken.RefreshTokenRequest
import com.nomba.wraith.core.api.models.refreshtoken.RefreshTokenResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface APIService {

    @POST("/v1/auth/token/issue")
    fun obtainAccessToken(@Header("accountId") accountId : String,  @Body accessTokenRequest: AccessTokenRequest): Call<AccessTokenResponse>

    @POST("/v1/auth/token/refresh")
    fun refreshAccessToken(@Header("accountId") accountId : String,  @Body refreshTokenRequest: RefreshTokenRequest): Call<RefreshTokenResponse>

    @GET("/v1/transfers/banks")
    fun getBanks(@Header("accountId") accountId : String, @Header("Authorization") authorization : String) : Call<FetchBanksResponse>

    @GET("/v1/accounts/parent")
    fun getparentBankAcount(@Header("accountId") accountId : String, @Header("Authorization") authorization : String) : Call<FetchParentAccountResponse>

}