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
import com.raogers.babystats.models.Food;

import java.text.DateFormat;
import java.util.Date;

public class FoodListActivity extends AppCompatActivity {

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mDateText;
        private TextView mStartTimeText;
        private TextView mDescriptionText;
        private TextView mCommentsText;

        private String mRefKey;

        public ViewHolder(View itemView) {
            super(itemView);
            mDateText = (TextView)itemView.findViewById(R.id.list_food_date);
            mStartTimeText = (TextView)itemView.findViewById(R.id.list_food_starttime);
            mDescriptionText = (TextView)itemView.findViewById(R.id.list_food_description);
            mCommentsText = (TextView)itemView.findViewById(R.id.list_food_comments);
            mRefKey = null;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(itemView.getContext(), FoodEditActivity.class);
            intent.putExtra(FoodEditActivity.EXTRA_PATH, mRefKey);
            startActivity(intent);
        }
    }

    private DatabaseReference mDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FoodListActivity.this.onAddEntry();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Set up firebase.
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("demo").child("food");

        // Set up the recycler view.
        Query query = mDatabaseRef.orderByChild("startTimeInNegativeMillis");
        FirebaseRecyclerOptions<Food> options =
                new FirebaseRecyclerOptions.Builder<Food>()
                        .setQuery(query, Food.class)
                        .setLifecycleOwner(this)
                        .build();

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Food, FoodListActivity.ViewHolder>(options) {
            @Override
            public FoodListActivity.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_food_row, parent, false);
                return new FoodListActivity.ViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(FoodListActivity.ViewHolder holder, int position, Food model) {
                holder.mRefKey = getRef(position).toString();

                Date date = new Date(model.getStartTimeInMillis());
                holder.mDateText.setText(DateFormat.getDateInstance().format(date));
                holder.mStartTimeText.setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(date));
                holder.mCommentsText.setText(model.comments);

                // Build up a description string.
                boolean inProgress = (model.nursingTimeInMillis <= 0 && model.milkOzInMillis <= 0 && model.formulaOzInMillis <= 0);
                if (inProgress) {
                    holder.mDescriptionText.setText("We're eating!");
                    holder.mDescriptionText.setTextColor(Color.RED);
                } else {
                    // "XX minutes + Y oz milk + Z oz formula"
                    StringBuilder descriptionBuilder = new StringBuilder();

                    if (model.nursingTimeInMillis > 0) {
                        descriptionBuilder.append(String.format("%d minutes", model.nursingTimeInMillis / 60000));
                    }

                    if (model.milkOzInMillis > 0) {
                        if (descriptionBuilder.length() > 0) {
                            descriptionBuilder.append(" + ");
                        }
                        descriptionBuilder.append(String.format("%.2f oz milk", model.milkOzInMillis / 1000.0));
                    }

                    if (model.formulaOzInMillis > 0) {
                        if (descriptionBuilder.length() > 0) {
                            descriptionBuilder.append(" + ");
                        }
                        descriptionBuilder.append(String.format("%.2f oz formula", model.formulaOzInMillis / 1000.0));
                    }

                    holder.mDescriptionText.setText(descriptionBuilder.toString());
                    holder.mDescriptionText.setTextColor(Color.BLACK);
                }
            }
        };

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.content_food_list_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }


    private void onAddEntry() {
        DatabaseReference newRef = mDatabaseRef.push();
        Intent intent = new Intent(this, FoodEditActivity.class);
        intent.putExtra(FoodEditActivity.EXTRA_PATH, newRef.toString());

        Food f = new Food();
        newRef.setValue(f);
        startActivity(intent);
    }

}
