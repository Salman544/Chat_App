package com.salman.firebasefindfriends.findfriends.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.salman.firebasefindfriends.R;

public class LoginActivity extends AppCompatActivity {

    private EditText mEmail;
    private EditText mPassword;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthState;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        mEmail = (findViewById(R.id.email_sign_in));
        mPassword = (findViewById(R.id.pass_sign_in));
        mProgressDialog = new ProgressDialog(LoginActivity.this, ProgressDialog.THEME_HOLO_DARK);

        statusNavigationBarColor();
        checkIfUserSignin();
    }

    private void checkIfUserSignin() {

        mAuthState = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    if(user.isEmailVerified())
                    {
                        Intent i = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(i);
                    }
                    else
                        verifyEmail();
                }
            }
        };
    }

    private void statusNavigationBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    private void setProgressDialog(boolean b) {

        mProgressDialog.setMessage("loading . . .");
        mProgressDialog.setCancelable(false);

        if (b)
            mProgressDialog.show();
        else
            mProgressDialog.dismiss();
    }

    public void signinBtn(View view) {

        View focusView = null;
        boolean check = true;
        String email = mEmail.getText().toString();
        String pass = mPassword.getText().toString();

        if (email.isEmpty() || !email.contains("@")) {
            if (email.isEmpty())
                mEmail.setError("enter email");
            else
                mEmail.setError("Invalid Email");

            focusView = mEmail;
            check = false;

        } else if (pass.isEmpty()) {
            if (pass.isEmpty())
                mPassword.setError("enter email");

            focusView = mPassword;
            check = false;
        }

        if (check) {
            setProgressDialog(true);
            mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(LoginActivity.this,
                    new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            setProgressDialog(false);
                            if (task.isSuccessful()) {

                                FirebaseUser user = mAuth.getCurrentUser();

                                if(user!=null)
                                {
                                    if(user.isEmailVerified())
                                    {
                                        Intent i = new Intent(LoginActivity.this,MainActivity.class);
                                        i.putExtra("email",user.isEmailVerified());
                                        startActivity(i);
                                    }

                                }
                                else
                                    Toast.makeText(getApplicationContext(),"problem in server try again",Toast.LENGTH_LONG).show();

                            } else
                                Toast.makeText(getApplicationContext(), "invalid email or password", Toast.LENGTH_LONG).show();

                        }
                    });
        } else {
            focusView.requestFocus();
        }
    }

    public void signupBtn(View view) {

        Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(i);
    }

    public void facebookSignin(View view) {

        Toast.makeText(getApplicationContext(), "later", Toast.LENGTH_LONG).show();

    }

    public void twitterSignin(View view) {
        Toast.makeText(getApplicationContext(), "later", Toast.LENGTH_LONG).show();

    }

    public void googleSignin(View view) {
        Toast.makeText(getApplicationContext(), "later", Toast.LENGTH_LONG).show();
    }

    public void forgotPassword(View view) {
        Toast.makeText(getApplicationContext(), "later", Toast.LENGTH_LONG).show();
    }


    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthState);

    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mAuth != null)
            mAuth.removeAuthStateListener(mAuthState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("Login", "onDestroy: Called");
    }


    private void verifyEmail()
    {
        final FirebaseUser user = mAuth.getCurrentUser();
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Verify Your Email");
        builder.setMessage("Press Verify Button To Verify");


        builder.setNegativeButton("Verify", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialogInterface, int i) {


                if(user!=null)
                {
                    user.sendEmailVerification().addOnCompleteListener(LoginActivity.this,new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful())
                            {
                                Toast.makeText(LoginActivity.this, "Verification email has been sent to "+user.getEmail(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        builder.show();

    }

}
