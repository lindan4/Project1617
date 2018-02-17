package com.flashitdelivery.flash_it_partner.util.api;

import com.flashitdelivery.flash_it_partner.models.remote.FCMDeviceBody;
import com.flashitdelivery.flash_it_partner.models.remote.FCMDeviceResponse;
import com.flashitdelivery.flash_it_partner.models.remote.TokenRespondBody;
import com.flashitdelivery.flash_it_partner.models.remote.UpdateDeliveryBody;
import com.flashitdelivery.flash_it_partner.models.remote.UpdateDeliveryResponse;
import com.flashitdelivery.flash_it_partner.models.remote.UsernameBody;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 * Created by yon on 16/06/16.
 */
public interface APIClient {
    @POST("fcm/devices/partner/")
    Call<FCMDeviceResponse> registerDevice(@Body FCMDeviceBody body);

    @POST("respond/")
    Call<ResponseBody> respondToken(@Body TokenRespondBody body);

    @POST("driver/start-shift/")
    Call<ResponseBody> startShift(@Body UsernameBody body);

    @PATCH
    Call<UpdateDeliveryResponse> updateDelivery(@Url String url, @Body UpdateDeliveryBody body);
}
