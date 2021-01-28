package com.jns.rsmsutility;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class LoginActivity extends AppCompatActivity {
    EditText tvid,tvpass;
    Button loginbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tvid=findViewById(R.id.tvid);
        tvpass=findViewById(R.id.tvpass);
        loginbtn=findViewById(R.id.loginbtn);

        loginbtn.setOnClickListener(v -> {
            if(tvid.getText().toString().isEmpty() || tvpass.getText().toString().isEmpty())
                Toast.makeText(LoginActivity.this,"Enter all the filelds before continuing!!",Toast.LENGTH_SHORT).show();
            else
            {
                    Intent intent = new Intent();
                    intent.putExtra("uid", tvid.getText().toString());
                    intent.putExtra("pass", tvpass.getText().toString());
                    setResult(RESULT_OK, intent);
                    LoginActivity.this.finish();
            }
        });
    }
}