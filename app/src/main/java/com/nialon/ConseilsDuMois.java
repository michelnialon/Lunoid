package com.nialon;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ConseilsDuMois extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        TextView t;
        InputStream infosdumois;
        String htmltxt;
        StringBuilder linetot = new StringBuilder();
        String message = "";

        try
        {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.conseilsdumois);
            t = findViewById(R.id.infosmois);

            Resources myRes = getResources();
            Intent intent = getIntent();

            String type_message = intent.getStringExtra("type_message");
            Log.d("type_message", type_message);
            if (!type_message.equals("2"))
            {
                message = intent.getStringExtra(Lunoid.EXTRA_MESSAGE);
                //Log.d("message", message);
            }

            if (type_message.equals("0"))
            {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                finish();
            }
            else
            {
                if (type_message.equals("1"))
                {
                    int id = this.getResources().getIdentifier(message, "raw", this.getPackageName());
                    Log.d("id", Integer.toString(id));
                    //InputStream infosdumois = myRes.openRawResource(R.raw.mars2016);
                    infosdumois = myRes.openRawResource(id);
                    InputStreamReader inputreader = new InputStreamReader(infosdumois);
                    BufferedReader buffreader = new BufferedReader(inputreader);
                    Log.d("message", message);
                    String line;

                    while ((line = buffreader.readLine()) != null)
                    {
                        linetot.append(line);
                        //Log.d("line", line);
                    }
                    htmltxt = linetot.toString();
                } else
                {
                    htmltxt = intent.getStringExtra("htmltxt");
                    //Log.d("htmltxt", htmltxt);
                }

                if (type_message.equals("2"))
                {
                    t.setBackgroundColor(Color.BLACK);
                    htmltxt += "<br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/>";
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    t.setText(Html.fromHtml(htmltxt, Html.FROM_HTML_MODE_COMPACT));
                } else {
                    t.setText(Html.fromHtml(htmltxt));
                }
            }
        }   catch (Exception e) {Log.d("erreur", e.toString());}
    }
}
