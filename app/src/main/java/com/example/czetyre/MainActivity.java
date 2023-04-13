package com.example.czetyre;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, GetData.AsyncResponse{

    private EditText etLogin;
    private EditText etPass;
    private Button btnSubmit;
    private String login, password, url_link;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etLogin=findViewById(R.id.eTLogin);
        etPass=findViewById(R.id.eTPass);
        btnSubmit=findViewById(R.id.btnSend);
        btnSubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        login=etLogin.getText().toString();
        password=etPass.getText().toString();
        url_link="http://a0805137.xsph.ru/scriptUserProfile.php";

        new GetData(this).execute(login, password, url_link);

        /*Intent intent=new Intent(this, ViewActivity.class);
        intent.putExtra("login",etLogin.getText().toString());
        intent.putExtra("password",etPass.getText().toString());
        startActivity(intent);*/
    }
    @Override
    public void processFinish(String output){
        Intent intent=new Intent(this, ViewActivity.class);
        intent.putExtra("full_name", output);
        startActivity(intent);
    }
}
