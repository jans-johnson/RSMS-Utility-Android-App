package com.jns.rsmsutility;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

public class InternalMarksActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    WebView wvimark,wvsubtable;
    Spinner spinnersemimark,spinnertypeimark;
    ArrayList<String> attr,values;
    Button btngotoweb;
    int pos1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internal_marks);
        ActionBar actionBar=getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Internal Marks");

        wvimark=findViewById(R.id.wvimark);
        wvsubtable=findViewById(R.id.wvsubtable);
        spinnersemimark=findViewById(R.id.spinnersemimark);
        spinnertypeimark=findViewById(R.id.spinnertypeimark);
        btngotoweb=findViewById(R.id.btngotoweb);

        wvsubtable.getSettings().setLoadWithOverviewMode(true);
        wvsubtable.getSettings().setUseWideViewPort(true);
        wvsubtable.getSettings().setDefaultFontSize(30);

        wvimark.getSettings().setBuiltInZoomControls(true);

        attr=new ArrayList<String>();
        attr.add("Internal Exam 1");
        attr.add("Internal Exam 2");
        attr.add("Assignment 1");
        attr.add("Assignment 2");
        attr.add("Assignment 3");
        attr.add("Practical exam");

        values=new ArrayList<String>();
        values.add("10");
        values.add("11");
        values.add("12");
        values.add("37");
        values.add("38");
        values.add("44");

        ArrayAdapter<String> semadapter=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,WebHandler.listsem);
        semadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnersemimark.setAdapter(semadapter);

        btngotoweb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.rajagiritech.ac.in/stud/ktu/Student/"));
                startActivity(intent);
            }
        });
        ArrayAdapter<String> typeadapter=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,attr);
        typeadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnertypeimark.setAdapter(typeadapter);

        spinnersemimark.setOnItemSelectedListener(this);
        spinnertypeimark.setOnItemSelectedListener(this);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
        if(parent.getId()==R.id.spinnersemimark)
        {
            pos1=position;
            ArrayAdapter<String> typeadapter=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,attr);
            typeadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnertypeimark.setAdapter(typeadapter);
            spinnertypeimark.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);
        }
        else if(parent.getId()==R.id.spinnertypeimark)
        {
            SetInternals setInternals=new SetInternals("https://www.rajagiritech.ac.in/stud/ktu/Student/Mark.asp?code="+WebHandler.listsem.get(pos1)+"&E_ID="+values.get(position));
            setInternals.execute();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @SuppressLint("StaticFieldLeak")
    public class SetInternals extends AsyncTask<Void,Void,Void>
    {
        String url,table;
        ProgressDialog dialog;

        public SetInternals(String url) {
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
            dialog=new ProgressDialog(InternalMarksActivity.this);
            dialog.setTitle("Fetching Data");
            dialog.setMessage("Please wait while loading...");
            dialog.show();
        }
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(table.equals(" "))
            {
                dialog.dismiss();
                Toast.makeText(InternalMarksActivity.this,"Error Loading Table !!!",Toast.LENGTH_LONG).show();
            }
            else {
                String newhtml_code = Base64.encodeToString(table.getBytes(), Base64.NO_PADDING);
                wvimark.loadData(newhtml_code, "text/html", "base64");

                String subtable = Base64.encodeToString(WebHandler.subjectsTable.getBytes(), Base64.NO_PADDING);
                wvsubtable.loadData(subtable, "text/html", "base64");
                dialog.dismiss();
            }

        }
    }

}