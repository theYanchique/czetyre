package com.example.czetyre;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;


import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;

import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.czetyre.utils.PhotosUtils;

import java.io.File;
import java.util.Date;

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
    private final static int REQUEST_PHOTO=2;
    private static final String TAG= "MainActivity";
    private ImageButton btnSelfPhoto;
    private ImageView ivSelfPhoto;
    private Intent intentGetPhoto;
    private File selfPhotoFile;
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
        btnSelfPhoto=findViewById(R.id.btnSelfPhoto);
        ivSelfPhoto=findViewById(R.id.ivSelfPhoto);
        intentGetPhoto=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String photoFileName=getPhotoFileName();
        selfPhotoFile=getPhotoFile(photoFileName);
        boolean canTakePhoto=false;
        canTakePhoto=selfPhotoFile!=null&&intentGetPhoto.resolveActivity(packageManager)!=null;
        btnSelfPhoto.setEnabled(canTakePhoto);
        if(canTakePhoto){
           Uri uri= FileProvider.getUriForFile(this, "com.example.czetyre.fileprovider", selfPhotoFile);
           intentGetPhoto.putExtra(MediaStore.EXTRA_OUTPUT,uri);
        }
        btnSelfPhoto.setOnClickListener(this);
        }
    }
    private String getPhotoFileName(){
        Date date=new Date();
        return "IMG+"+"18042023"+".jpg";
    }
    private File getPhotoFile(String fileName){
        File filesDir=this.getFilesDir();
        if(filesDir==null){
            return null;
        }
        //TODO: insert check that file exists, in this case dont create new file
        return new File(filesDir, fileName);
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
        if(view.getId()==btnSelfPhoto.getId()){
            startActivityForResult(intentGetPhoto, REQUEST_PHOTO);
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
    private void updateSelfPhotoImageView(File photoFile){
        if(photoFile==null || !photoFile.exists()){
            ivSelfPhoto.setImageDrawable(null);
            Log.d(TAG, "updateSelfPhotoImageView: ERROR with file");
        }else{
            Point size=new Point();
            this.getWindowManager().getDefaultDisplay().getSize(size);
            Bitmap bitmap= PhotosUtils.getScaledBitmap(photoFile.getPath(),size.x,size.y);
            ivSelfPhoto.setImageBitmap(bitmap);
        }
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
        if(requestCode==REQUEST_PHOTO){
            if(resultCode==RESULT_OK){
                updateSelfPhotoImageView(selfPhotoFile);
            }else {
                Log.d(TAG, "onActivityResult: ERROR with photo");
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
