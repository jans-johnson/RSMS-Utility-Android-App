package com.jns.rsmsutility;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class AttendanceActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    TextView tvtotal,tvLeave,tvApproved,tvDuty,tvDutyAtt;
    ListView lvHours;
    WebView wvtable;
    Spinner spinnersem;
    SetAttendance attendanceObj;

    Button btnshowtable,btnshowstat;


    FragmentManager fragmentManager;
    Fragment tableFragment,listFragment,btnFragment;

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
        String sem=parent.getItemAtPosition(position).toString();
        attendanceObj=new SetAttendance("https://www.rajagiritech.ac.in/stud/ktu/Student/Leave.asp?code="+sem);
        attendanceObj.execute();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @SuppressLint("StaticFieldLeak")
    public class SetAttendance extends AsyncTask<Void,Void,Void>
    {

        private String url,table;
        ProgressDialog dialog;

        public SetAttendance(String url) {
            this.url = url;
        }


        @Override
        protected Void doInBackground(Void... voids) {
            table=WebHandler.setAttendanceTable(url);

            return null;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog=new ProgressDialog(AttendanceActivity.this);
            dialog.setTitle("Fetching Data");
            dialog.setMessage("Please wait while loading...");
            dialog.show();
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(table.equals(" "))
            {
                dialog.dismiss();
                Toast.makeText(AttendanceActivity.this,"Error Loading Table !!!",Toast.LENGTH_LONG).show();
            }
            else {
                String newhtml_code = Base64.encodeToString(table.getBytes(), Base64.NO_PADDING);
                wvtable.loadData(newhtml_code, "text/html", "base64");
                tvtotal.setText("Total Hours lost: " + WebHandler.hrs);
                tvLeave.setText("Leave: " + WebHandler.lhrs);
                tvApproved.setText("Approved Leave: " + WebHandler.aphrs);
                tvDuty.setText("Duty Leave: " + WebHandler.dhrs);
                tvDutyAtt.setText("Duty Attendance: " + WebHandler.dahrs);
                dialog.dismiss();

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(AttendanceActivity.this, android.R.layout.simple_list_item_1, WebHandler.hournumber);
                lvHours.setAdapter(arrayAdapter);
            }

        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Attendance");

        //to prevent the activity from going in landscape mode
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        lvHours = findViewById(R.id.lvHours);
        tvtotal = findViewById(R.id.tvTotal);
        tvLeave = findViewById(R.id.tvLeave);
        tvApproved = findViewById(R.id.tvApproved);
        tvDuty = findViewById(R.id.tvDuty);
        tvDutyAtt = findViewById(R.id.tvDutyAtt);

        wvtable = findViewById(R.id.wvtable);
        spinnersem = findViewById(R.id.spinnersem);

        //Scale the WebView to fit the screen
        wvtable.getSettings().setLoadWithOverviewMode(true);
        wvtable.getSettings().setUseWideViewPort(true);
        //to enable zooming in the activity
        wvtable.getSettings().setBuiltInZoomControls(true);

        btnshowstat = findViewById(R.id.btnshowstat);
        btnshowtable = findViewById(R.id.btnshowtable);

        fragmentManager = getSupportFragmentManager();
        listFragment = fragmentManager.findFragmentById(R.id.listFragment);
        btnFragment = fragmentManager.findFragmentById(R.id.btnFragment);
        tableFragment = fragmentManager.findFragmentById(R.id.tableFragment);

        fragmentManager.beginTransaction()
                .hide(listFragment)
                .show(btnFragment)
                .show(tableFragment)
                .commit();

        btnshowtable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager.beginTransaction()
                        .hide(listFragment)
                        .show(btnFragment)
                        .show(tableFragment)
                        .commit();
            }
        });

        btnshowstat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager.beginTransaction()
                        .show(listFragment)
                        .show(btnFragment)
                        .hide(tableFragment)
                        .commit();
            }
        });

        try {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, WebHandler.listsem);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnersem.setAdapter(adapter);
        } catch (NullPointerException e)
        {
            Toast.makeText(AttendanceActivity.this,"Invalid Login Id/ Password",Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(AttendanceActivity.this,com.jns.rsmsutility.LoginActivity.class);
            startActivityForResult(intent, 3);
        }

        spinnersem.setOnItemSelectedListener(this);



    }
}