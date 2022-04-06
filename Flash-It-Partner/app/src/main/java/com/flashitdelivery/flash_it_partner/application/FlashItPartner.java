package com.flashitdelivery.flash_it_partner.application;

import android.app.Application;
import android.content.Context;
import androidx.multidex.MultiDex;

public class FlashItPartner extends Application {

    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

}
