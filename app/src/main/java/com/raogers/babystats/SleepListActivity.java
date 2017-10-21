package com.raogers.babystats;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.raogers.babystats.models.Sleep;

import java.text.DateFormat;
import java.util.Date;

public class SleepListActivity extends AppCompatActivity {

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mDateText;
        private TextView mStartTimeText;
        private TextView mDurationText;
        private TextView mCommentsText;

        private String mRefKey;

        public ViewHolder(View itemView) {
            super(itemView);
            mDateText = (TextView)itemView.findViewById(R.id.list_sleep_date);
            mStartTimeText = (TextView)itemView.findViewById(R.id.list_sleep_starttime);
            mDurationText = (TextView)itemView.findViewById(R.id.list_sleep_duration);
            mCommentsText = (TextView)itemView.findViewById(R.id.list_sleep_comments);
            mRefKey = null;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(itemView.getContext(), SleepEditActivity.class);
            intent.putExtra(SleepEditActivity.EXTRA_PATH, mRefKey);
            startActivity(intent);
        }
    }

    private DatabaseReference mDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SleepListActivity.this.onAddEntry();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Set up firebase.
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("demo").child("sleep");

        // Set up the recycler view.
        Query query = mDatabaseRef.orderByChild("startTimeInNegativeMillis");
        FirebaseRecyclerOptions<Sleep> options =
                new FirebaseRecyclerOptions.Builder<Sleep>()
                        .setQuery(query, Sleep.class)
                        .setLifecycleOwner(this)
                        .build();

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Sleep, ViewHolder>(options) {
            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_sleep_row, parent, false);
                return new ViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(ViewHolder holder, int position, Sleep model) {
                holder.mRefKey = getRef(position).toString();

                Date date = new Date(model.getStartTimeInMillis());
                holder.mDateText.setText(DateFormat.getDateInstance().format(date));
                holder.mStartTimeText.setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(date));
                holder.mCommentsText.setText(model.comments);

                if (model.durationInMillis > 0) {
                    int minutes = (int)((model.durationInMillis) / 60000);
                    holder.mDurationText.setText(String.format("%d minute%s", minutes,
                            minutes == 1 ? "" : "s"));
                    holder.mDurationText.setTextColor(Color.BLACK);
                } else {
                    holder.mDurationText.setText("We're napping!");
                    holder.mDurationText.setTextColor(Color.RED);
                }
            }
        };

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.content_sleep_list_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }


    private void onAddEntry() {
        DatabaseReference newRef = mDatabaseRef.push();
        Intent intent = new Intent(this, SleepEditActivity.class);
        intent.putExtra(SleepEditActivity.EXTRA_PATH, newRef.toString());

        Sleep s = new Sleep();
        newRef.setValue(s);
        startActivity(intent);
    }

}
