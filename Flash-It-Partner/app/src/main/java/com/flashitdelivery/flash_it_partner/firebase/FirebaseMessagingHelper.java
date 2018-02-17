package com.flashitdelivery.flash_it_partner.firebase;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;

import com.flashitdelivery.flash_it_partner.R;
import com.flashitdelivery.flash_it_partner.models.remote.FCMDeviceBody;
import com.flashitdelivery.flash_it_partner.models.remote.FCMDeviceResponse;
import com.flashitdelivery.flash_it_partner.util.api.APIClient;
import com.flashitdelivery.flash_it_partner.util.api.APIServiceGenerator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by yon on 08/07/16.
 */
public class FirebaseMessagingHelper {
    public static void registerDevice(Context context, String registrationId) {
        APIClient apiClient = APIServiceGenerator.createService(APIClient.class);
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.user_id_prefs), Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("USER_ID","");
        String deviceId = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        FCMDeviceBody body = new FCMDeviceBody(userId, registrationId, deviceId, true);
        apiClient.registerDevice(body).enqueue(new Callback<FCMDeviceResponse>() {
            @Override
            public void onResponse(Call<FCMDeviceResponse> call, Response<FCMDeviceResponse> response) {
            }

            @Override
            public void onFailure(Call<FCMDeviceResponse> call, Throwable t) {
            }
        });
    }
}
