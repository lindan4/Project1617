package com.flashitdelivery.flash_it.cardprovider;

import android.support.annotation.NonNull;
import android.text.Layout;
import android.view.View;
import android.widget.TextView;

import com.dexafree.materialList.card.Card;
import com.dexafree.materialList.card.CardProvider;
import com.flashitdelivery.flash_it.R;

/**
 * Created by Lindan on 2016-07-01.
 */
public class ChecklistCardProvider extends CardProvider<ChecklistCardProvider> {
    private String text;

    public int getLayout() {
        return R.layout.material_checklist_item_card;
    }

    public ChecklistCardProvider setLayout(Layout layout) {
        this.setLayout(R.layout.material_checklist_item_card);
        notifyDataSetChanged();
        return this;
    }

    public String getText() {
        return this.text;
    }

    public ChecklistCardProvider setText(String text) {
        this.text = text;
        notifyDataSetChanged();
        return this;
    }

    @Override
    public void render(@NonNull View view, @NonNull Card card) {
        super.render(view, card);
        TextView cardText = (TextView) view.findViewById(R.id.cardText);
        cardText.setText(text);
    }
}
