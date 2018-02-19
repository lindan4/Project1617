package com.flashitdelivery.flash_it.ui.materiallist;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dexafree.materialList.card.Card;
import com.dexafree.materialList.card.CardProvider;
import com.flashitdelivery.flash_it.R;
import com.flashitdelivery.flash_it.search.Result;

/**
 * Created by yon on 19/08/16.
 */
public class UserResultCardProvider extends CardProvider<UserResultCardProvider> {
    private String username;
    private Result.UserData userData;
    private ImageView userSearchImage;
    private String tag;
    public UserResultCardProvider setUserData(String username, Result.UserData userData) {
        this.username = username;
        this.userData = userData;
        return this;
    }

    public String getTag() {
        return this.tag;
    }

    public ImageView getUserSearchImage()
    {
        return this.userSearchImage;
    }

    public Result.UserData getUserData()
    {
        return this.userData;
    }

    public UserResultCardProvider setTag(String tag) {
        this.tag = tag;
        return this;
    }

    @Override
    public void render(@NonNull View view, @NonNull Card card) {
        super.render(view, card);

        TextView textView = (TextView) view.findViewById(R.id.username);
        textView.setText(username);

        userSearchImage = (ImageView) view.findViewById(R.id.searchUserPicture);
    }
}
