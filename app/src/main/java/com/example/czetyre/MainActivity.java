package com.example.czetyre;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tvFullUserName;
    private SharedPreferences sPref;
    public static String MY_PREF="CZETYRE_PREFERENCES_FILE";
    public final static String FULLUSERNAME="fullusername";
    private int code=0;
    private Toast toast;
    private Button btnSendInfo;
    private Button btnGetContact;
    private Intent intentGetContact;
    private final static int REQUEST_CONTACT=1;
    private static final String TAG= "MainActivity";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvFullUserName=findViewById(R.id.tvViewFullName);
        btnSendInfo=findViewById(R.id.btnSendInfo);

        String fullUserName;
        if((fullUserName=loadUserFullNameFromMyPref())==null){
            getUserFullNameFromLoginActivity();
        }else {
            tvFullUserName.setText("Eto zhe "+fullUserName);
            btnSendInfo.setOnClickListener(this);
            intentGetContact=new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            PackageManager packageManager=this.getPackageManager();
            if((packageManager.resolveActivity(intentGetContact, packageManager.MATCH_DEFAULT_ONLY))==null){
                btnGetContact.setEnabled(false);
            }else {
                btnGetContact=findViewById(R.id.btnGetContact);
                btnGetContact.setOnClickListener(this);
            }

        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==btnSendInfo.getId()){
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, "text1");
            intent.putExtra(Intent.EXTRA_SUBJECT, "text2");
            Intent IntentChooser = Intent.createChooser(intent, "text3");
            startActivity(IntentChooser);
        }
        if(view.getId()==btnGetContact.getId()){
            startActivityForResult(intentGetContact,REQUEST_CONTACT);
        }
    }

    private String loadUserFullNameFromMyPref(){
        sPref=getApplicationContext().getSharedPreferences(MY_PREF, MODE_PRIVATE);
        String fullUserName=sPref.getString(FULLUSERNAME, "");
        if (fullUserName.isEmpty()){
            return null;
        }else {
            return fullUserName;
        }
    }
    private void getUserFullNameFromLoginActivity(){
        Intent intent=new Intent(MainActivity.this, LoginActivity.class);
        startActivityForResult(intent, code);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==code){
            if (resultCode==RESULT_OK){
                String fullUserName=data.getStringExtra(FULLUSERNAME);
                saveFullUserNameInMyPref(fullUserName);
                tvFullUserName.setText("Eto zhe "+fullUserName);
            }else{
                int duration= Toast.LENGTH_LONG;
                if(toast!=null){
                    toast.cancel();
                }
                toast=Toast.makeText(this,"TEBE ZDES NE RADY", duration);
                toast.show();
                getUserFullNameFromLoginActivity();
            }
        }
        if(requestCode==REQUEST_CONTACT){
            if(resultCode==RESULT_OK){
                if(data!=null){
                    Uri contactUri=data.getData();
                    String[] queryFields=new String[]{
                            ContactsContract.Contacts.DISPLAY_NAME
                    };
                    Cursor c=this.getContentResolver().query(contactUri,queryFields, null,null,null);
                    try {
                        if(c.getCount()==0){
                            return;
                        }
                        c.moveToFirst();
                        String name=c.getString(0);
                        Log.d(TAG, "onActivityResult: name of contact"+name);
                    }finally {
                        c.close();
                    }
                }
            }
        }
    }
    private void saveFullUserNameInMyPref(String data){
        sPref=getApplicationContext().getSharedPreferences(MY_PREF, MODE_PRIVATE);
        SharedPreferences.Editor ed=sPref.edit();
        ed.putString(FULLUSERNAME, data);
        ed.commit();
    }
}
