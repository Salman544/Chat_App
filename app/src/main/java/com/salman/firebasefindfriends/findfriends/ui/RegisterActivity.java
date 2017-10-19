package com.salman.firebasefindfriends.findfriends.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.salman.firebasefindfriends.R;
import com.salman.firebasefindfriends.findfriends.pojo.EmailVerification;
import com.salman.firebasefindfriends.findfriends.pojo.FindUser;
import com.salman.firebasefindfriends.findfriends.pojo.UserInfo;
import com.salman.firebasefindfriends.findfriends.rest_api.EmailRestApi;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterActivity extends AppCompatActivity {


    private static final String TAG = "RegisterActivity";
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseReference;
    private StorageReference mStorageReference;
    private EditText mUserName,mEmail,mPassword,mConfirmPass,mOccupation;
    private TextInputLayout mTextUserName,mTextEmail,mTextConfirm,mTextPassword,mTextOccupation;
    private ProgressDialog mProgressDialog;
    private Uri mPhotoLink = null;
    private int CAMERA_REQUEST=111;
    private int GALLERY_REQUEST=123;
    private String mCurrentImagePath = "null";
    private String mCurrentTime = "null";
    private String mLink = "null";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mStorageReference = FirebaseStorage.getInstance().getReference();
        mProgressDialog = new ProgressDialog(RegisterActivity.this,ProgressDialog.THEME_HOLO_DARK);


        mUserName = (findViewById(R.id.user_name_register));
        mEmail = (findViewById(R.id.email_register));
        mOccupation = (findViewById(R.id.register_occupation));
        mPassword = (findViewById(R.id.pass_register));
        mConfirmPass = (findViewById(R.id.confirm_pass_register));
        mTextUserName = (findViewById(R.id.user_name_text_input));
        mTextEmail = (findViewById(R.id.email_register_text_input));
        mTextOccupation = (findViewById(R.id.register_occupation_text_input));
        mTextPassword = (findViewById(R.id.pass_register_text_input));
        mTextConfirm = (findViewById(R.id.confirm_pass_register_text_input));

        checkTextInput();
    }


    private void checkEmail() {

        String baseUrl = "http://apilayer.net/";
        String apikey = "87213344050aed41bd53fdba990b380a";

        if(checkEditext())
        {
            Retrofit.Builder retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create());

            Retrofit rt = retrofit.build();

            EmailRestApi api = rt.create(EmailRestApi.class);
            Call<EmailVerification> call = api.getEmailVerificationData(
                    apikey,
                    mEmail.getText().toString(),
                    1,1
            );


            setProgressDialog(true,"Verifying Email");

            call.enqueue(new Callback<EmailVerification>() {
                @Override
                public void onResponse(Call<EmailVerification> call, Response<EmailVerification> response) {

                    EmailVerification email = response.body();
                    setProgressDialog(true,"Email Verified");
                    registerUser(email);
                }

                @Override
                public void onFailure(Call<EmailVerification> call, Throwable t) {

                    Log.e(TAG, "onFailure: "+t.getMessage());
                    setProgressDialog(false,"Email Verification Failed");
                    Toast.makeText(getApplicationContext(),"Email Verification Failed",Toast.LENGTH_LONG).show();
                }
            });

        }
    }

    private void registerUser(final EmailVerification emailVerification) {


        if(emailVerification.isMx_found()&&emailVerification.isSmtp_check())
        {
            Toast.makeText(getApplicationContext(),"Valid Email",Toast.LENGTH_LONG).show();
            setProgressDialog(true,"Registering ....");

            mAuth.createUserWithEmailAndPassword(
                    emailVerification.getEmail(),mPassword.getText().toString()
            ).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    setProgressDialog(false,"User Created");
                    if(task.isSuccessful())
                    {
                        FirebaseUser user = mAuth.getCurrentUser();
                        String id = user.getUid();

                        UserInfo info = new UserInfo(
                                mUserName.getText().toString(),
                                emailVerification.getEmail(),
                                id,"Online",mLink,"Rawalpindi, Pakistan",
                                mOccupation.getText().toString(),"null","0",
                                "0","0"
                        );

                        // add User
                        mDatabaseReference.child("Users").child(id).setValue(info);
                        //findUser

                        FindUser findUser = new FindUser(mUserName.getText().toString(),mLink,id);

                        mDatabaseReference.child("FindUsers").child(id)
                                .setValue(findUser);

                        verifyEmail();
                    }
                }
            });
        }
        else
        {
            mTextEmail.setErrorEnabled(true);
            mTextEmail.setError("Invalid Email");
            mEmail.setError("Do you mean : "+emailVerification.getDid_you_mean());
        }
    }


    private boolean checkEditext()
    {
        View focusView = null;
        boolean check = true;
        String userName = mUserName.getText().toString();
        String email = mEmail.getText().toString();
        String occupation = mOccupation.getText().toString();
        String pass = mPassword.getText().toString();
        String confirmPass = mConfirmPass.getText().toString();

        if(userName.isEmpty())
        {
            mTextUserName.setErrorEnabled(true);

            if(mUserName.getText().toString().length()<8)
            {
                mTextUserName.setError("Name is short");
            }
            else
            mTextUserName.setError("This field is required");


            focusView = mUserName;
            check = false;
        }
        else if(email.isEmpty()||!mEmail.getText().toString().contains("@"))
        {
            mTextEmail.setErrorEnabled(true);
            if(!mEmail.getText().toString().contains("@"))
            {
                mTextEmail.setError("Invalid Email");
            }
            else
                mTextEmail.setError("This field is required");


            focusView = mEmail;
            check = false;

        }
        else if(occupation.isEmpty())
        {
            mTextOccupation.setErrorEnabled(true);
            mTextOccupation.setError("This field is required");

            focusView = mOccupation;
            check = false;

        }
        else if(pass.isEmpty()||pass.length()<8)
        {
            mTextPassword.setErrorEnabled(true);

            if(pass.isEmpty())
                mTextPassword.setError("This field is required");
            else
                mTextPassword.setError("Password is short, Should be  greater than 8 character");

            focusView = mPassword;
            check = false;
        }
        else if(!pass.equals(confirmPass))
        {
            mTextConfirm.setErrorEnabled(true);
            mTextConfirm.setError("Password does not match");

            focusView = mTextConfirm;
            check = false;
        }


        if(!check)
        {
            focusView.requestFocus();
        }

        return check;
    }

    private void checkTextInput()
    {

        mUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


                if(mUserName.getText().toString().length()<8)
                {
                    mTextUserName.setErrorEnabled(true);
                    mTextUserName.setError("Name is short");
                }
                else
                    mTextUserName.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if(!mEmail.getText().toString().contains("@")||mEmail.getText().toString().length()<8)
                {
                    mTextEmail.setErrorEnabled(true);
                    mTextEmail.setError("Invalid Email");
                }
                else
                    mTextEmail.setErrorEnabled(false);

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });



        mPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                String pass = mPassword.getText().toString();
                if(pass.length()<8)
                {
                    mTextPassword.setErrorEnabled(true);
                    mTextPassword.setError("Password is Short");
                }
                else
                    mTextPassword.setErrorEnabled(false);

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });



        mConfirmPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if(!mPassword.getText().toString().equals(mConfirmPass.getText().toString()))
                {
                    mTextConfirm.setErrorEnabled(true);
                    mTextConfirm.setError("Password Does Not Match");
                }
                else
                    mTextConfirm.setErrorEnabled(false);

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    private void setProgressDialog(boolean b,String msg)
    {

        mProgressDialog.setMessage(msg);
        mProgressDialog.setCancelable(false);

        if(b)
            mProgressDialog.show();
        else
            mProgressDialog.dismiss();
    }



    public void closeActivity(View view) {

        Intent i = new Intent(RegisterActivity.this,LoginActivity.class);
        startActivity(i);

    }

    public void takePicture(View view) {

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(RegisterActivity.this,
                android.R.layout.simple_list_item_1);
        adapter.add("\tTake Photo");
        adapter.add("\tOpen Gallery");

        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
        builder.setTitle("Profile Picture");
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if(i==0)
                    openCamera();
                else
                    openGallery();

                dialogInterface.dismiss();
            }
        });
        builder.show();

    }

    public void registerBtn(View view) {

        checkEmail();

    }

    private File createFile() throws IOException {
        mCurrentTime = new SimpleDateFormat("yyymmmdd_hhhmmss").format(new Date());

        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(
                ".jpg",
                mCurrentTime,
                dir
        );

        mCurrentImagePath = image.getAbsolutePath();

        return image;
    }

    private void openCamera()
    {
        if(getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA))
        {
            Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            File file = null;

            try
            {
                file = createFile();
            }catch (IOException e)
            {
                e.printStackTrace();
            }

            if(file!=null)
            {
                Uri uri = FileProvider.getUriForFile(
                        this,
                        "com.example.android.fileprovider",
                        file
                );

                i.putExtra(MediaStore.EXTRA_OUTPUT,uri);
                startActivityForResult(i,CAMERA_REQUEST);
            }
        }
    }

    private void openGallery()
    {
        Intent i = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i,GALLERY_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CAMERA_REQUEST && resultCode==RESULT_OK)
        {
            saveImageToCloud(null);
        }
        else if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK)
        {
            Uri uri = data.getData();
            saveImageToCloud(uri);


        }

    }


    private void saveImageToCloud(Uri uri) {

        final ImageView image = findViewById(R.id.register_image_view);
        final ProgressBar pb = findViewById(R.id.register_progress_bar);
        pb.setVisibility(View.VISIBLE);
        final Uri cloudImage;

        if(uri==null)
        {
            File file = new File(mCurrentImagePath);
            cloudImage = Uri.fromFile(file);
            mCurrentImagePath = mCurrentTime;
        }
        else
        {
            cloudImage = uri;
            mCurrentImagePath = uri.getLastPathSegment()+".jpg";
        }

        mStorageReference.child("images/"+mCurrentImagePath)
                .putFile(cloudImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @SuppressLint("VisibleForTests")
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                mPhotoLink = taskSnapshot.getDownloadUrl();
                if(mPhotoLink!=null)
                    mLink = mPhotoLink.toString();
                else
                    mLink = "null";
                image.setImageURI(cloudImage);
                pb.setVisibility(View.INVISIBLE);
            }
        });
    }


    private void verifyEmail()
    {
        final FirebaseUser user = mAuth.getCurrentUser();
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Verify Your Email");
        builder.setMessage("Tap Verify Button To Verify");
        builder.setCancelable(true);


        builder.setPositiveButton("Sign in", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {


                if(user!=null)
                    mAuth.signOut();

                Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });


        builder.setNegativeButton("Verify", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialogInterface, int i) {



                if(user!=null)
                {
                    if(user.isEmailVerified())
                    {
                        Toast.makeText(getApplicationContext(),"Your email is verified",Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        user.sendEmailVerification().addOnCompleteListener(RegisterActivity.this,new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if(task.isSuccessful())
                                {
                                    Toast.makeText(getApplicationContext(),"Verification Email Send," +
                                            "if Verification Email is Not Received Tap Blue Button",Toast.LENGTH_LONG).show();

                                    dialogInterface.dismiss();
                                }

                            }
                        });

                        builder.show();
                    }
                }
            }
        });


        builder.show();
    }


    public void verifyBtn(View view) {

        FirebaseUser user = mAuth.getCurrentUser();

        if(user!=null)
        {
            if(user.isEmailVerified())
                Toast.makeText(getApplicationContext(),"your email is verified",Toast.LENGTH_LONG).show();
            else
                verifyEmail();
        }
        else
            Toast.makeText(getApplicationContext(),"register your self",Toast.LENGTH_LONG).show();

    }


    @Override
    protected void onDestroy() {

        FirebaseUser user = mAuth.getCurrentUser();
        if(user!=null)
        {
            if(!user.isEmailVerified())
                mAuth.signOut();
        }
        super.onDestroy();

    }
}
