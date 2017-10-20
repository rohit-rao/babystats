package com.raogers.babystats;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.raogers.babystats.models.Diaper;

import java.text.DateFormat;
import java.util.Date;

public class DiapersListActivity extends AppCompatActivity {

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mDateText;
        private TextView mTimeText;
        private CheckBox mPoopCheckbox;
        private CheckBox mPeeCheckbox;
        private TextView mCommentsText;

        private String mRefKey;

        public ViewHolder(View itemView) {
            super(itemView);
            mDateText = (TextView)itemView.findViewById(R.id.list_diapers_date);
            mTimeText = (TextView)itemView.findViewById(R.id.list_diapers_time);
            mPoopCheckbox = (CheckBox)itemView.findViewById(R.id.list_diapers_poop);
            mPeeCheckbox = (CheckBox)itemView.findViewById(R.id.list_diapers_pee);
            mCommentsText = (TextView)itemView.findViewById(R.id.list_diapers_comments);
            mRefKey = null;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(itemView.getContext(), DiaperEditActivity.class);
            intent.putExtra(DiaperEditActivity.EXTRA_PATH, mRefKey);
            startActivity(intent);
        }
    }

    private DatabaseReference mDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diapers_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DiapersListActivity.this.onAddEntry();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Set up firebase.
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("demo").child("diapers");

        // Set up the recycler view.
        Query query = mDatabaseRef.orderByChild("timeInNegativeMillis");
        FirebaseRecyclerOptions<Diaper> options =
                new FirebaseRecyclerOptions.Builder<Diaper>()
                .setQuery(query, Diaper.class)
                .setLifecycleOwner(this)
                .build();

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Diaper, ViewHolder>(options) {
            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_diapers_row, parent, false);
                return new ViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(ViewHolder holder, int position, Diaper model) {
                holder.mRefKey = getRef(position).toString();

                ((TextView)holder.itemView.findViewById(R.id.list_diapers_sectionheader)).setVisibility(View.GONE);

                Date date = new Date(model.getTimeInMillis());
                holder.mDateText.setText(DateFormat.getDateInstance().format(date));
                holder.mTimeText.setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(date));

                holder.mPoopCheckbox.setChecked(model.hasPoop);
                holder.mPeeCheckbox.setChecked(model.hasPee);

                holder.mCommentsText.setText(model.comments);
            }
        };

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.content_diapers_list_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }


    private void onAddEntry() {
        DatabaseReference newRef = mDatabaseRef.push();
        Intent intent = new Intent(this, DiaperEditActivity.class);
        intent.putExtra(DiaperEditActivity.EXTRA_PATH, newRef.toString());

        Diaper d = new Diaper();
        newRef.setValue(d);
        startActivity(intent);
    }

}
