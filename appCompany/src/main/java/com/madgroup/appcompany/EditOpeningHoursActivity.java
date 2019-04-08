package com.madgroup.appcompany;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class EditOpeningHoursActivity extends AppCompatActivity {
    public static final String EXTRA_REPLY = "com.madgroup.appcompany.extra.REPLY";

    private ImageView confirmBtn;

    private ArrayList<String> weekdayName = new ArrayList<>();
    private ArrayList<String> dayOldHourPreview = new ArrayList<>();
    private LinkedHashMap<String, String> hourValue = new LinkedHashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_opening_hours); //todo: errore, chash perché non in grado di avviare l'activity. Didn't find class "android.support.v7.widget.RecyclerView" on path: DexPathList[[zip file "/data/app/com.madgroup.appcompany-XMLwVpj7evaMa4bUQvwpew==/base.apk"

        dayOldHourPreview = getIntent().getStringArrayListExtra(MainActivity.EXTRA_MESSAGE);

        initWeekDayName();

        confirmBtn = findViewById(R.id.confirmBtn);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmChanges(hourValue);
            }
        });
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

    /*todo: mandare i valori all'activity principale, forse salvandoli su un hashmap (coppia chiave valore) -> ((String)weekday, (String)coppia orari scelti)
    poi farò unaricerca con if(lunedì è presente nella mappa) allora prendo il valore. Metto nella mappa solo quelli con checkbox attiva*/

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
}
