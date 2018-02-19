package com.flashitdelivery.flash_it.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.StringDef;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dexafree.materialList.card.Card;
import com.dexafree.materialList.view.MaterialListView;
import com.flashitdelivery.flash_it.R;
import com.flashitdelivery.flash_it.helpers.ChecklistRequestNotificationHelper;
import com.flashitdelivery.flash_it.helpers.UserStateHelper;
import com.flashitdelivery.flash_it.models.remote.body.UserUIDBody;
import com.flashitdelivery.flash_it.models.remote.response.NotificationsPaginatedResponse;
import com.flashitdelivery.flash_it.ui.materiallist.NotificationCardProvider;
import com.flashitdelivery.flash_it.util.APIClient;
import com.flashitdelivery.flash_it.util.APIServiceGenerator;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NotificationFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NotificationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotificationFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    final private String REQUESTED= " has requested ";
    final private String CLICK_TO_VIEW = " click here to view the notification.";
    final private String CHECKLIST_DELIMITER = "\r";

    final private String DAYS_AGO = " days ago";
    final private String HOURS_AGO = " hours ago";
    final private String MINUTES_AGO = " minutes ago";

    final private String DAYS_AGO_ONE = " day ago";
    final private String HOURS_AGO_ONE = " hour ago";
    final private String MINUTES_AGO_ONE = " minute ago";

    final private String JUST_NOW = "Just now";

    final private int SIXTY = 60;

    final private String CANNOT_GET_NOTIFICATIONS = "Cannot retrieve notifications";

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private LinearLayout listOfNotifications;
    private TextView zeroNotifications;
    private MaterialListView materialListView;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean loading;

    public NotificationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NotificationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NotificationFragment newInstance(String param1, String param2) {
        NotificationFragment fragment = new NotificationFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.notificationsLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (UserStateHelper.isLoggedIn()) {
                    loadNotifications();
                    listOfNotifications.removeAllViews();
                    swipeRefreshLayout.setEnabled(false);
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
        //Access to the layout that will hold the notifications
        listOfNotifications = (LinearLayout) view.findViewById(R.id.listOfNotifications);

        //Access to "No New Notifications" text
        zeroNotifications = (TextView) view.findViewById(R.id.zeroNotificationsText);

        materialListView = (MaterialListView) view.findViewById(R.id.material_list_notifications);
        if (UserStateHelper.isLoggedIn()) {
            MaterialListView materialListView = new MaterialListView(getActivity());
            loadNotifications();
        }

        return view;
    }

    /*
        1. Public Delivery Request
        2. Private Delivery Request
        3. Private Delivery Offer
     */

    private View createNotification(NotificationsPaginatedResponse.Notification n)
    {
        View notificationView = LayoutInflater.from(getActivity()).inflate(R.layout.item_notification, null);
        TextView notificationText = (TextView) notificationView.findViewById(R.id.notificationText);
        TextView timeStamp = (TextView) notificationView.findViewById(R.id.notificationTimestamp);

        ImageView itemImage = (ImageView) notificationView.findViewById(R.id.notificationImage);

        ImageView notificationTypeIcon = (ImageView) notificationView.findViewById(R.id.notificationTypeImage);

        //public delivery request
        if (n.type == 1 || n.type == 2)
        {
                notificationText.setText(n.other_user_username + REQUESTED);
                if (n.thumbnail != null && !n.thumbnail.isEmpty()) {
                    Picasso.with(getActivity()).load(n.thumbnail).into(itemImage);
                }
                notificationView.setOnClickListener(setPublicDeliveryRequest(n));
                if (n.type == 1)
                {
                    notificationTypeIcon.setImageDrawable(getResources().getDrawable(R.drawable.checked_checkbox_96));
                }
                else
                {
                    notificationTypeIcon.setImageDrawable(getResources().getDrawable(R.drawable.lock_96));
                }


                NotificationsPaginatedResponse.TimeAgo relativeTime = n.time_ago;

                if (relativeTime.days > 0)
                {
                    if (relativeTime.days == 1)
                    {
                        timeStamp.setText(relativeTime.days + DAYS_AGO_ONE);
                    }
                    else
                    {
                        timeStamp.setText(relativeTime.days + DAYS_AGO);
                    }

                }
                else
                {
                    if (relativeTime.hours > 0)
                    {
                        if (relativeTime.hours == 1)
                        {
                            timeStamp.setText(relativeTime.hours + HOURS_AGO_ONE);

                        }
                        else
                        {
                            timeStamp.setText(relativeTime.hours + MINUTES_AGO);

                        }
                    }
                    else
                    {
                        if (relativeTime.minutes > 0)
                        {
                            if (relativeTime.minutes == 1)
                            {
                                timeStamp.setText(relativeTime.minutes + MINUTES_AGO_ONE);
                            }
                            else
                            {
                                timeStamp.setText(relativeTime.minutes + MINUTES_AGO);
                            }
                        }
                        else
                        {
                            timeStamp.setText(JUST_NOW);
                        }
                    }
                }
        }
        else if (n.type == 3)
        {

        }
        else
        {
            notificationView = null;
        }
        return notificationView;
    }

    private void loadNotifications() {
        progressBar.setVisibility(View.VISIBLE);
        zeroNotifications.setVisibility(View.GONE);
        materialListView.getAdapter().clearAll();
        loading = true;
        APIClient apiClient = APIServiceGenerator.createService(APIClient.class);
            apiClient.listNotifications(new UserUIDBody(UserStateHelper.getUID()))
                    .enqueue(new Callback<List<NotificationsPaginatedResponse.Notification>>() {
                        @Override
                        public void onResponse(Call<List<NotificationsPaginatedResponse.Notification>> call, Response<List<NotificationsPaginatedResponse.Notification>> response) {
                            if (response.isSuccessful()) {
                                for (NotificationsPaginatedResponse.Notification n : response.body())
                                {
                                    View notificationCard = createNotification(n);

                                    if (listOfNotifications != null)
                                    {
                                        listOfNotifications.addView(notificationCard);
                                    }
                                    /*
                                    Card card = new Card.Builder(getActivity())
                                            .withProvider(new NotificationCardProvider())
                                            .setLayout(R.layout.material_result_item)
                                            .endConfig()
                                            .build();
                                            */
                                    //materialListView.getAdapter().add(card);
                                }
                                if (!response.body().isEmpty()) {
                                    zeroNotifications.setVisibility(View.GONE);
                                }
                                else {
                                    zeroNotifications.setVisibility(View.VISIBLE);
                                }
                                loading = false;
                                progressBar.setVisibility(View.GONE);
                                swipeRefreshLayout.setEnabled(true);
                            }
                            else
                            {
                                zeroNotifications.setText(CANNOT_GET_NOTIFICATIONS);
                                progressBar.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onFailure(Call<List<NotificationsPaginatedResponse.Notification>> call, Throwable t) {
                            loading = false;
                            progressBar.setVisibility(View.GONE);
                            swipeRefreshLayout.setEnabled(true);
                        }
                    });
    }

    private View.OnClickListener setPublicDeliveryRequest(final NotificationsPaginatedResponse.Notification n)
    {
        return new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                ArrayList responseChecklist = convertToChecklist(n.checklist, CHECKLIST_DELIMITER);
                ChecklistRequestNotificationHelper requestChecklist = new ChecklistRequestNotificationHelper(getActivity(), "Checklist", responseChecklist);
                requestChecklist.inflate();
            }
        };
    }

    public ArrayList<String> convertToChecklist(String s, String delimiter)
    {
        ArrayList<String> checklist = new ArrayList<String>();

        StringTokenizer token = new StringTokenizer(s, delimiter);

        if (s.isEmpty())
        {
            checklist = new ArrayList<String>();
        }
        else
        {
            while (token.hasMoreTokens())
            {
                checklist.add(token.nextToken());
            }

        }
        return checklist;
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
