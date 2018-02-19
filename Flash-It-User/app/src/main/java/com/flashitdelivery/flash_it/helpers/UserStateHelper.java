package com.flashitdelivery.flash_it.helpers;

import android.content.Intent;

import com.firebase.ui.auth.AuthUI;
import com.flashitdelivery.flash_it.event.SignOutEvent;
import com.google.firebase.auth.FirebaseAuth;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by yon on 11/09/16.
 */
public class UserStateHelper {

    public static final int RC_SIGN_IN = 1;
    public static final int RC_REGISTER = 2;
    public static final int RC_PHOTO = 3;
    public static final int RC_AVAILABILITY = 4;
    public static final int RC_WEBVIEW = 5;

    public static Intent getLoginIntent() {
                    return AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setProviders(
                                    AuthUI.EMAIL_PROVIDER,
                                    AuthUI.GOOGLE_PROVIDER,
                                    AuthUI.FACEBOOK_PROVIDER)
                            .build();
    }

    public static boolean isLoggedIn() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }
    public static String getUID() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public static String getDisplayName()
    {
        return FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
    }

    public static void setDisplayName(String userDisplayName)
    {
        
    }

    public static void signOut() {
        FirebaseAuth.getInstance().signOut();
        EventBus.getDefault().post(new SignOutEvent());
    }
}