package com.software.predictopia;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by LENOVO on 07-10-2016.
 */

public class GetResult extends AsyncTask<String, Void, String> {
    public ValidationResponse delegate;
    Context ctx;

    GetResult(Context ctx) {
        this.ctx = ctx;
    }


    @Override
    protected String doInBackground(String... params) {
        Socket socket = null;
        DataOutputStream dataOutputStream = null;
        String resp = "";

        try {
            socket = new Socket(URLS.Script_URL, 8881);
            Log.d("isConnected", socket.isConnected() + "");
            OutputStream sout = socket.getOutputStream();
            byte[] bytes = params[2].getBytes("UTF-8");
            sout.write(bytes);
            sout.flush();

            InputStream is =
                    socket.getInputStream();
            BufferedReader buf =
                    new BufferedReader(new InputStreamReader(is));
            resp = buf.readLine();

        } catch (UnknownHostException e) {
            resp = e.getMessage();
        } catch (IOException e) {
            resp = "404 Not Found";
        }
        return resp;
    }

    @Override
    protected void onPostExecute(String s) {
        MainActivity obj = (MainActivity) ctx;
        obj.showProgress(false);
        Log.d("dopost", "here" + s);
        boolean isJSON = true;
        if (!s.equals("None")) {
            try {
                new JSONObject(s);
            } catch (JSONException ex) {
                try {
                    new JSONArray(s);
                } catch (JSONException ex1) {
                    isJSON = false;
                }
            }
        }
        if (s.equals("Connection Error. Please Try Again! ") || s.equals("Socket TimedOut! ") || s.equals("Malformed URL! ") || s.equals("404 Not Found")) {
            delegate.response(false, s);
        } else if (!isJSON)
            delegate.response(false, "Database server not reachable !");
        else {
            Log.e("Result", s);
            if (!s.equals("None")) {
                delegate.response(true, s);
            } else {
                delegate.response(false, s);
            }
        }
    }
}
