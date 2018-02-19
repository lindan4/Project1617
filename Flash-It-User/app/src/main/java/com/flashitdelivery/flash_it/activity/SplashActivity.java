package com.flashitdelivery.flash_it.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.flashitdelivery.flash_it.R;

/**
 * Created by yon on 13/09/16.
 */
public class SplashActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback
{
    private int REQUEST_CODE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        promptPermissions();

        Button proccedButton = (Button) findViewById(R.id.proceedButton);
        proccedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                promptPermissions();
            }
        });
        Button exitButton = (Button) findViewById(R.id.exitOnSplash);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }

    private void continueToMain()
    {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void promptPermissions()
    {
        RelativeLayout permissionsPromptLayout = (RelativeLayout) findViewById(R.id.permissionsPromptLayout);

        if ((ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED))
        {
            permissionsPromptLayout.setVisibility(View.VISIBLE);
            ActivityCompat.requestPermissions(SplashActivity.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);

        }
        else
        {
            permissionsPromptLayout.setVisibility(View.GONE);
            continueToMain();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE)
        {
            if (grantResults.length >= 2 && (grantResults[0] == PackageManager.PERMISSION_GRANTED) &&
                    (grantResults[1] == PackageManager.PERMISSION_GRANTED) && (grantResults[2] == PackageManager.PERMISSION_GRANTED))
            {
                continueToMain();

            }
        }

    }
}
