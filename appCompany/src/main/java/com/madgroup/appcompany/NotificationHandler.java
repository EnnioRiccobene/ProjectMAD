package com.madgroup.appcompany;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.tapadoo.alerter.Alerter;

import java.util.ArrayList;
import java.util.HashMap;

public class NotificationHandler {

    DatabaseReference ref;
    Activity activity;
    Context context;
    String notificationTitle;
    String notificationText;
    ArrayList<Boolean> dataset;

    public NotificationHandler(DatabaseReference ref, Activity activity, Context context, String notificationTitle, String notificationText) {
        this.ref = ref;
        this.activity = activity;
        this.context = context;
        this.notificationTitle = notificationTitle;
        this.notificationText = notificationText;
        this.dataset = new ArrayList<>();
    }

    public String getNotificationTitle() {
        return notificationTitle;
    }

    public void setNotificationTitle(String notificationTitle) {
        this.notificationTitle = notificationTitle;
    }

    public String getNotificationText() {
        return notificationText;
    }

    public void setNotificationText(String notificationText) {
        this.notificationText = notificationText;
    }

    public DatabaseReference getRef() {
        return ref;
    }

    public void setRef(DatabaseReference ref) {
        this.ref = ref;
    }

    public Context getContext() {
        return context;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    private void launchNotification(){
        Alerter.create(activity)
                .setTitle(notificationTitle)
                .setText(notificationText)
                .setBackgroundColorRes(R.color.colorAccent) // or setBackgroundColorInt(Color.CYAN)
                .setDuration(10000)
                .enableSwipeToDismiss()
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        context.startActivity(new Intent(context, ReservationActivity.class));
                    }
                })
//        .setOnClickListener(View.OnClickListener {
//            Toast.makeText(this@DemoActivity, "OnClick Called", Toast.LENGTH_LONG).show();
//        })
                .show();

        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(500);
        }

        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(context, notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    ArrayList<Boolean> parseDataset(DataSnapshot dataSnapshot) {

        ArrayList<Boolean> dataset = new ArrayList<>();

        if (dataSnapshot != null && dataSnapshot.getValue() != null) {
            HashMap<String, Object> data = (HashMap<String, Object>) dataSnapshot.getValue();
            dataset.add(Boolean.valueOf(data.get("seen").toString()));
        }
        return dataset;
    }

    public void newOrderListner(){

        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //aggiungere all'ordine un campo booleano "visto"
                //fare query di ordini e metterli in una lista
                //per la lista (basta che accade una volta ed esco dal for): if(Visto = falso) -> launchNotification() e quando apro l'activity reservation nel fragment pendenti faccio update di ordini visto " true
                ArrayList<Boolean> dataset = new ArrayList<>();
                dataset.addAll(parseDataset(dataSnapshot));

                for(int i = 0; i < dataset.size(); i++) {
                    if(!dataset.get(i)) {
                        launchNotification();
                        dataset.clear();
                        break;
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
