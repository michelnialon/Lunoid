package com.nialon;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

public class DisplayNotes extends Activity {
    SimpleDateFormat sdfx = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    Calendar seldayx = Calendar.getInstance();

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
            LinearLayout.LayoutParams wparams1 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
            LinearLayout.LayoutParams wparams2 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 2.4f);
            //LinearLayout.LayoutParams wparams3= new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.4f);

            Map<String, ?> allEntries = sp.getAll();
            List<Map.Entry<String, ?>> list = new LinkedList<>(allEntries.entrySet());
            // sort list based on comparator
            Collections.sort(list, (Comparator<Object>) (o1, o2) -> {
                int ret = 0;
                try {
                    Date d1 = sdfx.parse(o1.toString());
                    Date d2 = sdfx.parse(o2.toString());
                    assert d1 != null;
                    ret = d1.compareTo(d2);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return ret;
            });
            // put sorted list into map again
            Map<String, String> sortedMap = new LinkedHashMap<>();
            for (Map.Entry<String, ?> it : list) {
                if (!it.getValue().equals("")) {
                    sortedMap.put(it.getKey(), it.getValue().toString());
                }
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

                ImageView imgCal = new ImageView(this);
                TextView noteText = new TextView(this);
                noteText.setText(entry.getValue().toString());
                noteText.setTag(entry.getValue().toString());
                noteText.setTextColor(Color.rgb(255, 255, 255));
                noteText.setTextSize(14);
                noteText.setPadding(2, 0, 2, 10);
                noteText.setLayoutParams(wparams2);
                noteText.setOnClickListener(
                        v -> {
                            // do something when the corky is clicked
                            Log.d("txt", "clicked");
                            editNote(noteDate.getText().toString(), v, imgCal);
                            // Toast InfosToast = Toast.makeText(getApplicationContext(), v.getTag().toString(), Toast.LENGTH_SHORT);
                            // InfosToast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
                            // InfosToast.show();
                        }
                );
                layoutnote.addView(noteText);

                //imgCal.setImageResource(android.R.drawable.ic_menu_my_calendar);
                str1 = spSched.getString(entry.getKey(), "");
                imgCal.setTag(entry.getKey());
                assert str1 != null;
                if (!str1.equals("0")) {
                    Log.d("str11", str1);
                    imgCal.setImageResource(R.drawable.alarm24x24_on);
                } else {
                    Log.d("str12", str1);
                    imgCal.setImageResource(R.drawable.alarm24x24_off);
                }
                imgCal.setOnClickListener(
                        v -> {
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

    public void editNote(String d, View v, ImageView imgCal) {
        Calendar today = Calendar.getInstance();
        SharedPreferences sp = getSharedPreferences("myNotes", MODE_PRIVATE);
        //Map<String, ?> allEntries = sp.getAll();

        String str1 = sp.getString(d, "");
        SharedPreferences spNotif = getSharedPreferences("myNotif", MODE_PRIVATE);
        final String str1Notif = spNotif.getString(d, "");
        Log.d("editNote", "start");
        try {
            Date date = sdfx.parse(d);
            assert date != null;
            seldayx.setTime(date);
            View root = RelativeLayout.inflate(this, R.layout.editnote, null);
            final EditText e1 = root.findViewById(R.id.editText);
            final Switch sw1 = root.findViewById(R.id.switch1);
            e1.setText(str1);
            assert str1Notif != null;
            sw1.setChecked(!str1Notif.equals("0") && !str1Notif.equals(""));
            Log.d("today", today.toString());
            Log.d("seldayx", seldayx.toString());
            sw1.setEnabled(today.getTime().before(seldayx.getTime()));

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.notedu) + " " + d + " :");
            builder.setView(root);
            builder.setPositiveButton(R.string.sauvegarder, (dialog, which) -> {
                SharedPreferences sp1 = getSharedPreferences("myNotes", MODE_PRIVATE);
                UUID uuid;
                SharedPreferences.Editor sedt = sp1.edit();
                sedt.putString(d, e1.getText().toString());
                sedt.apply();
                ((TextView) v).setText(e1.getText().toString());
                if (sw1.isChecked()) {
                    imgCal.setImageResource(R.drawable.alarm24x24_on);
                } else {
                    imgCal.setImageResource(R.drawable.alarm24x24_off);
                }
                SharedPreferences spNotif1 = getSharedPreferences("myNotif", MODE_PRIVATE);
                SharedPreferences.Editor sedtNotif = spNotif1.edit();
                if (sw1.isChecked() && sw1.isEnabled() && !e1.getText().toString().equals("")) {
                    uuid = scheduleNotification(d, e1.getText().toString());
                    Log.d("uuid", uuid.toString());
                    sedtNotif.putString(d, uuid.toString());
                } else {
                    if (!str1Notif.equals("0") && !str1Notif.equals("")) {
                        Log.d("Cancel", str1Notif);
                        WorkManager.getInstance(getApplicationContext()).cancelWorkById(UUID.fromString(str1Notif));
                    }
                    sedtNotif.putString(d, "0");
                }
                sedtNotif.apply();
                Toast InfosToast;
                if (e1.getText().toString().equals("")) {
                    InfosToast = Toast.makeText(getApplicationContext(), R.string.notesupprimee, Toast.LENGTH_SHORT);
                } else {
                    InfosToast = Toast.makeText(getApplicationContext(), R.string.notesauvegardee, Toast.LENGTH_SHORT);
                }
                InfosToast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
                InfosToast.show();
                // Do something
            });
            builder.setNegativeButton(R.string.annuler, (dialog, which) -> {
                Toast InfosToast = Toast.makeText(getApplicationContext(), R.string.notenonsauvegardee, Toast.LENGTH_SHORT);
                InfosToast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
                InfosToast.show();
                // Do something
            });
            builder.show();
        } catch (Exception e) {
            Log.e("Lunoid", "exception", e);
        }
    }

    public UUID scheduleNotification(String d, String txtNotif) {
        long currentTime;
        Calendar cal1 = Calendar.getInstance();
        Calendar selday = Calendar.getInstance();
        currentTime = cal1.getTimeInMillis();
        try {
            Date date = sdfx.parse(d);
            assert date != null;
            selday.setTime(date);
        } catch (ParseException e) {
            Log.e("Lunoid", e.toString());
        }
        cal1 = selday;

        cal1.set(Calendar.HOUR_OF_DAY, 8);
        cal1.set(Calendar.MINUTE, 0);
        long specificTimeToTrigger = cal1.getTimeInMillis();
        long delayToPass = (specificTimeToTrigger - currentTime) / 1000 / 60;

        Log.d("Schedule Notification", txtNotif + " " + delayToPass);
        //Toast.makeText(getApplicationContext(), "schedule "+ " " + delayToPass.toString(), Toast.LENGTH_LONG).show();
        Data.Builder dataNotif = new Data.Builder();
        dataNotif.putString("textNotif", txtNotif);
        OneTimeWorkRequest notificationWork =
                new OneTimeWorkRequest.Builder(NotificationWorker.class)
                        .setInputData(dataNotif.build())
                        .setInitialDelay(delayToPass, TimeUnit.MINUTES)
                        .build();

        WorkManager.getInstance(this).enqueue(notificationWork);

        return notificationWork.getId();
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
