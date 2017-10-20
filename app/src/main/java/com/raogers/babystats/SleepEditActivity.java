package com.raogers.babystats;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.raogers.babystats.models.Sleep;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class SleepEditActivity extends AppCompatActivity implements ValueEventListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    public static String EXTRA_PATH = "path";

    private DatabaseReference mDatabaseRef;
    private Sleep mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_edit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (!getIntent().hasExtra(EXTRA_PATH)) {
            finish();
        }

        mDatabaseRef = FirebaseDatabase.getInstance().getReferenceFromUrl(
                getIntent().getStringExtra(EXTRA_PATH));
        mDatabaseRef.addValueEventListener(this);
    }

    @Override
    protected void onPause() {
        if (mData != null) {
            mDatabaseRef.setValue(mData);
        }

        super.onPause();
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        // Rebuild the UI with the latest changes.
        // TODO(rohitrao): This will blow away any in-progress edits.
        mData = dataSnapshot.getValue(Sleep.class);
        if (mData == null) {
            mData = new Sleep();
        }

        GregorianCalendar calendar = (GregorianCalendar) GregorianCalendar.getInstance();
        calendar.setTimeInMillis(mData.getStartTimeInMillis());
        Date time = calendar.getTime();
        ((TextView) findViewById(R.id.activity_sleep_edit_startdate)).setText(DateFormat.getDateInstance().format(time));
        ((TextView)findViewById(R.id.activity_sleep_edit_starttime)).setText(DateFormat.getTimeInstance().format(time));

        SeekBar duration = (SeekBar)findViewById(R.id.activity_sleep_edit_seek_duration);
        final TextView nursingTime = (TextView)findViewById(R.id.activity_sleep_edit_text_duration);
        duration.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                int minutes = 5 * (i / 5);

                nursingTime.setText(String.format("%d min", minutes));
                seekBar.setProgress(minutes);
                mData.durationInMillis = minutes * 60000L;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mDatabaseRef.setValue(mData);
            }
        });
        duration.setProgress((int)(mData.durationInMillis / 60000));

        ((Button)findViewById(R.id.activity_sleep_edit_now_duration)).setEnabled(
                mData.durationInMillis <= 0);

        // Set up the EditText views.
        ((EditText)findViewById(R.id.activity_sleep_edit_comments)).setText(mData.comments);
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    public void onSetDurationToNow(View view) {
        GregorianCalendar calendar = (GregorianCalendar) GregorianCalendar.getInstance();
        long durationInMillis = calendar.getTimeInMillis() - mData.getStartTimeInMillis();

        // Updating the UI will also update mData.
        SeekBar durationBar = (SeekBar)findViewById(R.id.activity_sleep_edit_seek_duration);
        durationBar.setProgress((int)(durationInMillis / 60000));
        mDatabaseRef.setValue(mData);

        // TODO(rohitrao): Hide the Now button at this point.
        ((Button)findViewById(R.id.activity_sleep_edit_now_duration)).setEnabled(false);
    }

    public static class TimePickerFragment extends DialogFragment {
        private Calendar mInitialTime;

        public TimePickerFragment() {
            mInitialTime = GregorianCalendar.getInstance();

        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            if (getArguments().containsKey("time")) {
                mInitialTime.setTimeInMillis(getArguments().getLong("time"));
            }

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), (TimePickerDialog.OnTimeSetListener)getActivity(),
                    mInitialTime.get(Calendar.HOUR_OF_DAY), mInitialTime.get(Calendar.MINUTE),
                    false);
        }
    }

    public void onChooseTime(View view) {
        DialogFragment newFragment = new TimePickerFragment();
        Bundle args = new Bundle();
        args.putLong("time", mData.getStartTimeInMillis());
        newFragment.setArguments(args);
        newFragment.show(getFragmentManager(), "timePicker");
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
        GregorianCalendar calendar = (GregorianCalendar) GregorianCalendar.getInstance();
        calendar.setTimeInMillis(mData.getStartTimeInMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        mData.setStartTimeInMillis(calendar.getTimeInMillis());
        mDatabaseRef.setValue(mData);

        Date time = calendar.getTime();
        ((TextView)findViewById(R.id.activity_sleep_edit_starttime)).setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(time));
    }

    public static class DatePickerFragment extends DialogFragment {
        private Calendar mInitialTime;

        public DatePickerFragment() {
            mInitialTime = GregorianCalendar.getInstance();

            if (getArguments() != null  && getArguments().containsKey("time")) {
                mInitialTime.setTimeInMillis(getArguments().getLong("time"));
            }
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Create a new instance of TimePickerDialog and return it
            return new DatePickerDialog(getActivity(), (DatePickerDialog.OnDateSetListener)getActivity(),
                    mInitialTime.get(Calendar.YEAR), mInitialTime.get(Calendar.MONTH),
                    mInitialTime.get(Calendar.DATE));
        }
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        GregorianCalendar calendar = (GregorianCalendar) GregorianCalendar.getInstance();
        calendar.setTimeInMillis(mData.getStartTimeInMillis());
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DATE, day);
        mData.setStartTimeInMillis(calendar.getTimeInMillis());
        mDatabaseRef.setValue(mData);

        Date time = calendar.getTime();
        ((TextView)findViewById(R.id.activity_sleep_edit_startdate)).setText(DateFormat.getDateInstance().format(time));
    }

    public void onChooseDate(View view) {
        DialogFragment newFragment = new DatePickerFragment();
        Bundle args = new Bundle();
        args.putLong("time", mData.getStartTimeInMillis());
        newFragment.setArguments(args);
        newFragment.show(getFragmentManager(), "datePicker");
    }
}
