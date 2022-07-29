package com.example.parsestarterapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class PasswordChnageActivity extends AppCompatActivity implements View.OnKeyListener {
    EditText oldPassword;
    EditText newPassword;
    EditText confirmNewPassword;

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {

        if(keyCode== KeyEvent.KEYCODE_ENTER && event.getAction()==KeyEvent.ACTION_DOWN){
            change(v);
        }

        return false;
    }

    public void change(View view){

        String oldPass=oldPassword.getText().toString();
        String newPass=newPassword.getText().toString();
        String confirmNewPass= confirmNewPassword.getText().toString();

        if(oldPass.equals("") || newPass.equals("") || confirmNewPassword.getText().toString().equals("")){
            Toast.makeText(this, "Kindly fill all the given credentials !", Toast.LENGTH_SHORT).show();
        }else{

            // CODE FOR CHANGING PASSWORD!!

            final ParseUser currentUser = ParseUser.getCurrentUser();
            final String userName = ParseUser.getCurrentUser().getUsername();

            ParseUser.logInInBackground(userName, oldPass, new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    if (user != null && newPass.compareTo(confirmNewPass)==0) {

                        currentUser.setPassword(confirmNewPass);
                        currentUser.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    Log.i("Password", "Changed!!");
                                } else {
                                    Log.i("Uncessful", "try");
                                }
                            }
                        });
                        ParseUser.logOut();
                        ParseUser.logInInBackground(userName, newPass, new LogInCallback() {
                            @Override
                            public void done(ParseUser parseUser, ParseException e) {
                                if (e == null) {
                                    Toast.makeText(PasswordChnageActivity.this, "Password Changed Successfully :)", Toast.LENGTH_LONG).show();
                                    finish();
                                } else
                                    Toast.makeText(PasswordChnageActivity.this, "Network Error", Toast.LENGTH_LONG).show();
                            }
                        });
                    }else{
                        Toast.makeText(PasswordChnageActivity.this, "Password Don't Match. Try Again!", Toast.LENGTH_SHORT).show();
                    }


                }
            });



        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_chnage);

        oldPassword=findViewById(R.id.oldPasswordEditText);
        newPassword=findViewById(R.id.newPasswordEditText);
        confirmNewPassword=findViewById(R.id.confirmNewPassword);

        confirmNewPassword.setOnKeyListener(this);

    }


}