package com.raogers.babystats;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    public void OnDiapersButtonClicked(View view) {
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
