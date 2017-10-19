package com.salman.firebasefindfriends.findfriends.rest_api;


import com.salman.firebasefindfriends.findfriends.pojo.EmailVerification;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface EmailRestApi {

    @GET("api/check")
    Call<EmailVerification> getEmailVerificationData(
            @Query("access_key") String apiKey,
            @Query("email") String email,
            @Query("smtp") int smtp,
            @Query("format") int format
    );

}
