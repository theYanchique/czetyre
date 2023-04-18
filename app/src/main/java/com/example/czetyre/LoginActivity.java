package com.example.czetyre;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends Activity implements View.OnClickListener, GetData.AsyncResponse {

    private EditText etLogin;
    private EditText etPass;
    private Button btnSubmit;
    private String login, password, url_link;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etLogin=findViewById(R.id.eTLogin);
        etPass=findViewById(R.id.eTPass);
        btnSubmit=findViewById(R.id.btnSend);
        btnSubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        login=etLogin.getText().toString();
        password=etPass.getText().toString();
        url_link="http://a0805137:ranisikafi@a0805137.xsph.ru/scriptUserProfile.php";
        new GetData(this).execute(login, password, url_link);
    }
    @Override
    public void processFinish(String result){
        if(!result.equals("OSHIBKA")){
            Intent intent=new Intent();
            intent.putExtra(MainActivity.FULLUSERNAME, result);
            setResult(RESULT_OK, intent);
            finish();
        } else {
            Intent intent=new Intent();
            setResult(RESULT_CANCELED, intent);
            finish();
        }
    }
}
