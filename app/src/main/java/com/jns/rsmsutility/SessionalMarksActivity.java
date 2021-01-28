package com.jns.rsmsutility;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class SessionalMarksActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    Spinner spinnersemsess;
    WebView wvsessional,wvsubjects;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sessional_marks);
        spinnersemsess=findViewById(R.id.spinnersemsess);
        wvsessional=findViewById(R.id.wvsessional);
        wvsubjects=findViewById(R.id.wvsubjects);

        //Scale the WebView to fit the screen
        wvsubjects.getSettings().setLoadWithOverviewMode(true);
        wvsubjects.getSettings().setUseWideViewPort(true);

        //Scale the WebView to fit the screen
        //wvsessional.getSettings().setLoadWithOverviewMode(true);
        //wvsessional.getSettings().setUseWideViewPort(true);
        //to enable zooming in the activity
        wvsessional.getSettings().setBuiltInZoomControls(true);

        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,WebHandler.listsem);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnersemsess.setAdapter(adapter);

        spinnersemsess.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
        SetSessional setSessional=new SetSessional("https://www.rajagiritech.ac.in/stud/ktu/Student/Mark_Sessional.asp?code="+parent.getItemAtPosition(position).toString());
        setSessional.execute();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @SuppressLint("StaticFieldLeak")
    public class SetSessional extends AsyncTask<Void,Void,Void>
    {
        ProgressDialog dialog;
        String url,table;

        public SetSessional(String url) {
            this.url = url;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            table=WebHandler.setSessionalMarkTable(url);
            return null;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog=new ProgressDialog(SessionalMarksActivity.this);
            dialog.setTitle("Fetching Data");
            dialog.setMessage("Please wait while loading...");
            dialog.show();
        }
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            String newhtml_code = Base64.encodeToString(table.getBytes(), Base64.NO_PADDING);
            wvsessional.loadData(newhtml_code,"text/html", "base64");

            String subtable = Base64.encodeToString(WebHandler.subjectsTable.getBytes(), Base64.NO_PADDING);
            wvsubjects.loadData(subtable,"text/html", "base64");
            dialog.dismiss();


        }
    }
}