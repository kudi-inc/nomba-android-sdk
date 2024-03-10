package com.nomba.wraith.core.managers

import android.database.Observable
import com.nomba.wraith.core.api.APIClient
import com.nomba.wraith.core.api.models.fetchparentaccount.FetchParentAccountResponse
import com.nomba.wraith.core.api.services.APIService
import retrofit2.Response

class NetworkManager {

    private lateinit var accessToken: String

    init {

    }

    fun fetchAcount(accountId: String) : Observable<Response<FetchParentAccountResponse>>{
        return APIClient.apiService.getparentBankAcount()
    }

}