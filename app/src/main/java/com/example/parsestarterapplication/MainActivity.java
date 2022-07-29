package com.example.parsestarterapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener{

    TextView loginTextView;
    EditText username;
    EditText password;
    Boolean isSignUpModeActive = true;
    Button button;


    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {

        if(keyCode== KeyEvent.KEYCODE_ENTER && event.getAction()==KeyEvent.ACTION_DOWN){
            signUp(v);
        }

        return false;
    }

    @Override
    public void onClick(View v) {

        if(v.getId()==R.id.loginTextView){

            if(isSignUpModeActive){

                isSignUpModeActive=false;
                button.setText("Login");
                loginTextView.setText("Or, Sign Up");

            }else{

                isSignUpModeActive=true;
                button.setText("Sign Up");
                loginTextView.setText("Or, Login");

            }

        }else if(v.getId()==R.id.layout || v.getId()==R.id.imageView){

            //      CODE FOR DISAPPERAING THE KEYBOARD WHEN CLICKING ON THE EMPTY SPACE.

            InputMethodManager inputMethodManager= (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

        }

    }

    // Function for going into another activity

    public void goToUserList(){
        Intent intent=new Intent(getApplicationContext(), LoginPage.class);

        startActivity(intent);
    }

    public void signUp(View view){



        if(username.getText().toString().matches("") || password.getText().toString().matches("")){
            Toast.makeText(MainActivity.this, "Please provide the above credentials!", Toast.LENGTH_SHORT).show();
            Log.i("Ho kya rha hai","chalo  dekhte hai!");
        }else{
            if(isSignUpModeActive) {
                ParseUser user = new ParseUser();
                user.setUsername(username.getText().toString());
                user.setPassword(password.getText().toString());

                user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Log.i("Sign Up", "Successful");
                            Toast.makeText(MainActivity.this, "Signed Up successfully!", Toast.LENGTH_SHORT).show();
                            goToUserList();
                        } else {
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }else{
                ParseUser.logInInBackground(username.getText().toString(), password.getText().toString(), new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {
                        if(user!=null) {
                            Log.i("Login ", "Successful");
                            Toast.makeText(MainActivity.this, "Login Successful, Welcome " + user.getUsername() + " !", Toast.LENGTH_SHORT).show();
                            goToUserList();
                        }else{
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        }

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("ZiyaGram");

        button=findViewById(R.id.button);
        loginTextView= findViewById(R.id.loginTextView);
        username= findViewById(R.id.usernameEditText);
        password= findViewById(R.id.passwordEditText);

        RelativeLayout background=findViewById(R.id.backgroundLayout);
        ImageView logo=findViewById(R.id.imageView);

        background.setOnClickListener(this);
        logo.setOnClickListener(this);

        loginTextView.setOnClickListener(this);
        password.setOnKeyListener(this);

        if(ParseUser.getCurrentUser()!=null){
            goToUserList();
        }



        ParseAnalytics.trackAppOpenedInBackground(getIntent());
    }


}