package com.madgroup.madproject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 10;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @SuppressLint({"CommitPrefEdits", "SetTextI18n"})
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        Button btn = findViewById(R.id.btn_logout);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        editor = prefs.edit();
        if (prefs.contains("currentUser")) {
            btn.setText(""+ Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getDisplayName() + ". Press to logout");
        } else {
            startLogin();
        }

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLogout();
            }
        });



    }

    private void startLogin() {
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build()
        );

        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setIsSmartLockEnabled(false)
                        .setAvailableProviders(providers)
                        .setLogo(R.drawable.personicon)
                        .build(),
                RC_SIGN_IN);
    }

    @SuppressLint("ShowToast")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                editor.putString("currentUser", user.getEmail());
                editor.apply();
                loadProfile(user);
            } else {
                if (response==null) {
                    // Back button pressed
                } else {
                    Toast.makeText(this, "Error: "+ Objects.requireNonNull(response.getError()).getErrorCode(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void loadProfile(FirebaseUser user) {


    }

    private void startLogout() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(LoginActivity.this, "Logout successful.", Toast.LENGTH_SHORT).show();
                    }
                });
        editor.remove("currentUser");
        editor.apply();
    }

    private void deleteAccount() {
        AuthUI.getInstance()
                .delete(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(LoginActivity.this, "Account deleted.", Toast.LENGTH_SHORT).show();
                    }
                });
    }


}
