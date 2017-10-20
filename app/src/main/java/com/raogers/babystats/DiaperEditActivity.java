package com.raogers.babystats;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.raogers.babystats.models.Diaper;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DiaperEditActivity extends AppCompatActivity implements ValueEventListener, TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

    public static String EXTRA_PATH = "path";

    private DatabaseReference mDatabaseRef;
    private Diaper mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diaper_edit);
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
        mData = dataSnapshot.getValue(Diaper.class);
        if (mData == null) {
            mData = new Diaper();
        }

        GregorianCalendar calendar = (GregorianCalendar) GregorianCalendar.getInstance();
        calendar.setTimeInMillis(mData.getTimeInMillis());
        Date time = calendar.getTime();

        ((TextView) findViewById(R.id.activity_diapers_edit_date)).setText(DateFormat.getDateInstance().format(time));
        ((TextView)findViewById(R.id.activity_diapers_edit_time)).setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(time));
        ((TextView)findViewById(R.id.activity_diapers_edit_comments)).setText(mData.comments);

        CheckBox poopView = (CheckBox)findViewById(R.id.activity_diapers_edit_poop);
        poopView.setChecked(mData.hasPoop);
        poopView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mData.hasPoop = b;
                mDatabaseRef.setValue(mData);
            }
        });

        CheckBox peeView = (CheckBox)findViewById(R.id.activity_diapers_edit_pee);
        peeView.setChecked(mData.hasPee);
        peeView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mData.hasPee = b;
                mDatabaseRef.setValue(mData);
            }
        });

        EditText commentsView = (EditText)findViewById(R.id.activity_diapers_edit_comments);
        commentsView.setText(mData.comments);
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        // For now, give up on error.
        finish();
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
        args.putLong("time", mData.getTimeInMillis());
        newFragment.setArguments(args);
        newFragment.show(getFragmentManager(), "timePicker");
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
        GregorianCalendar calendar = (GregorianCalendar) GregorianCalendar.getInstance();
        calendar.setTimeInMillis(mData.getTimeInMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        mData.setTimeInMillis(calendar.getTimeInMillis());
        mDatabaseRef.setValue(mData);

        Date time = calendar.getTime();
        ((TextView)findViewById(R.id.activity_diapers_edit_time)).setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(time));
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
        calendar.setTimeInMillis(mData.getTimeInMillis());
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DATE, day);
        mData.setTimeInMillis(calendar.getTimeInMillis());
        mDatabaseRef.setValue(mData);

        Date time = calendar.getTime();
        ((TextView)findViewById(R.id.activity_diapers_edit_date)).setText(DateFormat.getDateInstance().format(time));
    }

    public void onChooseDate(View view) {
        DialogFragment newFragment = new DatePickerFragment();
        Bundle args = new Bundle();
        args.putLong("time", mData.getTimeInMillis());
        newFragment.setArguments(args);
        newFragment.show(getFragmentManager(), "datePicker");
    }
}
