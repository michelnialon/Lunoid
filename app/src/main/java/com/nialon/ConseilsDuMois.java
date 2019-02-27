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
    private static String htmltxt;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        TextView t;
        InputStream infosdumois;

        try
        {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.conseilsdumois);
            //Toast.makeText(getApplicationContext(), "Veuillez patienter..", Toast.LENGTH_SHORT).show();
            t = findViewById(R.id.infosmois);

            //myTextView.setText(Html.fromHtml("<h2>Title</h2><br><p>Description here</p>"));
            Resources myRes = getResources();
            Intent intent = getIntent();
            String message = intent.getStringExtra(Lunoid.EXTRA_MESSAGE);
            String type_message = intent.getStringExtra("type_message");
            Log.d("message", message);
            Log.d("type_message", type_message);

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
                    StringBuilder linetot = new StringBuilder();

                    while ((line = buffreader.readLine()) != null)
                    {
                        linetot.append(line);
                        Log.d("line", line);
                    }
                    htmltxt = linetot.toString();
                    /*
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        t.setText(Html.fromHtml(htmltxt, Html.FROM_HTML_MODE_COMPACT));
                    } else {
                        t.setText(Html.fromHtml(htmltxt));
                    }
                    */
                } else
                {
                    String hs12 = intent.getStringExtra("hs12");
                    String ht12 = intent.getStringExtra("ht12");
                    String m1 = intent.getStringExtra("m1");
                    String s1 = intent.getStringExtra("s1");
                    String s2 = intent.getStringExtra("s2");
                    String t1 = intent.getStringExtra("t1");
                    String t2 = intent.getStringExtra("t2");
                    Log.d("hs12", hs12);
                    Log.d("ht12", ht12);
                    Log.d("m1", m1);
                    Log.d("s1", s1);
                    Log.d("t1", t1);
                    Log.d("t2", t2);
                    Log.d("s2", s2);
                    htmltxt = "";

                    //addTextFromFile("mars_descendante_fruits_fr.txt");
                    if (!s1.equals("") && s2.equals(""))
                    {
                        if (!t1.equals("") && t2.equals(""))
                        {
                            addTextFromFile(m1 + "_" + s1 + "_" + t1 + "_fr.txt");
                        }
                        if (!t1.equals("") && !t2.equals(""))
                        {
                            addTextFromFile(m1 + "_" + s1 + "_" + t1 + "_fr.txt");
                            addText("<font color=#fff>" + ht12 + "</font>");
                            addTextFromFile(m1 + "_" + s1 + "_" + t2 + "_fr.txt");
                        }
                    }
                    if (!s1.equals("") && !s2.equals(""))
                    {
                        if (!t1.equals("") && t2.equals(""))
                        {
                            addTextFromFile(m1 + "_" + s1 + "_" + t1 + "_fr.txt");
                            addText( hs12 );
                            addTextFromFile(m1 + "_" + s2 + "_" + t1 + "_fr.txt");
                        }
                        if (!t1.equals("") && !t2.equals(""))
                        {
                            if (hs12.compareTo(ht12) < 0)
                            {
                                addTextFromFile(m1 + "_" + s1 + "_" + t1 + "_fr.txt");
                                addText(hs12 );
                                addTextFromFile(m1 + "_" + s2 + "_" + t1 + "_fr.txt");
                                System.out.println(m1 + "_" + s2 + "_" + t1 + "_fr.txt");
                                addText(ht12 );
                                addTextFromFile(m1 + "_" + s2 + "_" + t2 + "_fr.txt");
                            }
                            if (hs12.compareTo(ht12) > 0)
                            {
                                addTextFromFile(m1 + "_" + s1 + "_" + t1 + "_fr.txt");
                                addText( ht12);
                                addTextFromFile(m1 + "_" + s1 + "_" + t2 + "_fr.txt");
                                addText(hs12 );
                                addTextFromFile(m1 + "_" + s2 + "_" + t2 + "_fr.txt");
                            }
                            if (hs12.compareTo(ht12) == 0)
                            {
                                addTextFromFile(m1 + "_" + s1 + "_" + t1 + "_fr.txt");
                                addText( hs12);
                                addTextFromFile(m1 + "_" + s2 + "_" + t2 + "_fr.txt");
                            }
                        }
                    }

                }
/*
                InputStreamReader inputreader = new InputStreamReader(infosdumois);
                BufferedReader buffreader = new BufferedReader(inputreader);
                Log.d("message", message);
                String line;
                String linetot = "";
                while ((line = buffreader.readLine()) != null) {
                    linetot += line;
                    Log.d("line", line);
                }
                */

                if (type_message.equals("2"))
                {
                    t.setBackgroundColor(Color.BLACK);
                    htmltxt += "<br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/>";
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    t.setText(Html.fromHtml(htmltxt, Html.FROM_HTML_MODE_COMPACT));
                } else {
                    t.setText(Html.fromHtml(htmltxt));
                }
            }
        }   catch (Exception e) {Log.d("erreur", e.toString());}
    }
    private void addTextFromFile(String s)
    {
        //InputStream infosdumois;
        //AssetManager assetMgr = this.getAssets();

        try
        {
            //infosdumois = assetMgr.open(s);

            //InputStreamReader inputreader = new InputStreamReader(infosdumois);
            BufferedReader br = new BufferedReader(new InputStreamReader(getAssets().open(s)));

            String st;
            StringBuilder linetot = new StringBuilder();
            while ((st = br.readLine()) != null)
            {
                System.out.println(st);
                linetot.append("\n");
                linetot.append(st);
            }
            htmltxt = linetot.toString();
        }
        catch (Exception e)
        {
            System.out.println("Something went wrong.");
        }
    }

    private static void addText(String s)
    {
        System.out.println(s);
        htmltxt = htmltxt + "<br/>" + "A partir de "  + s + " :" + "<br/>";
    }
}
