package com.nialon;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DisplayNotes extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.listnotes);
            LinearLayout allNotes = findViewById(R.id.layout_notes);
            LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
            lparams.setMargins(20, 0, 20, 0);
            //lparams.gravity = Gravity.CENTER_HORIZONTAL;

            SharedPreferences sp = getSharedPreferences("myNotes", MODE_PRIVATE);
            SharedPreferences spSched = getSharedPreferences("myNotif", MODE_PRIVATE);
            final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            LinearLayout.LayoutParams wparams1 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
            LinearLayout.LayoutParams wparams2 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 2.4f);
            //LinearLayout.LayoutParams wparams3= new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.4f);

            Map<String, ?> allEntries = sp.getAll();
            List<Map.Entry<String, ?>> list = new LinkedList<Map.Entry<String, ?>>(allEntries.entrySet());
            // sort list based on comparator
            Collections.sort(list, new Comparator<Object>() {
                public int compare(Object o1, Object o2) {
                    int ret = 0;
                    try {
                        Date d1 = sdf.parse(o1.toString());
                        Date d2 = sdf.parse(o2.toString());
                        ret = d1.compareTo(d2);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    return ret;
                }
            });
            // put sorted list into map again
            Map<String, String> sortedMap = new LinkedHashMap<>();
            for (Map.Entry<String, ?> it : list) {
                sortedMap.put(it.getKey(), it.getValue().toString());
            }
            int i = 0;
            String str1;
            for (LinkedHashMap.Entry<String, ?> entry : sortedMap.entrySet()) {
                LinearLayout layoutnote = new LinearLayout(this);
                layoutnote.setOrientation(LinearLayout.HORIZONTAL);

                TextView noteDate = new TextView(this);
                noteDate.setText(entry.getKey());
                noteDate.setTextColor(Color.YELLOW);
                noteDate.setTextSize(16);
                noteDate.setPadding(2, 0, 2, 0);
                noteDate.setLayoutParams(wparams1);
                layoutnote.addView(noteDate);

                TextView noteText = new TextView(this);
                noteText.setText(entry.getValue().toString());
                noteText.setTag(entry.getValue().toString());
                noteText.setTextColor(Color.rgb(255, 255, 255));
                noteText.setTextSize(14);
                noteText.setPadding(2, 0, 2, 0);
                noteText.setLayoutParams(wparams2);
                noteText.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // do something when the corky is clicked
                                Log.d("txt", "clicked");
                                Toast InfosToast = Toast.makeText(getApplicationContext(), v.getTag().toString(), Toast.LENGTH_SHORT);
                                InfosToast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
                                InfosToast.show();
                            }
                        }
                );
                layoutnote.addView(noteText);

                ImageView imgCal = new ImageView(this);
                //imgCal.setImageResource(android.R.drawable.ic_menu_my_calendar);
                str1 = spSched.getString(entry.getKey(), "");
                imgCal.setTag(entry.getKey());
                if (!str1.equals("0")) {
                    Log.d("str11", str1);
                    imgCal.setImageResource(R.drawable.alarm24x24_on);
                } else {
                    Log.d("str12", str1);
                    imgCal.setImageResource(R.drawable.alarm24x24_off);
                }
                imgCal.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // do something when the corky is clicked
/*
                                if ((int)((ImageView)v).getTag() == R.drawable.alarm24x24_on)
                                {
                                    Log.d("img", "clickedon");
                                    //((ImageView)v).setImageResource(R.drawable.alarm24x24_off);
                                   // v.setTag(R.drawable.alarm24x24_off);
                                }
                                else
                                {
                                    Log.d("img", "clickedoff");
                                    //((ImageView)v).setImageResource(R.drawable.alarm24x24_on);
                                    //v.setTag(R.drawable.alarm24x24_on);

                                }

 */
                            }
                        }
                );
                layoutnote.addView(imgCal);
                if (i % 2 == 0) {
                    layoutnote.setBackgroundColor(Color.rgb(55, 55, 55));
                }
                Log.d("map values", entry.getKey() + ": " + entry.getValue().toString());

                allNotes.addView(layoutnote);
                i++;
            }

        } catch (Exception e) {
            Log.d("erreur", e.toString());
        }
    }
    /*
    private boolean isNotifSet(String d1)
    {
        SharedPreferences spSched = getSharedPreferences("myNotif", MODE_PRIVATE);
        return (!(spSched.getString(d1, "")).equals ("0"));
    }
    private void NotifSet(String d1)
    {
        SharedPreferences spSched = getSharedPreferences("myNotif", MODE_PRIVATE);
        SharedPreferences.Editor sedtNotif = spSched.edit();

        if (sw1.isChecked() && sw1.isEnabled() && !e1.getText().toString().equals("")) {
            uuid = scheduleNotification(e1.getText().toString());
            Log.d("uuid", uuid.toString());
            sedtNotif.putString(dateString, uuid.toString());
        } else {
            if (!str1Notif.equals("0") && !str1Notif.equals("")) {
                Log.d("Cancel", str1Notif);
                WorkManager.getInstance(getApplicationContext()).cancelWorkById(UUID.fromString(str1Notif));
            }
            sedtNotif.putString(dateString, "0");
        }
        sedtNotif.apply();
    }

     */
}
