package com.nomba.wraith.core.api.services

import com.nomba.wraith.core.api.models.accesstoken.AccessTokenRequest
import com.nomba.wraith.core.api.models.accesstoken.AccessTokenResponse
import com.nomba.wraith.core.api.models.createorder.CreateOrderRequest
import com.nomba.wraith.core.api.models.createorder.CreateOrderResponse
import com.nomba.wraith.core.api.models.fetchbanks.FetchBanksResponse
import com.nomba.wraith.core.api.models.fetchparentaccount.FetchParentAccountResponse
import com.nomba.wraith.core.api.models.flashaccount.FlashAccountResponse
import com.nomba.wraith.core.api.models.refreshtoken.RefreshTokenRequest
import com.nomba.wraith.core.api.models.refreshtoken.RefreshTokenResponse
import com.nomba.wraith.core.api.models.savecard.SaveCardOtpRequest
import com.nomba.wraith.core.api.models.savecard.SaveCardOtpResponse
import com.nomba.wraith.core.api.models.savecardsubmitotp.SaveCardSubmitOtpRequest
import com.nomba.wraith.core.api.models.savecardsubmitotp.SaveCardSubmitOtpResponse
import com.nomba.wraith.core.api.models.submitcard.SubmitCardDetailsRequest
import com.nomba.wraith.core.api.models.submitcard.SubmitCardDetailsResponse
import com.nomba.wraith.core.api.models.submitotp.SubmitOTPRequest
import com.nomba.wraith.core.api.models.submitotp.SubmitOTPResponse
import com.nomba.wraith.core.api.models.transationstatus.CheckTransactionStatusRequest
import com.nomba.wraith.core.api.models.transationstatus.CheckTransactionStatusResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface APIService {

    @GET("/v1/transfers/banks")
    fun getBanks(@Header("accountId") accountId : String, @Header("Authorization") authorization : String) : Call<FetchBanksResponse>

    @GET("/v1/accounts/parent")
    fun getParentBankAccount(@Header("accountId") accountId : String, @Header("Authorization") authorization : String) : Call<FetchParentAccountResponse>

    @GET("/v1/checkout/get-checkout-kta/{orderReference}")
    fun getFlashBankAccount( @Path("orderReference") orderReference : String) : Call<FlashAccountResponse>

    @GET("/v1/transactions/accounts")
    fun getAccountTransactions(@Header("accountId") accountId : String) : Call<FetchParentAccountResponse>

    @POST("/v1/checkout/order")
    fun  createAnOrder(@Header("accountId") accountId : String, @Header("public_key") clientId : String, @Body createOrderRequest: CreateOrderRequest) : Call<CreateOrderResponse>


    @POST("/v1/checkout/confirm-transaction-receipt")
    fun checkTransactionOrderStatus( @Body checkTransactionStatusRequest: CheckTransactionStatusRequest) :Call<CheckTransactionStatusResponse>

    @POST("/v1/checkout/checkout-card-detail")
    fun submitCardDetails( @Body submitCardDetailsRequest: SubmitCardDetailsRequest) : Call<SubmitCardDetailsResponse>

    @POST("/v1/checkout/checkout-card-otp")
    fun submitOTPDetails( @Body submitOTPRequest: SubmitOTPRequest) : Call<SubmitOTPResponse>

    @POST("/v1/checkout/user-card/auth")
    fun requestOTPForCardSaving( @Body saveCardOtpRequest: SaveCardOtpRequest) : Call<SaveCardOtpResponse>

    @POST("/v1/checkout/user-card")
    fun submitOTPForCardSaving( @Body saveCardSubmitOTPRequest: SaveCardSubmitOtpRequest) : Call<SaveCardSubmitOtpResponse>

    @POST("/v1/checkout/checkout-card-otp")
    fun cancelCheckout(@Body submitOTPRequest: SubmitOTPRequest) : Call<SubmitOTPResponse>
}