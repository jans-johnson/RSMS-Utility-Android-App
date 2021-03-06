package com.jns.rsmsutility.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jns.rsmsutility.R;
import com.jns.rsmsutility.adapters.WebHandler;

import java.math.RoundingMode;
import java.text.DecimalFormat;


public class AttendanceActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    TextView tvtotal,tvLeave,tvApproved,tvDuty,tvDutyAtt;
    ListView lvHours;
    WebView wvtable;
    Spinner spinnersem;
    SetAttendance attendanceObj;

    Button btnshowtable,btnshowstat;


    FragmentManager fragmentManager;
    Fragment tableFragment,listFragment,btnFragment;

    private static DecimalFormat df = new DecimalFormat("0.00");

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
            table= WebHandler.setAttendanceTable(url);

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

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AttendanceActivity.this, android.R.layout.simple_list_item_1, WebHandler.hournumber);
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
        actionBar.setDisplayHomeAsUpEnabled(true);

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

        btnshowtable.setOnClickListener(v -> fragmentManager.beginTransaction()
                .hide(listFragment)
                .show(btnFragment)
                .show(tableFragment)
                .commit());

        btnshowstat.setOnClickListener(v -> fragmentManager.beginTransaction()
                .show(listFragment)
                .show(btnFragment)
                .hide(tableFragment)
                .commit());

        try {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, WebHandler.listsem);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnersem.setAdapter(adapter);
        } catch (NullPointerException e)
        {
            Toast.makeText(AttendanceActivity.this,"Invalid Login Id/ Password",Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(AttendanceActivity.this,com.jns.rsmsutility.activities.LoginActivity.class);
            startActivityForResult(intent, 3);
        }

        spinnersem.setOnItemSelectedListener(this);

        lvHours.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                btn_showMessage(Integer.parseInt(parent.getItemAtPosition(position).toString().split(":")[1].trim()),parent.getItemAtPosition(position).toString().split(":")[0].trim());
            }
        });
    }
    float percentage;
    int total ;

    public void btn_showMessage(int leave,String subject){
        final AlertDialog.Builder alert = new AlertDialog.Builder(AttendanceActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.percentage_dialog,null);
        final EditText txt_inputText = (EditText)mView.findViewById(R.id.txt_input);
        Button btn_cancel = (Button)mView.findViewById(R.id.btn_cancel);
        Button btn_okay = (Button)mView.findViewById(R.id.btn_okay);
        TextView subjectTv=(TextView) mView.findViewById(R.id.subjectTv);
        TextView percentageTv=(TextView) mView.findViewById(R.id.percentageTv);
        percentageTv.setVisibility(View.INVISIBLE);
        subjectTv.setText(subject);
        alert.setView(mView);
        final AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(false);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        btn_okay.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                df.setRoundingMode(RoundingMode.DOWN);
                total =Integer.parseInt(txt_inputText.getText().toString());
                percentage= (float) (((float)(total-leave)/(float)total)*100.0);
                percentageTv.setText(df.format(percentage)+" %");
                percentageTv.setVisibility(View.VISIBLE);
            }
        });
        alertDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id==android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}