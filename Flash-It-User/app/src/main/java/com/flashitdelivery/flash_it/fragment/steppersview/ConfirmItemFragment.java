package com.flashitdelivery.flash_it.fragment.steppersview;



import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.flashitdelivery.flash_it.R;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.File;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class ConfirmItemFragment extends Fragment {


    public ConfirmItemFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_confirm_item, container, false);
        String imagePath = getArguments().getString("imagePath", "");
        ImageView itemPicture = (ImageView)view.findViewById(R.id.itemPicture);
        TextView itemName = (TextView)view.findViewById(R.id.itemName);
        TextView itemDesc = (TextView)view.findViewById(R.id.itemDesc);
        if (!imagePath.isEmpty()) {
            Picasso.with(getContext())
                    .load(new File(imagePath))
                    .resizeDimen(R.dimen.item_photo_width, R.dimen.item_photo_height)
                    .centerCrop()
                    .into(itemPicture);
        }
        itemName.setText(getArguments().getString("itemName", ""));
        itemDesc.setText(getArguments().getString("itemDesc", ""));
        return view;
    }

}
