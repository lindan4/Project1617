package com.flashitdelivery.flash_it.helpers;

import android.app.Activity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.flashitdelivery.flash_it.R;
import com.flashitdelivery.flash_it.models.DateIdTuple;
import com.flashitdelivery.flash_it.models.Item;
import com.flashitdelivery.flash_it.models.remote.body.AvailabiltyPeriodBody;
import com.flashitdelivery.flash_it.models.remote.body.RequestDeliveryBody;
import com.flashitdelivery.flash_it.models.remote.response.RequestDeliveryResponse;
import com.flashitdelivery.flash_it.util.APIClient;
import com.flashitdelivery.flash_it.util.APIServiceGenerator;
import com.squareup.timessquare.CalendarCellDecorator;
import com.squareup.timessquare.CalendarCellView;
import com.squareup.timessquare.CalendarPickerView;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Lindan on 2016-09-08.
 */
public class ItemReceiveAvailabilityHelper implements CalendarCellDecorator
{
    private MaterialDialog receiveAvailabilityView;
    private ChecklistRevisionHelper checklistRevisionHelper;
    private Activity activity;
    private Item item;
    private CalendarPickerView pickerView;

    private boolean isAnyTime;
    private List<AvailabiltyPeriodBody> availability;
    private List<DateIdTuple> availableDates;

    public ItemReceiveAvailabilityHelper(Activity activity, final Item item, List<AvailabiltyPeriodBody> availability)
    {
        this.setActivity(activity);
        this.setItem(item);
        this.setAvailability(availability);
        this.setAvailableDates(parseAvailability(availability));
        if (availableDates.isEmpty()) {
            isAnyTime = true;
        }
        this.setChecklistRevisionHelper(new ChecklistRevisionHelper(getActivity(), getItem()));
        //this.setItem(item);
        receiveAvailabilityView = new MaterialDialog.Builder(getActivity()).customView(R.layout.receive_availability_view, true)
                .title(getActivity().getResources().getString(R.string.set_receive_availability))
                .titleColor(getActivity().getResources().getColor(R.color.flashIt_red))
                .positiveText(getActivity().getResources().getString(R.string.next_text).toUpperCase())
                .neutralText(getActivity().getResources().getString(R.string.cancel).toUpperCase())
                .positiveColor(getActivity().getResources().getColor(R.color.flashIt_red))
                .canceledOnTouchOutside(false)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull final MaterialDialog dialog, @NonNull DialogAction which)
                    {
                        ChecklistRevisionHelper copy = getChecklistRevisionHelper();
                        if (pickerView.getSelectedDate() == null)
                        {
                            Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.select_date_prompt), Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            copy.inflate();

                            final MaterialDialog checklistViewCopy = copy.getChecklistDialog();

                            final View checklistPositive = checklistViewCopy.getActionButton(DialogAction.POSITIVE);
                            View checklistNeutral = checklistViewCopy.getActionButton(DialogAction.NEUTRAL);
                            checklistPositive.setOnClickListener(new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View view)
                                {
                                    String requestUrl = UrlHelper.getViewSetUrl(item.getIsPrivate(), item.getBackendId()) + "/request/";
                                    APIClient apiClient = APIServiceGenerator.createService(APIClient.class);
                                    apiClient.requestItem(requestUrl,
                                            new RequestDeliveryBody(DateIdTuple.getIdFromList(availableDates, pickerView.getSelectedDate()), checklistRevisionHelper.getChecklist(),
                                                    UserStateHelper.getUID())).enqueue(new Callback<ResponseBody>() {
                                        @Override
                                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                            if (response.isSuccessful()) {
                                                checklistViewCopy.dismiss();
                                                Toast.makeText(getActivity(), "SUCCESS", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                                        }
                                    });
                                }
                            });

                            checklistNeutral.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    checklistViewCopy.dismiss();
                                    receiveAvailabilityView.show();
                                }
                            });
                        }
                    }
                })
                .neutralColor(getActivity().getResources().getColor(R.color.flashIt_red))
                .onNeutral(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.transaction_cancelled), Toast.LENGTH_LONG).show();
                    }
                })
                .build();

        final View calendarView = receiveAvailabilityView.getCustomView();

        final Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, 1);

        pickerView = (CalendarPickerView) calendarView.findViewById(R.id.itemReceiveAvailabilityCalendar);

        if (!isAnyTime) {
            Calendar dayAfterLastAvailableDay = Calendar.getInstance();
            dayAfterLastAvailableDay.setTime(availableDates.get(availableDates.size() - 1).date);
            dayAfterLastAvailableDay.add(Calendar.DAY_OF_YEAR, 1);
            pickerView.init(availableDates.get(0).date, dayAfterLastAvailableDay.getTime()).withHighlightedDates(DateIdTuple.dateTupleListToDateList(availableDates));
        }
        else {
            pickerView.init(new Date(), calendar.getTime()).withSelectedDate(new Date());
        }

        pickerView.setDateSelectableFilter(new CalendarPickerView.DateSelectableFilter() {
            @Override
            public boolean isDateSelectable(Date date) {
                return isDateAvailable(date, availableDates) || isAnyTime;
            }
        });
        pickerView.setOnInvalidDateSelectedListener(new CalendarPickerView.OnInvalidDateSelectedListener() {
            @Override
            public void onInvalidDateSelected(Date date) {
                Toast.makeText(getActivity(), "Unavailable", Toast.LENGTH_SHORT).show();
            }
        });
        this.setReceiveAvailabilityView(receiveAvailabilityView);
    }

    public Activity getActivity()
    {
        return this.activity;
    }

    public Item getItem()
    {
        return this.item;
    }

    public MaterialDialog getReceiveAvailabilityView()
    {
        return this.receiveAvailabilityView;
    }

    public ChecklistRevisionHelper getChecklistRevisionHelper()
    {
        return this.checklistRevisionHelper;
    }

    public void setActivity(Activity activity)
    {
        this.activity = activity;
    }

    public void setItem(Item item)
    {
        this.item = item;
    }

    private void setReceiveAvailabilityView(MaterialDialog receiveAvailabilityView)
    {
        this.receiveAvailabilityView = receiveAvailabilityView;
    }

    private boolean isDateAvailable(Date date, List<DateIdTuple> availableDates) {
        for (DateIdTuple d : availableDates) {
            if (date.getYear() == d.date.getYear() &&
                    date.getMonth() == d.date.getMonth() &&
                    date.getDay() == d.date.getDay()) {
                return true;
            }
        }
        return false;
    }
    // ommits dates that are not now
    private List<DateIdTuple> parseAvailability(List<AvailabiltyPeriodBody> availability) {
        List<DateIdTuple> availableDates = new ArrayList<>();
        try {
            for (AvailabiltyPeriodBody body : availability) {
                DateFormat df = new SimpleDateFormat(activity.getResources().getString(R.string.simple_date_format));
                Date now = Calendar.getInstance().getTime();
                Date date = df.parse(body.time_until);
                if (!date.before(now)) {
                    availableDates.add(new DateIdTuple(date, body.id));
                    Log.i("AVAILDATE", date.toString());
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Collections.sort(availableDates, new Comparator<DateIdTuple>() {
            @Override
            public int compare(DateIdTuple dateT, DateIdTuple t1) {
                return dateT.date.compareTo(t1.date);
            }
        });
        return availableDates;
    }

    public void setChecklistRevisionHelper(ChecklistRevisionHelper checklistRevisionHelper)
    {
        this.checklistRevisionHelper = checklistRevisionHelper;
    }

    public void setAvailableDates(List<DateIdTuple> availableDates) {
        this.availableDates = availableDates;
    }

    public void setAvailability(List<AvailabiltyPeriodBody> availability) {
        this.availability = availability;
    }

    private AvailabiltyPeriodBody getSelectedAvailability() {
        return null;
    }

    public void inflate()
    {
        getReceiveAvailabilityView().show();
    }

    @Override
    public void decorate(CalendarCellView cellView, Date date)
    {
        if (cellView.isSelected())
        {
            TextView cell = cellView.getDayOfMonthTextView();
            cell.setBackgroundColor(getActivity().getResources().getColor(R.color.flashIt_red));
        }

    }
}
