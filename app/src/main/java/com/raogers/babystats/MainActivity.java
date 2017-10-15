package com.raogers.babystats;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void OnDiapersButtonClicked(View view) {
        // TODO(rohitrao): Restructure as pushing a new fragment onto the stack.
        Intent intent = new Intent(this, DiapersListActivity.class);
        startActivity(intent);
    }

    public void OnFoodButtonClicked(View view) {
        Intent intent = new Intent(this, FoodListActivity.class);
        startActivity(intent);
    }

    public void OnSleepButtonClicked(View view) {
        Intent intent = new Intent(this, SleepListActivity.class);
        startActivity(intent);
    }
}
