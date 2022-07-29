package com.example.parsestarterapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class LoginPage extends AppCompatActivity {

    private long pressedTime;

    // back press krne par exit
/*
    @Override
    public void onBackPressed() {

        if (pressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            finish();
        } else {
            Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT).show();
        }
        pressedTime = System.currentTimeMillis();
    }
    
 */

    // back button press krne ke liye

    @Override
    public void onBackPressed() {

        if (pressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            finishAffinity();
          // System.exit(0);
        } else {
            Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT).show();
        }
        pressedTime = System.currentTimeMillis();
    }


    // LOG OUT, Sharing KRNE KA KAMAAL, MENU KE MADAD SE

    public void getPhoto(){
        Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.logOut){
            if(ParseUser.getCurrentUser()!= null) {
                ParseUser.logOutInBackground(new LogOutCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Log.i("Mission ", "Accomplished");
                        } else {
                            e.printStackTrace();
                        }
                    }
                });
            }
            finish();
        }else if(item.getItemId()==R.id.sharePhoto){

            if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                // agar nahi kiya hai permission grant to hum maange ge
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
            }else{
                getPhoto();
            }

        }else if(item.getItemId()==R.id.passwordChange){
            Intent intent=new Intent(getApplicationContext(), PasswordChnageActivity.class);

            startActivity(intent);
        }
         return super.onOptionsItemSelected(item);

    }

    // permission ka result aane par yeh function call hoga

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==1){
            if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                getPhoto();
            }
        }
    }

    // image ka result leke aayega tb ka scene hai

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // getting the result from the media store
        Uri selectedImage= data.getData();

        if(requestCode==1   &&  resultCode== RESULT_OK  && data!=null){

            try{

                //Getting image from the result

                Bitmap bitmap= MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                // parse server par image uploda krne ke liye

                ByteArrayOutputStream stream = new ByteArrayOutputStream();

                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);        // kisme compress krna hai, kitna quality chahiye and output stream

                byte[] byteArray= stream.toByteArray();

                ParseFile file=new ParseFile("image.png", byteArray);               // format bana rhe ki file type kya hoga

                // making image class around this Parse file to store image

                ParseObject object=new ParseObject("Image");
                object.put("image", file);
                object.put("username", ParseUser.getCurrentUser().getUsername());

                object.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e==null){
                            Log.i("Mission", "Accomplished");
                            Toast.makeText(LoginPage.this, "Shared!, Your upload was successful.", Toast.LENGTH_SHORT).show();
                        }else{
                            Log.i("Not possuble","how this happened");
                            Toast.makeText(LoginPage.this, "Sorry, some error occurred. Please try once more!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }catch(Exception e){
                e.printStackTrace();
            }

        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        setTitle("User Feed");

        ListView listView= findViewById(R.id.userListView);

        ArrayList<String> usernames=new ArrayList<>();
       //   test case:  usernames.add("test");

        usernames.add("My uploads!!");

        ArrayAdapter arrayAdapter= new ArrayAdapter(this, android.R.layout.simple_list_item_1, usernames);

        // clicking krne par aage uske uploaded photos nazar aayega

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getApplicationContext(), UserFeedActivity.class);

                if(position==0){

                    intent.putExtra("username", ParseUser.getCurrentUser().getUsername());

                }else {

                    intent.putExtra("username", usernames.get(position));
                }

                startActivity(intent);
            }
        });

        ParseQuery<ParseUser> query= ParseUser.getQuery();

        // line of code for, jo user login hai uska name list mein nahi chahiye

        query.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());

        // list ko order mein daalne ke liye

        query.addAscendingOrder("username");

        // now finally executing the query

        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {

                if(e==null){
                    if(objects.size()>0){

                        for(ParseUser user: objects){
                            usernames.add(user.getUsername());
                        }
                        listView.setAdapter(arrayAdapter);

                    }
                }else{
                    Toast.makeText(LoginPage.this, "Some Error!", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            }
        });


    }
}