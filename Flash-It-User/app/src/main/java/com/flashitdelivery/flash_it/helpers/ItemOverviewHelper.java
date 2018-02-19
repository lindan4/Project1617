package com.flashitdelivery.flash_it.helpers;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.flashitdelivery.flash_it.activity.WebViewActivity;
import com.flashitdelivery.flash_it.models.Item;
import com.flashitdelivery.flash_it.R;
import com.flashitdelivery.flash_it.models.remote.body.AvailabiltyPeriodBody;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Lindan on 2016-08-16.
 */
public class ItemOverviewHelper
{
    final private String URLStart = "https://";


    private Item item;
    private Activity activity;
    private List<AvailabiltyPeriodBody> availability;
    private MaterialDialog itemOverviewDialog;

    private boolean isOwnItem;

    public ItemOverviewHelper(Item item, Activity activity, List<AvailabiltyPeriodBody> availability, boolean isOwnItem)
    {
        this.setItem(item);
        this.setActivity(activity);
        this.setItemOverviewDialog(new MaterialDialog.Builder(getActivity())
                .customView(R.layout.item_overview_layout, false)
                .build());
        this.setOwnItem(isOwnItem);
        this.setAvailability(availability);

        View itemView = getItemOverviewDialog().getCustomView();

        ImageView itemOverviewImage = (ImageView) itemView.findViewById(R.id.itemOverviewImage);
        if (item.getItemImageURL() != null && !item.getItemImageURL().isEmpty()) {
            Picasso.with(getActivity()).load(item.getItemImageURL()).into(itemOverviewImage);
        }
        else {
            itemOverviewImage.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.flashit_red_box));
        }
        TextView itemTitle = (TextView) itemView.findViewById(R.id.itemOverviewName);
        itemTitle.setText(getItem().getItemName());


        TextView itemPrice = (TextView) itemView.findViewById(R.id.itemOverviewPrice);
        itemPrice.setText(CurrencyHelper.returnAmt(getItem().getItemPrice()));


        Button seeAdExternal = (Button) itemView.findViewById(R.id.viewAdButton);
        if (getItem().getItemURL().isEmpty())
        {
            seeAdExternal.setBackgroundColor(getActivity().getResources().getColor(android.R.color.darker_gray));
            seeAdExternal.setTextColor(getActivity().getResources().getColor(android.R.color.black));
            seeAdExternal.setVisibility(View.INVISIBLE);
            seeAdExternal.setClickable(false);
        }
        else
        {
            seeAdExternal.setBackgroundColor(getActivity().getResources().getColor(R.color.flashIt_red));
            seeAdExternal.setTextColor(getActivity().getResources().getColor(android.R.color.white));
            seeAdExternal.setVisibility(View.GONE);
            seeAdExternal.setClickable(true);
        }
        seeAdExternal.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent seeAd = new Intent(getActivity(), WebViewActivity.class);
                boolean post = false;
                seeAd.putExtra("post", post);
                seeAd.putExtra("adLink", getItem().getItemURL());
                getActivity().startActivity(seeAd);
            }
        });

        Button requestButton = (Button) itemView.findViewById(R.id.requestButton);

        if (getOwnItem())
        {
            requestButton.setVisibility(View.INVISIBLE);
        }
        else
        {
            requestButton.setVisibility(View.VISIBLE);
        }

        final ItemReceiveAvailabilityHelper itemReceiveAvailabilityHelper = new ItemReceiveAvailabilityHelper(getActivity(), getItem(), availability);

        requestButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                itemOverviewDialog.dismiss();
                itemReceiveAvailabilityHelper.inflate();
                //Toast.makeText(getActivity(), "Gucci", Toast.LENGTH_SHORT).show();
            }
        });

        TextView itemDesc = (TextView) itemView.findViewById(R.id.externalAdItemDesc);
        itemDesc.setText(getItem().getItemDescription());


        /*
        if (getItem().getItemURL().isEmpty())
        {
            seeAdExternal.setVisibility(View.GONE);

            itemDesc.setVisibility(View.VISIBLE);
            itemDesc.setText(getItem().getItemDescription());
        }
        else
        {
            seeAdExternal.setVisibility(View.VISIBLE);
            itemDesc.setVisibility(View.GONE);
        }
        */
    }

    public Item getItem()
    {
        return this.item;
    }

    public Activity getActivity()
    {
        return this.activity;
    }

    public MaterialDialog getItemOverviewDialog()
    {
        return this.itemOverviewDialog;
    }

    public boolean getOwnItem()
    {
        return this.isOwnItem;
    }

    public void setItem(Item item)
    {
        this.item = item;
    }

    public void setActivity(Activity activity)
    {
        this.activity = activity;
    }

    public void setAvailability(List<AvailabiltyPeriodBody> availability) {
        this.availability = availability;
    }

    public void setItemOverviewDialog(MaterialDialog itemOverviewDialog)
    {
        this.itemOverviewDialog = itemOverviewDialog;
    }

    public void setOwnItem(boolean isOwnItem)
    {
        this.isOwnItem  = isOwnItem;
    }
    public void inflate()
    {
        itemOverviewDialog.show();
    }

}
