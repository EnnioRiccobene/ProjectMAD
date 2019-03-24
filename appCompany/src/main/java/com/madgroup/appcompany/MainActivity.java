package com.madgroup.appcompany;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.madgroup.sdk.SmartLogger;


public class MainActivity extends AppCompatActivity {

    private ImageView personalImage;
    private EditText name;
    private EditText email;
    private EditText password;
    private EditText phone;
    private EditText address;
    private EditText additionalInformation;
    private Boolean modifyingInfo;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        editor = prefs.edit();

        // Defining EditText
        personalImage = findViewById(R.id.imagePersonalPhoto);
        name = findViewById(R.id.editTextName);
        email = findViewById(R.id.editTextEmail);
        password = findViewById(R.id.editTextPassword);
        phone = findViewById(R.id.editTextPhone);
        address = findViewById(R.id.editTextAddress);
        additionalInformation = findViewById(R.id.additionalInformation);
        modifyingInfo = false;

        // Set all field to unclickable
        setFieldUnclickable();

        // Load saved information, if exist
        loadFields();
    }

    // What happens if I click on a icon on the menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            // Edit icon
            case R.id.editIcon:
                if(!modifyingInfo){         // I pressed for modifying data
                    modifyingInfo = true;
                    setFieldClickable();
                }
                else{                      // I pressed to come back
                    modifyingInfo = false;
                    setFieldUnclickable();
                    saveFields();
                }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    // What happens if I press back button
    @Override
    public void onBackPressed() {
        if(modifyingInfo){
            modifyingInfo = false;
            setFieldUnclickable();
            saveFields();
        }
        else
            super.onBackPressed();
    }

    // Functions
    private void setFieldUnclickable() {
        name.setEnabled(false);
        email.setEnabled(false);
        password.setEnabled(false);
        phone.setEnabled(false);
        address.setEnabled(false);
        additionalInformation.setEnabled(false);
    }

    private void setFieldClickable() {
        name.setEnabled(true);
        email.setEnabled(true);
        password.setEnabled(true);
        phone.setEnabled(true);
        address.setEnabled(true);
        additionalInformation.setEnabled(true);
    }

    private void loadFields() {
        if(prefs.contains("Name"))
            name.setText(prefs.getString("Name", ""));
        if(prefs.contains("Email"))
            email.setText(prefs.getString("Email", ""));
        if(prefs.contains("Password"))
            password.setText(prefs.getString("Password", ""));
        if(prefs.contains("Phone"))
            phone.setText(prefs.getString("Phone", ""));
        if(prefs.contains("Address"))
            additionalInformation.setText(prefs.getString("Address", ""));
        if(prefs.contains("Information"))
            additionalInformation.setText(prefs.getString("Information", ""));

    }

    private void saveFields() {
        editor.putString("Name",name.getText().toString());
        editor.putString("Email",email.getText().toString());
        editor.putString("Password",password.getText().toString());
        editor.putString("Phone",phone.getText().toString());
        editor.putString("Address",address.getText().toString());
        editor.putString("Information",additionalInformation.getText().toString());
        editor.apply();
    }
}
