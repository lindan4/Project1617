package com.flashitdelivery.flash_it.cardprovider;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dexafree.materialList.card.Card;
import com.dexafree.materialList.card.CardProvider;
import com.flashitdelivery.flash_it.R;

/**
 * Created by Lindan on 2016-08-14.
 */
public class ItemScrollCardProvider extends CardProvider<ItemScrollCardProvider>
{
    public String text;
    public ImageView itemImage;

    public ItemScrollCardProvider setText(String text)
    {
        this.text = text;
        notifyDataSetChanged();
        return this;
    }

    public ItemScrollCardProvider setImage(ImageView itemImage)
    {
        this.itemImage = itemImage;
        notifyDataSetChanged();
        return this;
    }

    public ImageView getItemImage()
    {
        return this.itemImage;
    }

    public int getLayout()
    {
        return R.layout.item_homepage_item_layout;
    }

    @Override
    public void render(@NonNull View view, @NonNull Card card) {
        super.render(view, card);

        TextView itemNameText = (TextView) view.findViewById(R.id.itemName);
        itemNameText.setText(text);

        ImageView imageView = (ImageView) view.findViewById(R.id.itemImage);
        imageView.setImageDrawable(itemImage.getDrawable());

    }
}
