package com.jns.rsmsutility.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
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
import android.widget.Spinner;
import android.widget.Toast;

import com.jns.rsmsutility.R;
import com.jns.rsmsutility.adapters.WebHandler;

import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class InternalMarksActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    WebView wvimark,wvsubtable;
    Spinner spinnersemimark,spinnertypeimark;
    ArrayList<String> attr,values;
    int pos1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internal_marks);
        ActionBar actionBar=getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Internal Marks");
        actionBar.setDisplayHomeAsUpEnabled(true);

        wvimark=findViewById(R.id.wvimark);
        wvsubtable=findViewById(R.id.wvsubtable);
        spinnersemimark=findViewById(R.id.spinnersemimark);
        spinnertypeimark=findViewById(R.id.spinnertypeimark);

        wvsubtable.getSettings().setLoadWithOverviewMode(true);
        wvsubtable.getSettings().setUseWideViewPort(true);
        wvsubtable.getSettings().setDefaultFontSize(30);

        wvimark.getSettings().setBuiltInZoomControls(true);

        attr=new ArrayList<String>();
        values=new ArrayList<String>();

        ArrayAdapter<String> semadapter=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, WebHandler.listsem);
        semadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnersemimark.setAdapter(semadapter);



        spinnersemimark.setOnItemSelectedListener(this);
        spinnertypeimark.setOnItemSelectedListener(this);

    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id==android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {

        if(parent.getId()==R.id.spinnersemimark)
        {
            pos1=position;
            attr.clear();
            values.clear();
            SetType setType=new SetType();
            setType.execute();
            ArrayAdapter<String> typeadapter=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,attr);
            typeadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            typeadapter.add("<Select>");
            values.add("XXX");
            spinnertypeimark.setAdapter(typeadapter);
        }
        else if(parent.getId()==R.id.spinnertypeimark)
        {
            if(position!=0) {
                SetInternals setInternals = new SetInternals("https://www.rajagiritech.ac.in/stud/ktu/Student/Mark.asp?code=" + WebHandler.listsem.get(pos1) + "&E_ID=" + values.get(position));
                setInternals.execute();
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public class SetType extends AsyncTask<Void,Void,Void>
    {
        ProgressDialog dialog;
        Element table;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog=new ProgressDialog(InternalMarksActivity.this);
            dialog.setTitle("Fetching Data");
            dialog.setMessage("Please wait while loading...");
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                table=WebHandler.getType("https://www.rajagiritech.ac.in/stud/ktu/Student/Mark.asp?code="+WebHandler.listsem.get(pos1));
                String[] list=table.toString().split("> <");
                ArrayList listsem = new ArrayList<>(Arrays.asList(list));
                listsem.remove("<select style=\"width: 150px; font-size: 8pt\" id=\"list3\" name=\"E_ID\" class=\"ibox\"");
                listsem.remove("/select>");
                for (int i = 0; i < listsem.size(); i++)
                {
                    values.add(listsem.get(i).toString().split("\"")[1]);
                    attr.add(listsem.get(i).toString().split(">")[1].split("<")[0]);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dialog.dismiss();
        }
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
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
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