package com.example.czetyre;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.net.URLEncoder;

public class GetData extends AsyncTask<String, Void, String>{

    public AsyncResponse delegate;
    public GetData (AsyncResponse delegate){
        this.delegate=delegate;
    }
    public interface AsyncResponse{
        void processFinish (String output);
    }

    @Override
    protected String doInBackground(String... strings) {
        try{
            String login=strings[0];
            String pass=strings[1];
            String url_link=strings[2];

            String data= URLEncoder.encode("login", "UTF-8")+"="+URLEncoder.encode(login, "UTF-8");
            data+="&"+URLEncoder.encode("password", "UTF-8")+"="+URLEncoder.encode(pass, "UTF-8");

            URL url=new URL (url_link);
            URLConnection conn=url.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter wr=new OutputStreamWriter(conn.getOutputStream());

            wr.write(data);
            wr.flush();
            BufferedReader reader= new BufferedReader(new InputStreamReader(conn.getInputStream()));

            StringBuilder sb= new StringBuilder();
            String line= null;
            while ((line=reader.readLine())!=null){
                sb.append(line);
                break;
            }
            //Log.d(TAG, "doInBackground: ");
            return sb.toString();
        }catch (Exception e){
            //e.printStackTrace();
            return new String("Exception:"+e.getMessage());
        }
        //return null;
    }

    @Override
    protected void onPostExecute(String result) {
        //super.onPostExecute(s);
        if(result!=null && !result.equals("")){
            delegate.processFinish(result);
        }
    }
}
