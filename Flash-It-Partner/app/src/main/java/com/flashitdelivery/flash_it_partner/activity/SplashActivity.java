package com.flashitdelivery.flash_it_partner.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.flashitdelivery.flash_it_partner.R;

// This activity will show Lock
public class SplashActivity extends AppCompatActivity {

    private LocalBroadcastManager broadcastManager;
    private int LOCATION_REQUEST_CODE;
    final private int ONE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Customize your activity
        Intent MainIntent = new Intent(this, MainActivity.class);

        startActivity(MainIntent);
        forceLocationUsage();
    }

    private void forceLocationUsage()
    {
        int LOCATION_ALLOWED = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        if (LOCATION_ALLOWED != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.enable_location), Toast.LENGTH_LONG).show();
        }
        else
        {
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length == ONE) {

            } else {
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
