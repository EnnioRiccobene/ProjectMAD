package com.madgroup.appcompany;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.madgroup.sdk.SmartLogger;

import java.util.ArrayList;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {

    private EditText editCategory;
    private String[] listItems;
    private boolean[] checkedItems;
    private ArrayList<Integer> mUserItems = new ArrayList<>();
    private int categoriesCount = 0;

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

        editCategory = findViewById(R.id.editTextFoodCategory);
        listItems = getResources().getStringArray(R.array.food_categories);
        checkedItems = new boolean[listItems.length];
        categoriesCount = 0;

        editCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
                mBuilder.setTitle(getString(R.string.dialog_title));
                mBuilder.setMultiChoiceItems(listItems, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int position, boolean isChecked) {
                        if (isChecked) {
                            if (categoriesCount < 3) {
                                if (!mUserItems.contains(position)) {
                                    mUserItems.add(position);
                                    categoriesCount++;
                                }
                            } else {
                                checkedItems[position] = false;
                                Toast.makeText(getApplicationContext(), getString(R.string.toast_checkbox_limit), Toast.LENGTH_SHORT).show();
                            }
                        } else if (mUserItems.contains(position)) {
                            int a = mUserItems.indexOf(position);
                            mUserItems.remove(a);
                            categoriesCount--;
                        }
                    }
                });

                mBuilder.setCancelable(false);
                mBuilder.setPositiveButton(getString(R.string.ok_label), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String item = "";
                        for (int i = 0; i < mUserItems.size(); i++) {
                            item = item + listItems[mUserItems.get(i)];
                            if (i != mUserItems.size() - 1) {
                                item = item + ", ";
                            }
                        }
                        editCategory.setText(item);
                    }
                });

                mBuilder.setNeutralButton(getString(R.string.clear_all_label), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (int i = 0; i < checkedItems.length; i++) {
                            checkedItems[i] = false;
                            mUserItems.clear();
                            categoriesCount = 0;
                            editCategory.setText("");
                        }
                    }
                });

                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            }
        });

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
        switch (item.getItemId()) {
            // Edit icon
            case R.id.editIcon:
                if (!modifyingInfo) {         // I pressed for modifying data
                    modifyingInfo = true;
                    setFieldClickable();
                } else {                      // I pressed to come back
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
        if (modifyingInfo) {
            modifyingInfo = false;
            setFieldUnclickable();
            saveFields();
        } else
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
        editCategory.setEnabled(false);
    }

    private void setFieldClickable() {
        name.setEnabled(true);
        email.setEnabled(true);
        password.setEnabled(true);
        phone.setEnabled(true);
        address.setEnabled(true);
        additionalInformation.setEnabled(true);
        editCategory.setEnabled(true);
    }

    private void loadFields() {
        if (prefs.contains("Name"))
            name.setText(prefs.getString("Name", ""));
        if (prefs.contains("Email"))
            email.setText(prefs.getString("Email", ""));
        if (prefs.contains("Password"))
            password.setText(prefs.getString("Password", ""));
        if (prefs.contains("Phone"))
            phone.setText(prefs.getString("Phone", ""));
        if (prefs.contains("Address"))
            address.setText(prefs.getString("Address", ""));
        if (prefs.contains("Information"))
            additionalInformation.setText(prefs.getString("Information", ""));
        if (prefs.contains("FoodCounter"))
            categoriesCount = prefs.getInt("FoodCounter", 0);
        if (prefs.contains("checkedItems_size")){
            int size = prefs.getInt("checkedItems_size", 0);
            for (int i = 0; i < size; i++) {
                checkedItems[i] = prefs.getBoolean("checkedItems_" + i, false);
                SmartLogger.d("LoadFields: array["+i+"] = " + checkedItems[i]);
            }
            int listsize = prefs.getInt("mUserItems_size", 0);
            for (int i = 0; i < listsize; i++) {
                mUserItems.add(prefs.getInt("listItems_" + i, 0));
            }
        }
        if (prefs.contains("EditCategory"))
            editCategory.setText(prefs.getString("EditCategory", ""));
    }

    private void saveFields() {
        editor.putString("Name", name.getText().toString());
        editor.putString("Email", email.getText().toString());
        editor.putString("Password", password.getText().toString());
        editor.putString("Phone", phone.getText().toString());
        editor.putString("Address", address.getText().toString());
        editor.putString("Information", additionalInformation.getText().toString());
        editor.putInt("FoodCounter", categoriesCount);
        editor.putInt("checkedItems_size", checkedItems.length);
        editor.putInt("mUserItems_size", mUserItems.size());
        for(int i = 0; i < checkedItems.length; i++) {
            editor.putBoolean("checkedItems_" + i, checkedItems[i]);
            SmartLogger.d("SaveFields: array["+i+"] = " + checkedItems[i]);
        }
        for(int i = 0; i < mUserItems.size(); i++){
            editor.putInt("listItems_" + i, mUserItems.get(i));
        }
        editor.putString("EditCategory", editCategory.getText().toString());
        editor.apply();


    }
}
