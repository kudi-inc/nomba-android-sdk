package com.nomba.wraith.core.managers

import android.util.Log
import com.nomba.wraith.core.api.APIClient
import com.nomba.wraith.core.api.models.accesstoken.AccessTokenRequest
import com.nomba.wraith.core.api.models.accesstoken.AccessTokenResponse
import com.nomba.wraith.core.api.models.createorder.CreateOrderRequest
import com.nomba.wraith.core.api.models.createorder.CreateOrderResponse
import com.nomba.wraith.core.api.models.fetchparentaccount.FetchParentAccountResponse
import com.nomba.wraith.core.api.models.flashaccount.FlashAccountResponse
import com.nomba.wraith.core.api.models.submitcard.SubmitCardDetailsRequest
import com.nomba.wraith.core.api.models.submitcard.SubmitCardDetailsResponse
import com.nomba.wraith.core.api.models.submitotp.SubmitOTPRequest
import com.nomba.wraith.core.api.models.submitotp.SubmitOTPResponse
import com.nomba.wraith.core.api.models.transationstatus.CheckTransactionStatusRequest
import com.nomba.wraith.core.api.models.transationstatus.CheckTransactionStatusResponse
import com.nomba.wraith.core.enums.PaymentOption
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NetworkManager {

    private lateinit var accessToken: String
    private lateinit var refreshToken: String

    init {

    }

    //first call to server, if accessToken is not initialised, fetch access token and save it
    //if initialised, just make a trip with the account instead
    fun getAccessToken(accountId: String, clientId: String, clientKey: String, selectedPaymentOption : PaymentOption, onAccessTokenGottenFun: (selectedPaymentOption : PaymentOption) -> Unit,) {
        if (this::accessToken.isInitialized || this::refreshToken.isInitialized){
            onAccessTokenGottenFun(selectedPaymentOption)
        } else {
            APIClient.apiService.obtainAccessToken(accountId = accountId, accessTokenRequest = AccessTokenRequest(grant_type = "client_credentials",
                client_id = clientId,
                client_secret = clientKey)).enqueue(object : Callback<AccessTokenResponse> {
                override fun onResponse(call: Call<AccessTokenResponse>, response: Response<AccessTokenResponse>) {
                    if (response.isSuccessful) {
                        val post = response.body()
                        accessToken = post!!.data.access_token
                        refreshToken = post.data.refresh_token
                        Log.e("Success", "Gotten Token")
                        onAccessTokenGottenFun(selectedPaymentOption)
                        // Handle the retrieved post data
                    } else {
                        Log.e("Error", "Error Access Token")
                        Log.e("Error", response.toString())
                        Log.e("Error", response.code().toString())
                        // Handle error
                        accessToken = ""
                        refreshToken = ""
                    }
                }
                override fun onFailure(call: Call<AccessTokenResponse>, t: Throwable) {
                    // Handle failure

                }
            })
        }
    }

    fun fetchAccount(accountId: String) : Call<FetchParentAccountResponse> {
        return APIClient.apiService.getParentBankAccount(authorization = accessToken, accountId = accountId)
    }

    fun getFlashAccount(orderReference: String) : Call<FlashAccountResponse> {
        return APIClient.apiService.getFlashBankAccount(accessToken, orderReference)
    }

    fun createOrder(accountId: String, createOrderRequest: CreateOrderRequest) : Call<CreateOrderResponse> {
        return APIClient.apiService.createAnOrder(accountId, accessToken, createOrderRequest)
    }

    fun checkTransactionOrderStatus(checkTransactionStatusRequest: CheckTransactionStatusRequest) : Call<CheckTransactionStatusResponse> {
        return APIClient.apiService.checkTransactionOrderStatus(accessToken, checkTransactionStatusRequest)
    }

    fun submitCardDetails(submitCardDetailsRequest: SubmitCardDetailsRequest) : Call<SubmitCardDetailsResponse> {
        return APIClient.apiService.submitCardDetails(accessToken, submitCardDetailsRequest)
    }

    fun submitOTPDetails(submitOTPRequest: SubmitOTPRequest) : Call<SubmitOTPResponse> {
        return APIClient.apiService.submitOTPDetails(accessToken, submitOTPRequest)
    }

}