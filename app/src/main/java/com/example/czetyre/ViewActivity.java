package com.example.czetyre;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class ViewActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        TextView tv=findViewById(R.id.tvViewLoginPass);
        Intent intent=getIntent();
        String login=intent.getStringExtra("login");
        String pass=intent.getStringExtra("password");
        tv.setText("Login:"+login+", Pass:"+pass);
    }
}
