package com.flashitdelivery.flash_it.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.flashitdelivery.flash_it.activity.MainActivity;
import com.flashitdelivery.flash_it.event.SearchResultsEvent;
import com.flashitdelivery.flash_it.helpers.CurrencyHelper;
import com.flashitdelivery.flash_it.helpers.ItemOverviewHelper;
import com.flashitdelivery.flash_it.models.Item;
import com.flashitdelivery.flash_it.R;
import com.flashitdelivery.flash_it.models.remote.body.AvailabiltyPeriodBody;
import com.flashitdelivery.flash_it.models.remote.body.GetSuggestionsBody;
import com.flashitdelivery.flash_it.models.remote.response.CategoriesResponse;
import com.flashitdelivery.flash_it.models.remote.response.GetSuggestionsResponse;
import com.flashitdelivery.flash_it.search.Result;
import com.flashitdelivery.flash_it.util.APIClient;
import com.flashitdelivery.flash_it.util.APIServiceGenerator;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import xyz.sahildave.widget.SearchViewLayout;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements ActivityCompat.OnRequestPermissionsResultCallback
{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private final int REQUEST_CODE = 3;

    private SearchViewLayout searchViewLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private List<View> categoryViews;
    private LinearLayout itemScrollLists;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private OnFragmentInteractionListener mListener;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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

        if ((ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED))
        {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            Toast.makeText(getActivity(), getResources().getString(R.string.enable_permissions), Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_home, container, false);
        categoryViews = new ArrayList<>();

        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getCategories();
                swipeRefreshLayout.setRefreshing(false);
                swipeRefreshLayout.setEnabled(false);
            }
        });
        searchViewLayout = (SearchViewLayout)view.findViewById(R.id.search_view_container);
        HomeExpandSearchFragment homeExpandSearchFragment = new HomeExpandSearchFragment();
        searchViewLayout.setExpandedContentSupportFragment(getActivity(), homeExpandSearchFragment);
        searchViewLayout.setOnToggleAnimationListener(new SearchViewLayout.OnToggleAnimationListener() {
            @Override
            public void onStart(boolean expanded) {
                if(expanded) {
                    ((MainActivity)getActivity()).hideNavigationTabBar();
                } else {
                    ((MainActivity)getActivity()).showNavigationTabBar();
                }
            }

            @Override
            public void onFinish(boolean expanded) { }
        });
        searchViewLayout.setSearchListener(new SearchViewLayout.SearchListener() {
            @Override
            public void onFinished(String searchKeyword) {
                APIClient apiClient = APIServiceGenerator.createService(APIClient.class);
                if (!searchKeyword.isEmpty()) {
                    final ProgressBar progressBar = (ProgressBar)view.findViewById(R.id.progress_bar);
                    progressBar.setVisibility(View.VISIBLE);
                    apiClient.getSuggestions(new GetSuggestionsBody(searchKeyword)).enqueue(new Callback<GetSuggestionsResponse>() {
                    @Override
                    public void onResponse(Call<GetSuggestionsResponse> call, Response<GetSuggestionsResponse> response) {
                        if (response.isSuccessful()) {
                            List<Result> results = new ArrayList<>();
                            GetSuggestionsResponse getSuggestionsResponse = response.body();
                            for (String username : getSuggestionsResponse.users) {
                                results.add(new Result(Result.ResultType.User, new Result.UserData(username)));
                            }
                            for (GetSuggestionsResponse.Delivery delivery : getSuggestionsResponse.deliveries) {
                                results.add(new Result(Result.ResultType.Item, new Result.ItemData(delivery.id, delivery.owner, delivery.name, delivery.image, delivery.description, delivery.ad_url,
                                        delivery.price, delivery.tags, delivery.availability)));
                            }
                            EventBus.getDefault().post(new SearchResultsEvent(results));
                            progressBar.setVisibility(View.GONE);
                        }
                        else {
                            onFailure(call, null);
                        }
                    }

                    @Override
                    public void onFailure(Call<GetSuggestionsResponse> call, Throwable t) {
                        new MaterialDialog.Builder(getContext())
                                .title(R.string.network_error)
                                .content(R.string.results_fetching_failed)
                                .positiveText(R.string.ok)
                                .show();
                    }
                });
                }
            }
        });

        //Access to the list of the home page which will contain all scrollable lists
        itemScrollLists = (LinearLayout) view.findViewById(R.id.itemScrollList);
        getCategories();

        return view;
}

    private View createHomeListIconView(Item item)
    {
        View itemButtonView = LayoutInflater.from(getContext()).inflate(R.layout.item_homepage_item_layout, null);

        ImageView itemPic = (ImageView) itemButtonView.findViewById(R.id.itemImage);
        if (item.getItemImageURL() != null && item.getItemImageURL().isEmpty())
        {
            itemPic.setImageDrawable(getResources().getDrawable(R.drawable.dummydog));

        }
        else
        {
            Picasso.with(getActivity()).load(item.getItemImageURL()).into(itemPic);
        }
        itemButtonView.setOnClickListener(setOnItemViewListener(item, getActivity(), itemPic.getDrawable()));

        TextView itemButtonName = (TextView) itemButtonView.findViewById(R.id.itemName);
        itemButtonName.setText(item.getItemName());
        itemButtonName.setTypeface(Typeface.createFromAsset(getResources().getAssets(), "fonts/Raleway-Light.ttf"));

        TextView itemPrice = (TextView) itemButtonView.findViewById(R.id.itemPriceText);
        itemPrice.setText(CurrencyHelper.returnAmt(item.getItemPrice()));

        ImageView externalIcon = (ImageView) itemButtonView.findViewById(R.id.externalIcon);
        if (item.getItemURL().isEmpty())
        {
            externalIcon.setVisibility(View.GONE);
        }
        else
        {
            externalIcon.setVisibility(View.VISIBLE);
        }

        return itemButtonView;
    }

    private ArrayList<Item> getItemsOnDisplay()
    {
        ArrayList<Item> itemsForDisplay = new ArrayList<Item>();

        ArrayList<String> tags = new ArrayList<String>();
        tags.add("Fresh");
        tags.add("Real");

        Item itemOne = new Item("Prada purse", "Authentic. Made in China.", "yonnie", "196 Aldergrove Dr, Markham, ON",  14.95,"", "",
                new ArrayList<String>(), new ArrayList<AvailabiltyPeriodBody>());
        Item itemTwo = new Item("Argon 18", "Authentic. Not made in China.", "yonnie", "120 Aldergrove Dr, Markham, ON", 16.95,
                "http://www.kijiji.ca/v-road-bike/city-of-toronto/mint-condition-argon-18/1188661679?enableSearchNavigationFlag=true", "", tags,
        new ArrayList<AvailabiltyPeriodBody>());

        itemsForDisplay.add(itemOne);
        itemsForDisplay.add(itemTwo);

        return itemsForDisplay;
    }

    public View.OnClickListener setOnItemViewListener(final Item item, final Activity activity, final Drawable itemImageDrawable)
    {
        return new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                ItemOverviewHelper itemOverview = new ItemOverviewHelper(item, activity, item.getAvailability(), false);
                itemOverview.inflate();
            }
        };

    }

    private void requestLocationPermissions()
    {

    }

    private void getCategories() {
        itemScrollLists.removeAllViews();
        progressBar.setVisibility(View.VISIBLE);
        APIClient apiClient = APIServiceGenerator.createService(APIClient.class);
        apiClient.homePage().enqueue(new Callback<List<CategoriesResponse>>() {
            @Override
            public void onResponse(Call<List<CategoriesResponse>> call, Response<List<CategoriesResponse>> response) {
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setEnabled(true);
                if (response.isSuccessful()) {
                    for (CategoriesResponse r : response.body()) {
                        //Access to the horizontal scroll list view
                        View horizontalScrollableListView = LayoutInflater.from(getActivity()).inflate(R.layout.item_horizontal_scrollable_list, null);

                        //Get the list from the horizontal scroll list view
                        final LinearLayout horizontalScrollableList = (LinearLayout) horizontalScrollableListView.findViewById(R.id.linearScrollList);

                        TextView listTitle = (TextView) horizontalScrollableListView.findViewById(R.id.categoryText);
                        listTitle.setTypeface(Typeface.createFromAsset(getResources().getAssets(), "fonts/Raleway-Regular.ttf"));
                        listTitle.setText(r.title);

                        for (GetSuggestionsResponse.Delivery d : r.items) {
                            Item item = new Item(d.name, d.description, d.owner, "", d.price, d.ad_url, d.image, d.tags, d.availability);
                            View itemIcon = createHomeListIconView(item);
                            horizontalScrollableList.addView(itemIcon);
                            horizontalScrollableList.setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View view, MotionEvent motionEvent) {
                                    if (motionEvent.getAction() == MotionEvent.ACTION_MOVE && horizontalScrollableList.getParent() != null)
                                    {
                                        horizontalScrollableList.getParent().requestDisallowInterceptTouchEvent(true);
                                    }
                                    return horizontalScrollableList.onTouchEvent(motionEvent);
                                }
                            });
                        }
                        //Add horizontal scrollable list view to the scroll list
                        itemScrollLists.addView(horizontalScrollableListView);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<CategoriesResponse>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setEnabled(true);
            }
        });
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
                    + context.getString(R.string.must_implement_fragment_transaction_listener));
        }
    }



    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
