package com.flashitdelivery.flash_it.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.dexafree.materialList.card.Card;
import com.dexafree.materialList.listeners.RecyclerItemClickListener;
import com.dexafree.materialList.view.MaterialListView;
import com.flashitdelivery.flash_it.R;
import com.flashitdelivery.flash_it.activity.UserInfoActivity;
import com.flashitdelivery.flash_it.event.SearchResultsEvent;
import com.flashitdelivery.flash_it.helpers.ItemOverviewHelper;
import com.flashitdelivery.flash_it.models.Item;
import com.flashitdelivery.flash_it.search.Result;
import com.flashitdelivery.flash_it.ui.materiallist.ItemResultCardProvider;
import com.flashitdelivery.flash_it.ui.materiallist.UserResultCardProvider;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeExpandSearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeExpandSearchFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private final String LEFT_BRACKET = " (";
    private final String RIGHT_BRACKET = ")";

    private final String ITEMS_TEXT = "ITEMS";
    private final String USERS_TEXT = "USERS";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private List<Result> results;

    private InputMethodManager imm;

    private LinearLayout itemsBlock;
    private LinearLayout usersBlock;

    private MaterialListView materialListItem;
    private MaterialListView materialListUser;

    private TabHost tabHost;

    private TabHost.TabSpec tabOne;
    private TabHost.TabSpec tabTwo;
    private MaterialDialog itemOverviewDialog;


    public HomeExpandSearchFragment(){
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeExpandSearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeExpandSearchFragment newInstance(String param1, String param2) {
        HomeExpandSearchFragment fragment = new HomeExpandSearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        itemOverviewDialog = new MaterialDialog.Builder(getActivity()).build();
    }
    @Subscribe
    @SuppressWarnings("unused")
    public void onSearchResultsEvent(SearchResultsEvent event) {
        this.results = event.getResultList();
        materialListItem.getAdapter().clearAll();
        materialListUser.getAdapter().clearAll();


        for (Result r : results) {
            Card.Builder cardBuilder = new Card.Builder(getContext());
            Card card;
            Log.i("RESULTS", "" + results.size());
            if (r.getResultType() == Result.ResultType.User) {
                Result.UserData userData = (Result.UserData)r.getResultData();
                card = cardBuilder.withProvider(new UserResultCardProvider())
                        .setTag(userData.username)
                        .setUserData(userData.username, userData)
                        .setLayout(R.layout.material_result_user)
                        .endConfig()
                        .build();
                materialListUser.getAdapter().add(card);
                materialListUser.addOnItemTouchListener(new RecyclerItemClickListener.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(@NonNull Card card, int position)
                    {
                        UserResultCardProvider userResultCardProvider = (UserResultCardProvider) card.getProvider();
                        Intent userOverviewActivity = new Intent(getActivity(), UserInfoActivity.class);
                        userOverviewActivity.putExtra("username", userResultCardProvider.getUserData().username);
                        getActivity().startActivity(userOverviewActivity);
                    }

                    @Override
                    public void onItemLongClick(@NonNull Card card, int position) {

                    }
                });
            }
            else if (r.getResultType() == Result.ResultType.Item) {
                Result.ItemData itemData = (Result.ItemData)r.getResultData();
                final Item searchItem = new Item(itemData.itemName, itemData.itemDesc, itemData.itemOwner, "",  Float.parseFloat("" + itemData.price), itemData.adUrl, itemData.itemImage, itemData.tags, itemData.availability);
                searchItem.setBackendId(itemData.deliveryId);

                card = cardBuilder.withProvider(new ItemResultCardProvider())
                        .setTag("" + itemData.deliveryId)
                        .setItemData(getActivity(), itemData.itemName, itemData.itemDesc, searchItem, itemData.deliveryId)
                        .setLayout(R.layout.material_result_item)
                        .endConfig()
                        .build();

                materialListItem.getAdapter().add(card);

                materialListItem.addOnItemTouchListener(new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(@NonNull Card card, int position)
                    {
                        showItemOverviewDialog(card, searchItem);
                    }

                    @Override
                    public void onItemLongClick(@NonNull Card card, int position) {

                    }
                });
            }
        }
        updateTabs(tabHost);
    }

    private void showItemOverviewDialog(Card card, Item searchItem) {
        itemOverviewDialog.dismiss();
        ItemResultCardProvider publicItemCardProvider = (ItemResultCardProvider) card.getProvider();
        ItemOverviewHelper itemOverview = new ItemOverviewHelper(publicItemCardProvider.getItem(), getActivity(),
                searchItem.getAvailability(), false);
        itemOverview.inflate();
        itemOverviewDialog = itemOverview.getItemOverviewDialog();
    }

    private void updateTabs(TabHost tabHost)
    {
        int itemCount = materialListItem.getAdapter().getItemCount();
        int userCount = materialListUser.getAdapter().getItemCount();

        TextView itemsTabText = (TextView) tabHost.getTabWidget().getChildTabViewAt(0).findViewById(android.R.id.title);
        TextView usersTabText = (TextView) tabHost.getTabWidget().getChildTabViewAt(1).findViewById(android.R.id.title);
        
        if (itemCount == 0)
        {
            itemsTabText.setText(ITEMS_TEXT);
        }
        else
        {
            itemsTabText.setText(ITEMS_TEXT + LEFT_BRACKET + itemCount + RIGHT_BRACKET);
        }

        if (userCount == 0)
        {
            usersTabText.setText(USERS_TEXT);

        }
        else
        {
            usersTabText.setText(USERS_TEXT + LEFT_BRACKET + userCount + RIGHT_BRACKET);
        }

    }

        @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_expand_search_home, container, false);

        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        itemsBlock = (LinearLayout) view.findViewById(R.id.itemResultBlock);
        usersBlock = (LinearLayout) view.findViewById(R.id.userResultBlock);

        materialListItem = (MaterialListView) view.findViewById(R.id.material_list_item);
        materialListUser = (MaterialListView) view.findViewById(R.id.material_list_user);

        tabHost = (TabHost) view.findViewById(R.id.tabSelectorHost);
        tabHost.setup();

        tabOne = tabHost.newTabSpec(ITEMS_TEXT);
        tabOne.setContent(R.id.resultsScrollItemWrapper);
        tabOne.setIndicator(ITEMS_TEXT);
        tabHost.addTab(tabOne);

        tabTwo = tabHost.newTabSpec(USERS_TEXT);
        tabTwo.setContent(R.id.resultsScrollUserWrapper);
        tabTwo.setIndicator(USERS_TEXT);
        tabHost.addTab(tabTwo);

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener()
        {
            @Override
            public void onTabChanged(String s)
            {
                tabHost.requestFocus();
                imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);

            }
        });

        return view;
    }



}
