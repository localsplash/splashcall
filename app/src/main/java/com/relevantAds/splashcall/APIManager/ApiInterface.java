package com.relevantAds.splashcall.APIManager;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiInterface {

    /**
     * Device Login End Point
     * @param deviceUID
     * @param phoneNumber
     * @return
     */
    @GET("SplashCallOTP/DeviceLogin")
    Call<ResponseBody> deviceLoginEndPoint(@Query("deviceUID") String deviceUID, @Query("phoneNumber") long phoneNumber);

    @GET("SplashCallOTP/OTPSMSRequest")
    Call<ResponseBody> otpSmsRequest(@Query("deviceUID") String deviceUID, @Query("phoneNumber") long phoneNumber);

    @POST("SplashCallOTP/OTPSMSResponse")
    Call<ResponseBody> otpSmsResponse(@Query("deviceUID") String deviceUID, @Query("OTPcode") long otpCode);







}
