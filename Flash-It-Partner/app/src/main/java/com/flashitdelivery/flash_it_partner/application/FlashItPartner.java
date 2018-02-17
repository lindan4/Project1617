package com.flashitdelivery.flash_it_partner.application;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.auth0.lock.Lock;
import com.auth0.lock.LockProvider;

public class FlashItPartner extends Application implements LockProvider {

    private Lock lock;

    public void onCreate() {
        super.onCreate();
        lock = new Lock.Builder()
                .loadFromApplication(this)
                        /** Other configuration goes here */
                .closable(true)
                .build();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public Lock getLock() {
        return lock;
    }
}
