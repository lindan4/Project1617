package com.flashitdelivery.flash_it.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.blackcat.currencyedittext.CurrencyEditText;
import com.flashitdelivery.flash_it.R;
import com.flashitdelivery.flash_it.activity.PickupInfoActivity;
import com.flashitdelivery.flash_it.activity.RegisterActivity;
import com.flashitdelivery.flash_it.activity.WebViewActivity;
import com.flashitdelivery.flash_it.event.PhotoPickedEvent;
import com.flashitdelivery.flash_it.helpers.UrlHelper;
import com.flashitdelivery.flash_it.helpers.UserStateHelper;
import com.flashitdelivery.flash_it.models.remote.Address;
import com.flashitdelivery.flash_it.models.remote.body.AvailabiltyPeriodBody;
import com.flashitdelivery.flash_it.models.remote.body.UserUIDBody;
import com.flashitdelivery.flash_it.models.remote.body.CreateDeliveryPrivateBody;
import com.flashitdelivery.flash_it.models.remote.body.CreateDeliveryPublicBody;
import com.flashitdelivery.flash_it.models.remote.body.UsernameBody;
import com.flashitdelivery.flash_it.models.remote.response.CheckPaymentInfoResponse;
import com.flashitdelivery.flash_it.models.remote.response.CheckUsernameExistsResponse;
import com.flashitdelivery.flash_it.models.remote.response.CreateDeliveryResponse;
import com.flashitdelivery.flash_it.util.APIClient;
import com.flashitdelivery.flash_it.util.APIServiceGenerator;
import com.flashitdelivery.flash_it.util.FileUploadService;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import belka.us.androidtoggleswitch.widgets.BaseToggleSwitch;
import belka.us.androidtoggleswitch.widgets.ToggleSwitch;
import me.gujun.android.taggroup.TagGroup;
import me.iwf.photopicker.PhotoPicker;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EnlistFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EnlistFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EnlistFragment extends Fragment implements ActivityCompat.OnRequestPermissionsResultCallback
{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final int REQUEST_CODE_PHOTO = UserStateHelper.RC_PHOTO;
    private static final int REQUEST_CODE_WEBVIEW = UserStateHelper.RC_WEBVIEW;
    private static final int REQUEST_CODE_AVAILABILITY = UserStateHelper.RC_AVAILABILITY;

    private boolean publicSelected = true;
    private boolean privateSelected = false;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private ImageView photoView;
    private EditText itemNameInput;
    private EditText itemDescInput;
    private CurrencyEditText itemPriceInput;
    private EditText receiverUsername;
    private ScrollView tagGroupWrapper;
    private TagGroup tagGroup;

    private String imagePath;
    private List<String> availabilityDates;
    private TextView optionDescription;
    private ToggleSwitch linkAd;
    private ProgressDialog progressDialog;

    private TextView nextButton;
    private TextView viewAd;
    private String adUrl;

    private boolean isPrivate;
    private boolean post;
    private boolean availabilitySet;
    private boolean photoUploaded;

    public EnlistFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EnlistFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EnlistFragment newInstance(String param1, String param2) {
        EnlistFragment fragment = new EnlistFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        imagePath = new String();
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_enlist, container, false);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.dismiss();

        final RelativeLayout publicOption = (RelativeLayout) view.findViewById(R.id.publicOption);
        final RelativeLayout privateOption = (RelativeLayout) view.findViewById(R.id.privateOption);

        itemNameInput = (EditText) view.findViewById(R.id.itemNameInput);
        itemDescInput = (EditText) view.findViewById(R.id.itemDescInput);
        itemPriceInput = (CurrencyEditText) view.findViewById(R.id.itemPriceInput);

        viewAd = (TextView) view.findViewById(R.id.viewAd);

        tagGroupWrapper = (ScrollView) view.findViewById(R.id.tagGroupScrollWrapper);
        tagGroup = (TagGroup) view.findViewById(R.id.tag_group);
        receiverUsername = (EditText) view.findViewById(R.id.receiver_id);
        receiverUsername.setHint("Receiver Username");
        receiverUsername.setEnabled(true);

        optionDescription = (TextView) view.findViewById(R.id.optionDescription);

        publicOption.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                isPrivate = false;
                optionDescription.setText(R.string.public_option_desc);
                //Setting appearance of public option selection
                ImageView publicOptionSelection = (ImageView) publicOption.findViewById(R.id.publicOptionSelection);
                TextView publicOptionText = (TextView) publicOption.findViewById(R.id.publicOptionText);

                publicOptionSelection.setImageDrawable(getResources().getDrawable(R.drawable.enlistoptionselect_1));
                publicOptionText.setTextColor(getResources().getColor(R.color.flashIt_white));

                //Setting appearance of private option selection
                ImageView privateOptionSelection = (ImageView) privateOption.findViewById(R.id.privateOptionSelection);
                TextView privateOptionText = (TextView) privateOption.findViewById(R.id.privateOptionText);

                privateOptionSelection.setImageDrawable(getResources().getDrawable(R.drawable.enlistoptionunselect_1));
                privateOptionText.setTextColor(getResources().getColor(R.color.flashIt_red));

                tagGroupWrapper.setVisibility(View.VISIBLE);
                receiverUsername.setVisibility(View.GONE);
            }
        });

        privateOption.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                isPrivate = true;
                optionDescription.setText(R.string.private_option_desc);
                //Setting appearance of public option selection
                ImageView publicOptionSelection = (ImageView) publicOption.findViewById(R.id.publicOptionSelection);
                TextView publicOptionText = (TextView) publicOption.findViewById(R.id.publicOptionText);

                publicOptionSelection.setImageDrawable(getResources().getDrawable(R.drawable.enlistoptionunselect_1));
                publicOptionText.setTextColor(getResources().getColor(R.color.flashIt_red));

                //Setting appearance of private option selection
                ImageView privateOptionSelection = (ImageView) privateOption.findViewById(R.id.privateOptionSelection);
                TextView privateOptionText = (TextView) privateOption.findViewById(R.id.privateOptionText);

                privateOptionSelection.setImageDrawable(getResources().getDrawable(R.drawable.enlistoptionselect_1));
                privateOptionText.setTextColor(getResources().getColor(R.color.flashIt_white));

                tagGroupWrapper.setVisibility(View.GONE);
                receiverUsername.setVisibility(View.VISIBLE);
            }
        });

        photoView = (ImageView)view.findViewById(R.id.itemPicture);
        photoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                selectPhoto();
            }
        });

        nextButton = (TextView) view.findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemNameInput.getText().toString().isEmpty()) {
                    itemNameInput.setError(getString(R.string.must_have_name));
                }
                if (itemDescInput.getText().toString().isEmpty()) {
                    itemDescInput.setError(getString(R.string.must_have_description));
                }
                if (itemPriceInput.getText().toString().isEmpty()) {
                    itemPriceInput.setError(getString(R.string.must_have_price));
                }
                if (receiverUsername.getText().toString().isEmpty() && isPrivate) {
                    receiverUsername.setError(getString(R.string.cant_be_empty));
                }
                if (!itemNameInput.getText().toString().isEmpty() &&
                        !itemDescInput.getText().toString().isEmpty()
                        && !itemPriceInput.getText().toString().isEmpty())
                {
                    nextButton.setEnabled(false);
                    if (isPrivate && !receiverUsername.getText().toString().isEmpty()) {
                        nextIfPrivate();
                    }
                    else {
                        preparePickupInfoActivity();
                    }
                }
            }
        });



        linkAd = (ToggleSwitch) view.findViewById(R.id.linkAd);
        linkAd.setOnToggleSwitchChangeListener(new BaseToggleSwitch.OnToggleSwitchChangeListener()
        {
            @Override
            public void onToggleSwitchChangeListener(int position, boolean isChecked)
            {
                if (position == 0)
                {
                    viewAd.setVisibility(View.GONE);
                }
                else
                {
                    Intent webViewintent = new Intent(getActivity(), WebViewActivity.class);
                    webViewintent.putExtra(getString(R.string.post), true);
                    startActivityForResult(webViewintent, REQUEST_CODE_WEBVIEW);
                }

            }
        });

        return view;
    }

    private void selectPhoto()
    {
        if ((ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED))
        {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, REQUEST_CODE_PHOTO);
            Toast.makeText(getActivity(), getResources().getString(R.string.enable_permissions), Toast.LENGTH_LONG).show();
        }
        else
        {
            PhotoPicker.builder()
                    .setPhotoCount(1)
                    .setShowCamera(true)
                    .setShowGif(false)
                    .setPreviewEnabled(true)
                    .start(getActivity(), PhotoPicker.REQUEST_CODE);
        }
    }

    private void nextIfPrivate() {
        APIClient apiClient = APIServiceGenerator.createService(APIClient.class);
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.show();
        apiClient.checkUsernameExists(new UsernameBody(receiverUsername.getText().toString())).enqueue(new Callback<CheckUsernameExistsResponse>() {
            @Override
            public void onResponse(Call<CheckUsernameExistsResponse> call, Response<CheckUsernameExistsResponse> response) {
                progressDialog.dismiss();
                if (response.isSuccessful() && response.body().exists) {
                    preparePickupInfoActivity();
                }
                else {
                    receiverUsername.setError(getString(R.string.user_does_not_exist));
                    nextButton.setEnabled(true);
                }
            }

            @Override
            public void onFailure(Call<CheckUsernameExistsResponse> call, Throwable t) {
                call.enqueue(this);
            }
        });
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onPhotoPickedEvent(PhotoPickedEvent event) {
        imagePath = event.photoUrl;
        Picasso.with(getContext())
                .load(new File(event.photoUrl))
                .resizeDimen(R.dimen.item_photo_width, R.dimen.item_photo_height)
                .centerCrop()
                .into(photoView);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults)
    {
        if (requestCode == REQUEST_CODE_PHOTO)
        {
            if (grantResults.length > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)
            {
                selectPhoto();
            }
            else
            {
                selectPhoto();
            }
        }
        else
        {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
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

    private void launchPickupInfoActivity() {
        Intent pickupInfoIntent = new Intent(getActivity(), PickupInfoActivity.class);

        PendingIntent pendingIntent =
                TaskStackBuilder.create(getActivity())
                        .addNextIntentWithParentStack(getActivity().getIntent())
                        .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        progressDialog.show();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity());
        builder.setContentIntent(pendingIntent);
        startActivityForResult(pickupInfoIntent, REQUEST_CODE_AVAILABILITY);
    }

    private void preparePickupInfoActivity() {
        // TODO

        if (UserStateHelper.isLoggedIn())
        {
            launchPickupInfoActivity();
        }
        else {
            startActivityForResult(UserStateHelper.getLoginIntent(), UserStateHelper.RC_SIGN_IN);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        progressDialog.dismiss();

        if (requestCode == REQUEST_CODE_WEBVIEW)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                viewAd.setVisibility(View.VISIBLE);
                adUrl =  data.getExtras().getString("url");
                viewAd.setTag(adUrl);
                viewAd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        post = false;
                        Intent webActivity = new Intent(getActivity(), WebViewActivity.class);
                        webActivity.putExtra("post", post);
                        webActivity.putExtra("adLink", (String) viewAd.getTag());

                        startActivity(webActivity);

                    }
                });
                linkAd.setCheckedTogglePosition(1);
            }
            else
            {
                viewAd.setVisibility(View.GONE);
                linkAd.setCheckedTogglePosition(0);
            }
        }
        else if (requestCode == REQUEST_CODE_AVAILABILITY) {
            if (resultCode == Activity.RESULT_OK) {
                // TODO availability / publication code
                Address pickupAddress = new Address(data.getStringExtra("ADDRESS"),
                        data.getDoubleExtra("LAT", 0),
                        data.getDoubleExtra("LNG", 0));
                availabilityDates = data.getStringArrayListExtra("DATES");

                createDelivery(pickupAddress);
            }
            else {
                nextButton.setEnabled(true);
            }
        }

        else if (requestCode == UserStateHelper.RC_SIGN_IN) {
            if (resultCode == Activity.RESULT_OK) {
                checkUsernameAsociated(UserStateHelper.getUID());
            }
        }
        else if (requestCode == UserStateHelper.RC_REGISTER) {
            if (resultCode == Activity.RESULT_OK) {
                launchPickupInfoActivity();
            }
            else {
                UserStateHelper.signOut();
            }
        }
    }
    private void createDelivery(Address pickup) {
        String userUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        progressDialog.show();
        if (isPrivate) {
            CreateDeliveryPrivateBody createDeliveryPrivateBody =
                    new CreateDeliveryPrivateBody(adUrl == null ? "" : adUrl,
                            userUID,
                            pickup,
                            itemNameInput.getText().toString().trim(),
                            ((float)itemPriceInput.getRawValue()) * 0.01f,
                            itemDescInput.getText().toString().trim(),
                            receiverUsername.getEditableText().toString().trim());
            APIClient apiClient = APIServiceGenerator.createService(APIClient.class);
            apiClient.createDeliveryPrivate(createDeliveryPrivateBody).enqueue(new Callback<CreateDeliveryResponse>() {
                @Override
                public void onResponse(Call<CreateDeliveryResponse> call, Response<CreateDeliveryResponse> response) {
                    if (response.isSuccessful()) {
                        if (!imagePath.isEmpty()) {
                            if (response.isSuccessful()) {
                                long id = response.body().id;
                                uploadPhoto(imagePath, id);
                                setAvailabiltyDates(availabilityDates, id);
                            }
                        }
                    }
                    else {
                        Toast.makeText(getActivity(), R.string.toast_failed, Toast.LENGTH_SHORT).show();
                        nextButton.setEnabled(true);
                        progressDialog.dismiss();
                    }
                }

                @Override
                public void onFailure(Call<CreateDeliveryResponse> call, Throwable t) {
                    Toast.makeText(getActivity(), R.string.toast_failed, Toast.LENGTH_SHORT).show();
                    nextButton.setEnabled(true);
                }
            });
        }
        else {
            CreateDeliveryPublicBody createDeliveryPublicBody =
                    new CreateDeliveryPublicBody(adUrl == null ? "" : adUrl,
                            itemNameInput.getText().toString().trim(),
                            itemDescInput.getText().toString().trim(),
                            pickup,
                            ((float)itemPriceInput.getRawValue()) *0.01f,
                            getTags(),
                            UserStateHelper.getUID());
            APIClient apiClient = APIServiceGenerator.createService(APIClient.class);
            apiClient.createDeliveryPublic(createDeliveryPublicBody).enqueue(new Callback<CreateDeliveryResponse>() {
                @Override
                public void onResponse(Call<CreateDeliveryResponse> call, Response<CreateDeliveryResponse> response) {
                    if (!imagePath.isEmpty()) {
                        if (response.isSuccessful()) {
                            long id = response.body().id;
                            uploadPhoto(imagePath, id);
                            setAvailabiltyDates(availabilityDates, id);
                        }
                    }
                }

                @Override
                public void onFailure(Call<CreateDeliveryResponse> call, Throwable t) {
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), R.string.toast_failed, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    private List<CreateDeliveryPublicBody.Tag> getTags() {
        List<CreateDeliveryPublicBody.Tag> tags = new ArrayList<>();
        for (String t : tagGroup.getTags()) {
            tags.add(new CreateDeliveryPublicBody.Tag(t));
        }
        return tags;
    }

    private void checkUsernameAsociated(String user_uid) {
        APIClient apiClient = APIServiceGenerator.createService(APIClient.class);
        UserUIDBody body = new UserUIDBody(user_uid);
        progressDialog.show();
        apiClient.checkPaymentInfoExists(body).enqueue(new Callback<CheckPaymentInfoResponse>() {
            @Override
            public void onResponse(Call<CheckPaymentInfoResponse> call, Response<CheckPaymentInfoResponse> response) {
                progressDialog.dismiss();
                if (response.body().payment_exists) {
                }
                else {
                    Intent registerIntent = new Intent(getActivity(), RegisterActivity.class);
                    startActivityForResult(registerIntent, UserStateHelper.RC_REGISTER);
                }
            }

            @Override
            public void onFailure(Call<CheckPaymentInfoResponse> call, Throwable t) {
                call.enqueue(this);
            }
        });
    }

    private void setAvailabiltyDates(List<String> dates, long deliveryId) {
        APIClient apiClient = APIServiceGenerator.createService(APIClient.class);
        String url = UrlHelper.getViewSetUrl(isPrivate, deliveryId) + "/availability/";
        Set<AvailabiltyPeriodBody> body = new HashSet<>();
        for (String d : dates) {
            body.add(new AvailabiltyPeriodBody(d, getString(R.string.simple_date_format)));
        }
        apiClient.setAvailabilityDates(url, body).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    availabilitySet = true;
                    if (availabilitySet && photoUploaded) {
                        progressDialog.dismiss();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                call.enqueue(this);
            }
        });
    }
    private void uploadPhoto(String imagePath, long deliveryId) {
        File file = new File(imagePath);
        FileUploadService fileUploadService = APIServiceGenerator.createService(FileUploadService.class);
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
        String url = UrlHelper.getViewSetUrl(isPrivate, deliveryId) + "/photos/";
        fileUploadService.upload(url, body).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    photoUploaded = true;
                    if (availabilitySet && photoUploaded) {
                        progressDialog.dismiss();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                call.enqueue(this);
            }
        });
    }
}
