package com.jns.rsmsutility;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class InternalMarksActivity extends AppCompatActivity{

    WebView wvimark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internal_marks);

        ActionBar actionBar=getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Internal Marks");

        wvimark=findViewById(R.id.wvimark);

        SetInternals setInternals=new SetInternals();
        setInternals.execute();

    }

    @SuppressLint("StaticFieldLeak")
    public class SetInternals extends AsyncTask<Void,Void,Void>
    {
        ProgressDialog dialog;
        @Override
        protected Void doInBackground(Void... voids) {

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
            dialog.dismiss();

        }
    }

}