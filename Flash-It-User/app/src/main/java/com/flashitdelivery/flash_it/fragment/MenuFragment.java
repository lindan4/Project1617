package com.flashitdelivery.flash_it.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.flashitdelivery.flash_it.R;
import com.flashitdelivery.flash_it.activity.RegisterActivity;
import com.flashitdelivery.flash_it.event.SignOutEvent;
import com.flashitdelivery.flash_it.helpers.ItemOverviewHelper;
import com.flashitdelivery.flash_it.helpers.UserStateHelper;
import com.flashitdelivery.flash_it.models.Item;
import com.flashitdelivery.flash_it.models.remote.body.AvailabiltyPeriodBody;
import com.flashitdelivery.flash_it.models.remote.body.CreateDeliveryPublicBody;
import com.flashitdelivery.flash_it.models.remote.body.UserUIDBody;
import com.flashitdelivery.flash_it.models.remote.response.CheckPaymentInfoResponse;
import com.flashitdelivery.flash_it.models.remote.response.Delivery;
import com.flashitdelivery.flash_it.models.remote.response.GetDisplayUsernameResponse;
import com.flashitdelivery.flash_it.util.APIClient;
import com.flashitdelivery.flash_it.util.APIServiceGenerator;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import belka.us.androidtoggleswitch.widgets.BaseToggleSwitch;
import belka.us.androidtoggleswitch.widgets.ToggleSwitch;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MenuFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MenuFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MenuFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private RelativeLayout userInfoLoggedIn;
    private RelativeLayout userNotLoggedIn;

    private ScrollView sellingWrapper;
    private ScrollView ongoingWrapper;
    private ScrollView soldWrapper;

    private LinearLayout sellingList;
    private LinearLayout ongoingList;
    private LinearLayout soldList;

    private TextView noSellingText;
    private TextView noOngoingText;
    private TextView noSoldText;

    private ToggleSwitch itemStatuses;
    private ImageView menuImageView;

    private TextView menuUsername;
    private APIClient uniApiClient;

    private boolean loggedIn;
    private View view;

    public MenuFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MenuFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MenuFragment newInstance(String param1, String param2) {
        MenuFragment fragment = new MenuFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_menu, container, false);
        this.view = view;
        userInfoLoggedIn = (RelativeLayout) view.findViewById(R.id.userInfoLoggedIn);
        userNotLoggedIn = (RelativeLayout) view.findViewById(R.id.userNotLoggedIn);

        menuUsername = (TextView) view.findViewById(R.id.menuUsername);

        isLoggedIn(view, UserStateHelper.isLoggedIn());


        menuImageView = (ImageView) view.findViewById(R.id.userAppSettings);
        menuImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopup(view);
            }
        });

        return view;
    }

    private void isLoggedIn(View view, boolean loggedIn)
    {
        if (loggedIn)
        {
            userNotLoggedIn.setVisibility(View.GONE);
            userInfoLoggedIn.setVisibility(View.VISIBLE);
            prepareLoggedInLayout(view);

        }
        else
        {
            userInfoLoggedIn.setVisibility(View.GONE);
            userNotLoggedIn.setVisibility(View.VISIBLE);
            prepareNotLoggedInLayout(view);
        }

    }

    private void prepareNotLoggedInLayout(View view)
    {
        final TextView accountReasoning = (TextView) view.findViewById(R.id.accountReasoningText);
        accountReasoning.setText(getResources().getString(R.string.account_reasoning));
        final LinearLayout accountOptions = (LinearLayout) view.findViewById(R.id.accountOptions);

        TextView loginButton = (TextView) view.findViewById(R.id.loginButton);
        TextView createAccountButton = (TextView) view.findViewById(R.id.registerButton);

        final LinearLayout loginInput = (LinearLayout) view.findViewById(R.id.loginInput);

        final TextView cancelLoginButton = (TextView) view.findViewById(R.id.cancelButtonContinue);

        final TextView loginOnPage = (TextView) view.findViewById(R.id.loginButtonContinue);
        loginOnPage.setOnClickListener(setToLog(view));

        View.OnClickListener signInOnClick = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(UserStateHelper.getLoginIntent(), UserStateHelper.RC_SIGN_IN);
            }
        };
        accountOptions.setOnClickListener(signInOnClick);
    }

    private View.OnClickListener setToLog(final View loggedinView)
    {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                isLoggedIn(loggedinView, true);
            }
        };
    }

    private void prepareLoggedInLayout(View view)
    {
        uniApiClient = APIServiceGenerator.createService(APIClient.class);
        uniApiClient.getDisplayUsername(new UserUIDBody(UserStateHelper.getUID())).enqueue(new Callback<GetDisplayUsernameResponse>() {
            @Override
            public void onResponse(Call<GetDisplayUsernameResponse> call, Response<GetDisplayUsernameResponse> response)
            {
                if (response.isSuccessful())
                {
                    menuUsername.setText(response.body().display_username);
                }
                else
                {
                    menuUsername.setText(UserStateHelper.getDisplayName());
                }
            }

            @Override
            public void onFailure(Call<GetDisplayUsernameResponse> call, Throwable t)
            {
                menuUsername.setText(UserStateHelper.getDisplayName());
            }
        });


        sellingWrapper = (ScrollView) view.findViewById(R.id.sellingListWrapper);
        ongoingWrapper = (ScrollView) view.findViewById(R.id.ongoingListWrapper);
        soldWrapper = (ScrollView) view.findViewById(R.id.soldListWrapper);

        sellingList = (LinearLayout) view.findViewById(R.id.sellingMenuList);
        ongoingList = (LinearLayout) view.findViewById(R.id.ongoingMenuList);
        soldList = (LinearLayout) view.findViewById(R.id.soldMenuList);

        noSellingText = (TextView) view.findViewById(R.id.noSellingTextMenu);
        noOngoingText = (TextView) view.findViewById(R.id.noOngoingTextMenu);
        noSoldText = (TextView) view.findViewById(R.id.noSoldTextMenu);

        final List<Item> itemList = new ArrayList<>();
        APIClient apiClient = APIServiceGenerator.createService(APIClient.class);
        apiClient.getDeliveries(menuUsername.getText().toString()).enqueue(new Callback<List<Delivery>>() {
            @Override
            public void onResponse(Call<List<Delivery>> call, Response<List<Delivery>> response) {
                if (response.isSuccessful()) {
                    for (Delivery d : response.body()) {
                        ArrayList<String> tags = new ArrayList<>();
                        for (CreateDeliveryPublicBody.Tag t : d.tags) {
                            tags.add(t.text);
                        }
                        Item item = new Item(d.name, d.description, "", "", d.price, d.ad_url, d.thumbnail, tags,
                                null);

                        View itemView = createPublicItemLayout(item);
                        sellingList.addView(itemView);

                        itemList.add(item);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Delivery>> call, Throwable t) {

            }
        });

        itemStatuses = (ToggleSwitch) view.findViewById(R.id.toggleItemStatus);
        itemStatuses.setCheckedTogglePosition(0);


        sellingWrapper.setVisibility(View.VISIBLE);
        ongoingWrapper.setVisibility(View.GONE);
        soldWrapper.setVisibility(View.GONE);


        itemStatuses.setOnToggleSwitchChangeListener(new BaseToggleSwitch.OnToggleSwitchChangeListener() {
            @Override
            public void onToggleSwitchChangeListener(int position, boolean isChecked)
            {
                if (position == 0)
                {
                    sellingWrapper.setVisibility(View.VISIBLE);
                    ongoingWrapper.setVisibility(View.GONE);
                    soldWrapper.setVisibility(View.GONE);
                }
                else if (position == 1)
                {
                    sellingWrapper.setVisibility(View.GONE);
                    ongoingWrapper.setVisibility(View.VISIBLE);
                    soldWrapper.setVisibility(View.GONE);
                }
                else
                {
                    sellingWrapper.setVisibility(View.GONE);
                    ongoingWrapper.setVisibility(View.GONE);
                    soldWrapper.setVisibility(View.VISIBLE);
                }

            }
        });

        if (sellingList.getChildCount() == 0)
        {
            noSellingText.setVisibility(View.VISIBLE);
            sellingList.setVisibility(View.GONE);
        }
        else
        {
            noSellingText.setVisibility(View.GONE);
            sellingList.setVisibility(View.VISIBLE);
        }

        if (ongoingList.getChildCount() == 0)
        {
            noOngoingText.setVisibility(View.VISIBLE);
            ongoingList.setVisibility(View.GONE);
        }
        else
        {
            noOngoingText.setVisibility(View.GONE);
            ongoingList.setVisibility(View.VISIBLE);
        }

        if (soldList.getChildCount() == 0)
        {
            noSoldText.setVisibility(View.VISIBLE);
            soldList.setVisibility(View.GONE);
        }
        else
        {
            noSoldText.setVisibility(View.GONE);
            soldList.setVisibility(View.VISIBLE);
        }



    }



    private View createPublicItemLayout(Item item)
    {
        View publicItemLayout = LayoutInflater.from(getActivity()).inflate(R.layout.material_menu_public_item, null);
        TextView publicItemName = (TextView) publicItemLayout.findViewById(R.id.itemName);
        publicItemName.setText(item.getItemName());

        TextView publicItemDescription = (TextView) publicItemLayout.findViewById(R.id.itemDesc);
        publicItemDescription.setText(item.getItemDescription());

        ImageView publicItemPicture = (ImageView) publicItemLayout.findViewById(R.id.publicItemPicture);
        publicItemLayout.setOnClickListener(setOnItemViewListener(item, getActivity(), publicItemPicture.getDrawable()));


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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UserStateHelper.RC_SIGN_IN) {
            if (resultCode == Activity.RESULT_OK) {
                final APIClient apiClient = APIServiceGenerator.createService(APIClient.class);
                apiClient.checkPaymentInfoExists(new UserUIDBody(UserStateHelper.getUID())).enqueue(new Callback<CheckPaymentInfoResponse>() {
                    @Override
                    public void onResponse(Call<CheckPaymentInfoResponse> call, Response<CheckPaymentInfoResponse> response) {
                        if (response.body().payment_exists) {
                            isLoggedIn(view, true);
                            apiClient.getDisplayUsername(new UserUIDBody(UserStateHelper.getUID())).enqueue(new Callback<GetDisplayUsernameResponse>() {
                                @Override
                                public void onResponse(Call<GetDisplayUsernameResponse> call, Response<GetDisplayUsernameResponse> response)
                                {
                                    if (response.isSuccessful())
                                    {
                                        menuUsername.setText(response.body().display_username);
                                    }
                                    else
                                    {
                                        menuUsername.setText(UserStateHelper.getDisplayName());
                                    }
                                }

                                @Override
                                public void onFailure(Call<GetDisplayUsernameResponse> call, Throwable t)
                                {
                                    menuUsername.setText(UserStateHelper.getDisplayName());
                                }
                            });
                            //menuUsername.setText(apiClient.getDisplayUsername(new UserUIDBody(UserStateHelper.getUID())));
                        }
                        else {

                            Intent intent = new Intent(getActivity(), RegisterActivity.class);
                            startActivityForResult(intent, UserStateHelper.RC_REGISTER);
                        }
                    }

                    @Override
                    public void onFailure(Call<CheckPaymentInfoResponse> call, Throwable t) {
                    }
                });
            }
        }
        else if (requestCode == UserStateHelper.RC_REGISTER) {
            if (resultCode == Activity.RESULT_OK) {
                isLoggedIn(getView(), true);
                menuUsername.setText(UserStateHelper.getDisplayName());
            }
            else {
                UserStateHelper.signOut();
            }
        }
    }

    private void setDisplayName()
    {

    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(getActivity(), v);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.signout) {
                    UserStateHelper.signOut();
                }
                return false;
            }
        });
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_fragment_menu, popup.getMenu());
        popup.show();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + getString(R.string.must_implement_fragment_transaction_listener));
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onSignOutEvent(SignOutEvent event) {
        isLoggedIn(view, false);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
