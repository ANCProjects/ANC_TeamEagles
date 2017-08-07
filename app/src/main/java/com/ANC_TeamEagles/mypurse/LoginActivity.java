package com.ANC_TeamEagles.mypurse;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.ui.ResultCodes;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private static final int rcSignIn = 100;
    private Intent loginSuccessIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginSuccessIntent = new Intent(LoginActivity.this, MainActivity.class);
        auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser() != null){

            // useralredy signed in
            startActivity(loginSuccessIntent);
            finish();
        } else {
            startActivityForResult(AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setProviders(
                            AuthUI.EMAIL_PROVIDER,
                            AuthUI.GOOGLE_PROVIDER)
                    .setTheme(R.style.LoginTheme)
                    .setLogo(R.drawable.logo)
                    .build(),
                    rcSignIn);
        }


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == rcSignIn){
            handleSignInResponse(resultCode, data);
            return;
            }
        }


    @MainThread
    private void handleSignInResponse(int resultCode, Intent data) {
        IdpResponse response = IdpResponse.fromResultIntent(data);

        // Successfully signed in
        if (resultCode == RESULT_OK) {
            startActivity(loginSuccessIntent);
            finish();
            return;
        }
        // Sign in failed
        if (resultCode == RESULT_CANCELED) {
            // User pressed back button
            Toast.makeText(getApplicationContext(), "sign_in_cancelled", Toast.LENGTH_SHORT).show();
            return;
        }
        // No internet connection
        if (resultCode == ResultCodes.RESULT_NO_NETWORK) {
            Toast.makeText(getApplicationContext(), "no_internet_connection", Toast.LENGTH_SHORT).show();
            return;
        }
    }



}
