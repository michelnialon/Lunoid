package com.nialon;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.app.Activity;
import android.text.Html;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ConseilsDuMois extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        TextView t;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.conseilsdumois);
        //Toast.makeText(getApplicationContext(), "Veuillez patienter..", Toast.LENGTH_SHORT).show();
        t =(TextView) findViewById(R.id.infosmois);

        //myTextView.setText(Html.fromHtml("<h2>Title</h2><br><p>Description here</p>"));
        Resources myRes = getResources();
        Intent intent = getIntent();
        String message = intent.getStringExtra(Lunoid.EXTRA_MESSAGE);
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        int id = this.getResources().getIdentifier(message, "raw", this.getPackageName());
        //InputStream infosdumois = myRes.openRawResource(R.raw.mars2016);
        InputStream infosdumois = myRes.openRawResource(id);

        InputStreamReader inputreader = new InputStreamReader(infosdumois);
        BufferedReader buffreader = new BufferedReader(inputreader);
        String line;
        String linetot="";
        try
        {
            while (( line = buffreader.readLine()) != null)
            {
                linetot += line;
            }
        } catch (IOException e) {}
        t.setText(Html.fromHtml(linetot));
    }
}
