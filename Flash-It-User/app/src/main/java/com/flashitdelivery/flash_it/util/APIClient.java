package com.flashitdelivery.flash_it.util;

import com.flashitdelivery.flash_it.models.remote.body.AvailabiltyPeriodBody;
import com.flashitdelivery.flash_it.models.remote.body.UserUIDBody;
import com.flashitdelivery.flash_it.models.remote.body.CreateChecklistBody;
import com.flashitdelivery.flash_it.models.remote.body.CreateDeliveryBody;
import com.flashitdelivery.flash_it.models.remote.body.CreateDeliveryPrivateBody;
import com.flashitdelivery.flash_it.models.remote.body.CreateDeliveryPublicBody;
import com.flashitdelivery.flash_it.models.remote.body.CreateDisplayUsernameBody;
import com.flashitdelivery.flash_it.models.remote.body.CreateSaleBody;
import com.flashitdelivery.flash_it.models.remote.body.FCMDeviceBody;
import com.flashitdelivery.flash_it.models.remote.body.GetMyDeliveriesBody;
import com.flashitdelivery.flash_it.models.remote.body.GetSuggestionsBody;
import com.flashitdelivery.flash_it.models.remote.body.RequestDeliveryBody;
import com.flashitdelivery.flash_it.models.remote.body.TokenRespondBody;
import com.flashitdelivery.flash_it.models.remote.body.UpdateUserReceiptEmailBody;
import com.flashitdelivery.flash_it.models.remote.body.UsernameBody;
import com.flashitdelivery.flash_it.models.remote.response.CategoriesResponse;
import com.flashitdelivery.flash_it.models.remote.response.CheckPaymentInfoResponse;
import com.flashitdelivery.flash_it.models.remote.response.CheckUsernameExistsResponse;
import com.flashitdelivery.flash_it.models.remote.response.CreateChecklistResponse;
import com.flashitdelivery.flash_it.models.remote.response.CreateDeliveryResponse;
import com.flashitdelivery.flash_it.models.remote.response.CreateSaleResponse;
import com.flashitdelivery.flash_it.models.remote.response.Delivery;
import com.flashitdelivery.flash_it.models.remote.response.FCMDeviceResponse;
import com.flashitdelivery.flash_it.models.remote.response.GetDisplayUsernameResponse;
import com.flashitdelivery.flash_it.models.remote.response.GetSuggestionsResponse;
import com.flashitdelivery.flash_it.models.remote.response.NotificationsPaginatedResponse;
import com.flashitdelivery.flash_it.models.remote.response.RequestDeliveryResponse;
import com.flashitdelivery.flash_it.models.remote.response.UpdateUserResponse;

import java.util.List;
import java.util.Set;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by yon on 05/04/16.
 */
public interface APIClient {
    @PATCH
    Call<RequestDeliveryResponse> requestDelivery(@Url String url, @Body RequestDeliveryBody body);

    @POST("delivery/")
    Call<CreateDeliveryResponse> createDelivery(@Body CreateDeliveryBody body);

    @POST("delivery/")
    Call<CreateSaleResponse> createSale(@Body CreateSaleBody body);

    @POST("user/update-receipt-email/")
    Call<UpdateUserResponse> updateReceiptEmail(@Body UpdateUserReceiptEmailBody body);

    @POST("payment/")
    Call<ResponseBody> createDisplayUsername(@Body CreateDisplayUsernameBody body);

    @POST("me/deliveries/")
    Call<List<Delivery>> getMyDeliveries(@Body GetMyDeliveriesBody body);

    @POST("fcm/devices/user/")
    Call<FCMDeviceResponse> registerDevice(@Body FCMDeviceBody body);

    @POST("checklist/")
    Call<CreateChecklistResponse> createChecklist(@Body CreateChecklistBody body);

    @POST("respond/")
    Call<ResponseBody> respondToken(@Body TokenRespondBody body);

    @POST("suggestions/")
    Call<GetSuggestionsResponse> getSuggestions(@Body GetSuggestionsBody body);

    @POST("delivery_public/")
    Call<CreateDeliveryResponse> createDeliveryPublic(@Body CreateDeliveryPublicBody body);

    @GET("delivery_public/")
    Call<List<Delivery>> getDeliveries(@Query("username") String username);

    @POST("delivery_private/")
    Call<CreateDeliveryResponse> createDeliveryPrivate(@Body CreateDeliveryPrivateBody body);

    @POST
    Call<ResponseBody> setAvailabilityDates(@Url String url, @Body Set<AvailabiltyPeriodBody> body);

    @POST
    Call<ResponseBody> requestItem(@Url String url, @Body RequestDeliveryBody body);

    @POST("payment/username-exists/")
    Call<CheckUsernameExistsResponse> checkUsernameExists(@Body UsernameBody body);

    @POST("payment/exists/")
    Call<CheckPaymentInfoResponse> checkPaymentInfoExists(@Body UserUIDBody body);

    @POST("payment/username/")
    Call<GetDisplayUsernameResponse> getDisplayUsername(@Body UserUIDBody body);

    @POST("notifications/")
    Call<List<NotificationsPaginatedResponse.Notification>> listNotifications(@Body UserUIDBody body);

    @GET("categories/")
    Call<List<CategoriesResponse>> homePage();

    @GET("delivery/")
    Call<List<Delivery>> getDeliveries();

    @GET("delivery/available-users/")
    Call<ResponseBody> getAvailableUsers();
}
