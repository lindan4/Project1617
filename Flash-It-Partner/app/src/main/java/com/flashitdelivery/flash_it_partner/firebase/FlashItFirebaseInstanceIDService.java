package com.flashitdelivery.flash_it_partner.firebase;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by yon on 08/07/16.
 */
public class FlashItFirebaseInstanceIDService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        FirebaseMessagingHelper.registerDevice(getApplicationContext(), refreshedToken);
    }
}
