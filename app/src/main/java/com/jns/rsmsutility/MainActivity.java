package com.jns.rsmsutility;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import hotchemi.android.rate.AppRate;


public class MainActivity extends AppCompatActivity {
    String name;
    SharedPreferences sharedPreferences;
    JsoupTest jsoupTest;
    TextView tvname,tvuid,tvpassword;
    ImageView ivpic;
    CardView cvattendance,cvinternalmarks,cvsessionalmarks;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.logout)
        {
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.clear();
            editor.apply();
            Intent intent=new Intent(MainActivity.this,com.jns.rsmsutility.LoginActivity.class);
            startActivityForResult(intent, 3);
        }
        return super.onOptionsItemSelected(item);
    }

    //method to save the credentials to sharedPreferences to be reused
    public void saveCred()
    {
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("uid",WebHandler.user);
        editor.putString("pass",WebHandler.pass);
        editor.apply();
    }

    //method to obtain credentials from sharedPreferences
    public void getCred()
    {
        WebHandler.user=sharedPreferences.getString("uid","");
        WebHandler.pass=sharedPreferences.getString("pass","");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //to prevent the activity from going in landscape mode
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.activity_main);



        AppRate.with(this)
                .setInstallDays(5)
                .setLaunchTimes(6)
                .setRemindInterval(1)
                .monitor();
        AppRate.showRateDialogIfMeetsConditions(this);

        ivpic=findViewById(R.id.ivpic);
        tvname=findViewById(R.id.tvname);
        tvuid=findViewById(R.id.tvuid);
        tvpassword=findViewById(R.id.tvpassword);

        cvattendance=findViewById(R.id.cvattendance);
        cvinternalmarks=findViewById(R.id.cvinternalmarks);
        cvsessionalmarks=findViewById(R.id.cvsessionalmarks);

        sharedPreferences=getSharedPreferences("login",MODE_PRIVATE);


            if(!sharedPreferences.contains("pass")) {
                Intent intent=new Intent(MainActivity.this,com.jns.rsmsutility.LoginActivity.class);
                startActivityForResult(intent, 3);
            }
            else {
                //setting info in the app if previously logged in
                getCred();
                jsoupTest=new JsoupTest(WebHandler.user,WebHandler.pass);
                jsoupTest.execute();
            }


        //setting on click listener of Attendance CardView
        cvattendance.setOnClickListener(v -> {
            Intent intent1=new Intent(MainActivity.this, AttendanceActivity.class);
            startActivity(intent1);
        });

        cvinternalmarks.setOnClickListener(v -> {
            Intent intent=new Intent(MainActivity.this,InternalMarksActivity.class);
            startActivity(intent);
        });

        cvsessionalmarks.setOnClickListener(v -> {
            Intent intent=new Intent(MainActivity.this,SessionalMarksActivity.class);
            startActivity(intent);
        });

    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //coming back from the login activity
        if(requestCode==3)
        {
            if (resultCode==RESULT_OK)
            {
                assert data != null;
                WebHandler.user=data.getStringExtra("uid");
                WebHandler.pass=data.getStringExtra("pass");


                jsoupTest=new JsoupTest(WebHandler.user,WebHandler.pass);
                jsoupTest.execute();


            }
        }
    }



    //for fetching data (This specifically for authentication)
    @SuppressLint("StaticFieldLeak")
    public class JsoupTest extends AsyncTask<Void,Void,Void>
    {
        private String username;
        private String password;

        ProgressDialog dialog;

        public JsoupTest(String username, String password) {
            this.username = username;
            this.password = password;

        }

        @Override
        protected Void doInBackground(Void... voids) {
            //checking if the Credentials entered by the user is correct
            name=WebHandler.getAuth(username,password);
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //to show the Loading dialouge box, while data is fetched
            dialog=new ProgressDialog(MainActivity.this);
            dialog.setTitle("login");
            dialog.setMessage("Please wait while loading...");
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            //checking the authentication of the user
            if(name.equals(" ")) {
                Toast.makeText(MainActivity.this,"Invalid Login Id/ Password",Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(MainActivity.this,com.jns.rsmsutility.LoginActivity.class);
                startActivityForResult(intent, 3);
            }
            else if(name.equals("x")){
                cvattendance.setEnabled(false);
                cvinternalmarks.setEnabled(false);
                cvsessionalmarks.setEnabled(false);
                Toast.makeText(MainActivity.this,"Please Connect to the Internet and Try again !!",Toast.LENGTH_LONG).show();
            }
            else
            {
                saveCred();
                tvname.setText(name);
                tvuid.setText(WebHandler.user);
                tvpassword.setText(WebHandler.pass);
                ivpic.setImageBitmap(WebHandler.image);
            }
            dialog.dismiss();
        }
    }


}