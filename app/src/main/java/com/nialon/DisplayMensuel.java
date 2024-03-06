package com.nialon;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class DisplayMensuel extends Activity {
    SimpleDateFormat sdfx = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    SimpleDateFormat sdf2 = new SimpleDateFormat("EEEE dd MMMM yyyy", Locale.getDefault());
    SimpleDateFormat sdfMonthYear = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.vuemensuelle);
            LinearLayout allJours = findViewById(R.id.layout_mensuel);

            LinearLayout.LayoutParams wparams1 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
            LinearLayout.LayoutParams wparams2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            wparams2.setMargins(0, 0, 0, 4);
            wparams1.setMargins(0, 4, 0, 4);
            wparams1.gravity = Gravity.CENTER;

            Intent intent = getIntent();
            int year = intent.getIntExtra("year", 2024);
            int month = intent.getIntExtra("month", 1);
            Calendar cal = new GregorianCalendar(year, month, 1);

            Date d1 = new Date();
            String ds, ds2;
            TextView txtMonth = findViewById(R.id.txtmonth);

            cal.set(Calendar.MONTH, month);
            cal.set(Calendar.YEAR, year);
            int nbdays = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
            txtMonth.setText(sdfMonthYear.format(cal.getTime()));

            for (int d = 1; d <= nbdays; d++) {
                LinearLayout layoutJour = new LinearLayout(this);
                layoutJour.setOrientation(LinearLayout.HORIZONTAL);
                layoutJour.setLayoutParams(wparams2);
                cal.set(Calendar.DAY_OF_MONTH, d);
                d1.setTime(cal.getTimeInMillis());
                ds = sdfx.format(d1);
                ds2 = sdf2.format(d1);
                Log.d("txt", ds);
                Log.d("txt", ds2);

                TextView noteDate = new TextView(this);
                noteDate.setText(ds2.substring(0, 1).toUpperCase() + ds2.substring(1, 3).toLowerCase());
                noteDate.setTextSize(20);
                noteDate.setPadding(2, 0, 2, 0);
                noteDate.setLayoutParams(wparams1);

                // ___________
                TextView noteText = new TextView(this);
                noteText.setText(Integer.toString(d));
                noteText.setTextColor(Color.parseColor("#949494"));
                noteText.setTextSize(20);
                noteText.setPadding(2, 0, 2, 0);
                noteText.setLayoutParams(wparams1);
                layoutJour.setTag(d);
                layoutJour.setOnClickListener(
                        v -> {
                            // do something when the corky is clicked
                            Log.d("txt", "clicked");

//                            Toast InfosToast = Toast.makeText(getApplicationContext(), v.getTag().toString(), Toast.LENGTH_SHORT);
//                            InfosToast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
//                            InfosToast.show();

                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("d", (int) (v.getTag()));
                            setResult(Activity.RESULT_OK, resultIntent);
                            finish();

                        }
                );
                if (Lunoid.mapApogee.get(ds).equalsIgnoreCase("0") &&
                        Lunoid.mapPerigee.get(ds).equalsIgnoreCase("0") &&
                        Lunoid.mapNoeud.get(ds).equalsIgnoreCase("0")) {
                    noteDate.setTextColor(Color.parseColor("#949494"));
                    noteText.setTextColor(Color.parseColor("#949494"));
                } else {
                    noteDate.setTextColor(Color.parseColor("#ff0000"));
                    noteText.setTextColor(Color.parseColor("#ff0000"));
                }
                layoutJour.addView(noteDate);
                layoutJour.addView(noteText);
                ImageView imgMD = new ImageView(this);
                if (Lunoid.mapMontant.get(ds).equalsIgnoreCase("0")) {
                    imgMD.setImageResource(R.drawable.lune_d);
                } else if (Lunoid.mapMontant.get(ds).equalsIgnoreCase("1")) {
                    imgMD.setImageResource(R.drawable.lune_m);
                } else {
                    imgMD.setImageResource(R.drawable.lune_md);
                }
                imgMD.setLayoutParams(wparams1);
                layoutJour.addView(imgMD);
                ImageView imgFe = new ImageView(this);
                ImageView imgFr = new ImageView(this);
                ImageView imgRa = new ImageView(this);
                ImageView imgFl = new ImageView(this);


                if (Lunoid.mapJour.get(ds).toLowerCase().contains("feuilles")) {
                    imgFe.setImageResource(R.drawable.salad30_on);
                } else {
                    imgFe.setImageResource(R.drawable.salad30_off);
                    imgFe.setAlpha(0.3F);
                }
                if (Lunoid.mapJour.get(ds).toLowerCase().contains("fruits")) {
                    imgFr.setImageResource(R.drawable.apple30_on);
                } else {
                    imgFr.setImageResource(R.drawable.apple30_off);
                    imgFr.setAlpha(0.3F);
                }
                if (Lunoid.mapJour.get(ds).toLowerCase().contains("racines")) {
                    imgRa.setImageResource(R.drawable.carrot30_on);
                } else {
                    imgRa.setImageResource(R.drawable.carrot30_off);
                    imgRa.setAlpha(0.3F);
                }
                if (Lunoid.mapJour.get(ds).toLowerCase().contains("fleurs")) {
                    imgFl.setImageResource(R.drawable.flower30_on);
                } else {
                    imgFl.setImageResource(R.drawable.flower30_off);
                    imgFl.setAlpha(0.3F);
                }
                imgFe.setLayoutParams(wparams1);
                imgFl.setLayoutParams(wparams1);
                imgFr.setLayoutParams(wparams1);
                imgRa.setLayoutParams(wparams1);
                layoutJour.addView(imgFe);
                layoutJour.addView(imgFr);
                layoutJour.addView(imgRa);
                layoutJour.addView(imgFl);
                if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                    layoutJour.setBackgroundColor(Color.parseColor("#e6fbff"));
                } else if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                    layoutJour.setBackgroundColor(Color.parseColor("#cbf7ff"));
                } else {
                    if (d % 2 == 0) {
                        layoutJour.setBackgroundColor(Color.WHITE);
                    } else {
                        layoutJour.setBackgroundColor(Color.WHITE);
                    }
                }
                ImageView imgMoon = new ImageView(this);
                imgMoon.setImageResource(R.drawable.moon_empty);
                if (Lunoid.mapEclair.get(ds).equalsIgnoreCase("100")) {
                    imgMoon.setImageResource(R.drawable.moon_full);
                }
                if (Lunoid.mapEclair.get(ds).equalsIgnoreCase("0")) {
                    imgMoon.setImageResource(R.drawable.moon_new);
                }
                layoutJour.addView(imgMoon);

                allJours.addView(layoutJour);
            }

        } catch (Exception e) {
            Log.d("erreur", e.toString());
        }
    }
}