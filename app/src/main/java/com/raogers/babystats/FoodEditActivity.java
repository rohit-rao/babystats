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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.raogers.babystats.models.Food;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class FoodEditActivity extends AppCompatActivity implements ValueEventListener, RadioGroup.OnCheckedChangeListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    public static String EXTRA_PATH = "path";

    private DatabaseReference mDatabaseRef;
    private Food mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_edit);
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
        mData = dataSnapshot.getValue(Food.class);
        if (mData == null) {
            mData = new Food();
        }

        GregorianCalendar calendar = (GregorianCalendar) GregorianCalendar.getInstance();
        calendar.setTimeInMillis(mData.getStartTimeInMillis());
        Date time = calendar.getTime();
        ((TextView) findViewById(R.id.activity_food_edit_startdate)).setText(DateFormat.getDateInstance().format(time));
        ((TextView)findViewById(R.id.activity_food_edit_starttime)).setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(time));

        SeekBar nursing = (SeekBar)findViewById(R.id.activity_food_edit_seek_nursing);
        final TextView nursingTime = (TextView)findViewById(R.id.activity_food_edit_time_nursing);
        nursing.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                int minutes = 5 * (i / 5);

                if (minutes == 0) {
                    nursingTime.setText("Nope");
                } else {
                    nursingTime.setText(String.format("%d min", minutes));
                }
                seekBar.setProgress(minutes);
                mData.nursingTimeInMillis = minutes * 60000L;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mDatabaseRef.setValue(mData);
            }
        });
        nursing.setProgress((int)(mData.nursingTimeInMillis / 60000));

        ((Button)findViewById(R.id.activity_food_edit_now_nursing)).setEnabled(
                mData.nursingTimeInMillis <= 0);


        SeekBar milk = (SeekBar)findViewById(R.id.activity_food_edit_seek_milk);
        final TextView milkOz = (TextView)findViewById(R.id.activity_food_edit_amount_milk);
        milk.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                int rounded = 25 * (i /25);

                if (rounded == 0) {
                    milkOz.setText("None");
                } else {
                    milkOz.setText(String.format("%.2f oz", rounded / 100.0));
                }
                seekBar.setProgress(rounded);
                mData.milkOzInMillis = rounded * 10L;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mDatabaseRef.setValue(mData);
            }
        });
        milk.setProgress((int)(mData.milkOzInMillis / 10));

        SeekBar formula = (SeekBar)findViewById(R.id.activity_food_edit_seek_formula);
        final TextView formulaOz = (TextView)findViewById(R.id.activity_food_edit_amount_formula);
        formula.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                int rounded = 25 * (i /25);

                if (rounded == 0) {
                    formulaOz.setText("None");
                } else {
                    formulaOz.setText(String.format("%.2f oz", rounded / 100.0));
                }
                seekBar.setProgress(rounded);
                mData.formulaOzInMillis = rounded * 10L;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mDatabaseRef.setValue(mData);
            }
        });
        formula.setProgress((int) (mData.formulaOzInMillis / 10));

        // Set up the NursingSide radio button group.
        int nursingGroupId = -1;
        if (mData.nursingSide == Food.SIDE_LEFT) {
            nursingGroupId = R.id.activity_food_edit_nursingside_left;
        } else if (mData.nursingSide == Food.SIDE_MOSTLY_LEFT) {
            nursingGroupId = R.id.activity_food_edit_nursingside_mostlyleft;
        } else if (mData.nursingSide == Food.SIDE_BOTH) {
            nursingGroupId = R.id.activity_food_edit_nursingside_both;
        } else if (mData.nursingSide == Food.SIDE_MOSTLY_RIGHT) {
            nursingGroupId = R.id.activity_food_edit_nursingside_mostlyright;
        } else if (mData.nursingSide == Food.SIDE_RIGHT) {
            nursingGroupId = R.id.activity_food_edit_nursingside_right;
        }

        if (nursingGroupId > 0) {
            ((RadioButton)findViewById(nursingGroupId)).setChecked(true);
        }
        RadioGroup nursingSideGroup = (RadioGroup)findViewById(R.id.activity_food_edit_nursingside);
        nursingSideGroup.setOnCheckedChangeListener(this);


        ((EditText)findViewById(R.id.activity_food_edit_comments)).setText(mData.comments);
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        // Bail on error for now.
        finish();
    }

    public void onSetNursingTimeToNow(View view) {
        GregorianCalendar calendar = (GregorianCalendar) GregorianCalendar.getInstance();
        long nursingTime = calendar.getTimeInMillis() - mData.getStartTimeInMillis();

        // Updating the UI will also update mData.
        SeekBar nursing = (SeekBar)findViewById(R.id.activity_food_edit_seek_nursing);
        nursing.setProgress((int)(nursingTime / 60000));
        mDatabaseRef.setValue(mData);

        // TODO(rohitrao): Hide the Now button at this point.
        ((Button)findViewById(R.id.activity_food_edit_now_nursing)).setEnabled(false);
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
        if (checkedId == R.id.activity_food_edit_nursingside_left) {
            mData.nursingSide = Food.SIDE_LEFT;
        } else if (checkedId == R.id.activity_food_edit_nursingside_mostlyleft) {
            mData.nursingSide = Food.SIDE_MOSTLY_LEFT;
        } else if (checkedId == R.id.activity_food_edit_nursingside_both) {
            mData.nursingSide = Food.SIDE_BOTH;
        } else if (checkedId == R.id.activity_food_edit_nursingside_mostlyright) {
            mData.nursingSide = Food.SIDE_MOSTLY_RIGHT;
        } else if (checkedId == R.id.activity_food_edit_nursingside_right) {
            mData.nursingSide = Food.SIDE_RIGHT;
        }

        mDatabaseRef.setValue(mData);
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
        ((TextView)findViewById(R.id.activity_food_edit_starttime)).setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(time));
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
        ((TextView)findViewById(R.id.activity_food_edit_startdate)).setText(DateFormat.getDateInstance().format(time));
    }

    public void onChooseDate(View view) {
        DialogFragment newFragment = new DatePickerFragment();
        Bundle args = new Bundle();
        args.putLong("time", mData.getStartTimeInMillis());
        newFragment.setArguments(args);
        newFragment.show(getFragmentManager(), "datePicker");
    }

}