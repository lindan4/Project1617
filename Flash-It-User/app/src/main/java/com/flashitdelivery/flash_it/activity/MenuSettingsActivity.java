package com.flashitdelivery.flash_it.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.flashitdelivery.flash_it.R;

/**
 * Created by Lindan on 2016-09-11.
 */
public class MenuSettingsActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_settings);
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
    }
}
