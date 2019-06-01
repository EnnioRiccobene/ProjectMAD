package com.madgroup.appcompany;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditOpeningHoursActivity extends AppCompatActivity {
    public static final String EXTRA_REPLY = "com.madgroup.appcompany.extra.REPLY";

    private ArrayList<String> weekdayName = new ArrayList<>();
    private ArrayList<String> dayOldHourPreview = new ArrayList<>();
    private LinkedHashMap<String, String> hourValue = new LinkedHashMap<>();

    private SharedPreferences prefs;
    String notificationTitle = "MAD Company";
    String notificationText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_opening_hours);
        dayOldHourPreview = getIntent().getStringArrayListExtra(ProfileActivity.EXTRA_MESSAGE);

        initWeekDayName();

        setTitle(R.string.opening_hours_title);

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        toolbar.setTitle("");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        notificationText = getResources().getString(R.string.notification_text);
        if (prefs.contains("currentUser")) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference newOrderRef = database.getReference().child("Company").child("Reservation").child("Pending").child("NotifyFlag").child(prefs.getString("currentUser", ""));
            NotificationHandler notify = new NotificationHandler(newOrderRef, this, this, notificationTitle, notificationText);
            notify.newOrderListner();
        }
    }

    private void initWeekDayName(){
        weekdayName.add(getResources().getString(R.string.Monday));
        weekdayName.add(getResources().getString(R.string.Tuesday));
        weekdayName.add(getResources().getString(R.string.Wednesday));
        weekdayName.add(getResources().getString(R.string.Thursday));
        weekdayName.add(getResources().getString(R.string.Friday));
        weekdayName.add(getResources().getString(R.string.Saturday));
        weekdayName.add(getResources().getString(R.string.Sunday));

        initRecycleView();
    }


    private void initRecycleView(){
        RecyclerView recyclerView = findViewById(R.id.hoursrecycleview);
        RecyclerViewHoursAdapter adapter = new RecyclerViewHoursAdapter(this, weekdayName, dayOldHourPreview, (LinkedHashMap<String, String>) hourValue);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public void confirmChanges(LinkedHashMap<String, String> map){

        Intent replyIntent = new Intent();
        Bundle bundle = new Bundle();
        ArrayList<String> week = new ArrayList<>();
        week.add(getResources().getString(R.string.Monday));
        week.add(getResources().getString(R.string.Tuesday));
        week.add(getResources().getString(R.string.Wednesday));
        week.add(getResources().getString(R.string.Thursday));
        week.add(getResources().getString(R.string.Friday));
        week.add(getResources().getString(R.string.Saturday));
        week.add(getResources().getString(R.string.Sunday));

        for(int i = 0; i < week.size(); i++){
            if(map.containsKey(week.get(i))){
                bundle.putString(week.get(i), map.get(week.get(i)));
            }
        }

        replyIntent.putExtras(bundle);
        setResult(RESULT_OK, replyIntent);
        finish();
    }


    // App Bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_opening_hours_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.accept_schedule:
                confirmChanges(hourValue);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
