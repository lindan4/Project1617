package com.flashitdelivery.flash_it.ui.materiallist;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dexafree.materialList.card.Card;
import com.dexafree.materialList.card.CardProvider;
import com.flashitdelivery.flash_it.R;
import com.flashitdelivery.flash_it.models.Item;
import com.squareup.picasso.Picasso;

/**
 * Created by yon on 19/08/16.
 */
public class ItemResultCardProvider extends CardProvider<ItemResultCardProvider> {

    private Activity activity;
    private String itemName;
    private String itemDesc;
    private Item item;
    private ImageView imageView;
    private String tag;
    private int itemId;

    public ItemResultCardProvider setItemData(Activity activity, String itemName, String itemDesc, Item item, int itemId) {

        this.activity = activity;
        this.itemName = itemName;
        this.itemDesc = itemDesc;
        this.item = item;
        this.itemId = itemId;
        return this;
    }

    public String getTag() {
        return this.tag;
    }

    public int getItemId() {
        return itemId;
    }


    public Item getItem()
    {
        return this.item;
    }

    public ImageView getImageView()
    {
        return this.imageView;
    }

    public ItemResultCardProvider setTag(String tag) {
        this.tag = tag;
        return this;
    }

    @Override
    public void render(@NonNull View view, @NonNull Card card) {
        super.render(view, card);

        TextView itemNameText = (TextView) view.findViewById(R.id.itemName);
        itemNameText.setText(itemName);
        TextView itemDescText = (TextView) view.findViewById(R.id.itemDesc);
        itemDescText.setText(itemDesc);

        //Place image
        imageView = (ImageView) view.findViewById(R.id.searchItemPicture);

        if (item.getItemImageURL() == null || getItem().getItemImageURL().isEmpty())
        {
            imageView.setImageDrawable(activity.getResources().getDrawable(R.drawable.flashit_red_box));
        }
        else
        {
            Picasso.with(activity).load(getItem().getItemImageURL()).into(imageView);
        }
    }
}
