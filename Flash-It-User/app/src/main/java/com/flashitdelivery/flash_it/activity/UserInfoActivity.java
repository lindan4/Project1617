package com.flashitdelivery.flash_it.activity;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.flashitdelivery.flash_it.R;
import com.flashitdelivery.flash_it.helpers.ItemOverviewHelper;
import com.flashitdelivery.flash_it.models.Item;

import belka.us.androidtoggleswitch.widgets.BaseToggleSwitch;
import belka.us.androidtoggleswitch.widgets.ToggleSwitch;

/**
 * Created by Lindan on 2016-09-11.
 */
public class UserInfoActivity extends AppCompatActivity
{
    private TextView externalUsername;
    private ScrollView sellingWrapper;
    private LinearLayout sellingList;

    private ScrollView soldWrapper;
    private LinearLayout soldList;

    private ToggleSwitch itemToggle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        externalUsername = (TextView) findViewById(R.id.externalUsername);

        String username = getIntent().getExtras().getString("username");
        externalUsername.setText(username);



        sellingWrapper = (ScrollView) findViewById(R.id.sellingListInfoWrapper);
        sellingList = (LinearLayout) findViewById(R.id.sellingListUserInfo);

        soldWrapper = (ScrollView) findViewById(R.id.soldListUserInfoWrapper);
        soldList = (LinearLayout) findViewById(R.id.soldListUserInfo);

        itemToggle = (ToggleSwitch) findViewById(R.id.toggleItemStatusUserInfo);

        itemToggle.setOnToggleSwitchChangeListener(new BaseToggleSwitch.OnToggleSwitchChangeListener() {
            @Override
            public void onToggleSwitchChangeListener(int position, boolean isChecked)
            {
                if (position == 0)
                {
                    sellingWrapper.setVisibility(View.VISIBLE);
                    soldWrapper.setVisibility(View.GONE);
                }
                else
                {
                    sellingWrapper.setVisibility(View.GONE);
                    soldWrapper.setVisibility(View.VISIBLE);
                }
            }
        });
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

    private View createPublicItemLayout(Item item)
    {
        View publicItemLayout = LayoutInflater.from(UserInfoActivity.this).inflate(R.layout.material_menu_public_item, null);
        TextView publicItemName = (TextView) publicItemLayout.findViewById(R.id.itemName);
        publicItemName.setText(item.getItemName());

        TextView publicItemDescription = (TextView) publicItemLayout.findViewById(R.id.itemDesc);
        publicItemDescription.setText(item.getItemDescription());

        ImageView publicItemPicture = (ImageView) publicItemLayout.findViewById(R.id.publicItemPicture);
        publicItemLayout.setOnClickListener(setOnItemViewListener(item, UserInfoActivity.this, publicItemPicture.getDrawable()));


        return publicItemLayout;
    }

    public View.OnClickListener setOnItemViewListener(final Item item, final Activity activity, final Drawable itemImageDrawable)
    {
        return new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                ItemOverviewHelper itemOverview = new ItemOverviewHelper(item, activity, item.getAvailability(), true);
                itemOverview.inflate();
            }
        };

    }
}
