package com.nialon;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class WidgetProvider4Cell extends AppWidgetProvider {
    static Map<String, String> mapLever = new HashMap<>();
    static Map<String, String> mapCoucher = new HashMap<>();
    static Map<String, String> mapEclair = new HashMap<>();
    static Map<String, String> mapPhase = new HashMap<>();
    static Map<String, String> mapCroissant = new HashMap<>();
    static Map<String, String> mapApogee = new HashMap<>();
    static Map<String, String> mapPerigee = new HashMap<>();
    static Map<String, String> mapNoeud = new HashMap<>();
    static Map<String, String> mapMontant = new HashMap<>();
    static Map<String, String> mapJour = new HashMap<>();
    static Map<String, String> mapSigne = new HashMap<>();
    static SimpleDateFormat sdf;
    static String dateString;
    static Boolean lh;
    static Boolean hemispheresud;
    private Calendar d2021;

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        Log.d("widget1cell", "onEnabled");
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
    {
        Log.d("widget1cell", "onupdate");
        try {
            sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            dateString = sdf.format(Calendar.getInstance().getTime());
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            lh = prefs.getBoolean("timezone", false);
            lh = true;
            hemispheresud = prefs.getBoolean("hemisphere", false);
            //dateString="06/03/2020";
            Log.d("datestring", dateString);
            d2021 = Calendar.getInstance();
            d2021.set(Calendar.DAY_OF_MONTH, 1);
            d2021.set(Calendar.MONTH, 0);
            d2021.set(Calendar.YEAR, 2021);
            d2021.set(Calendar.HOUR, 0);
            d2021.set(Calendar.MINUTE, 0);
            d2021.set(Calendar.SECOND, 0);

            ReadData(context);
            ComponentName thisWidget = new ComponentName(context, WidgetProvider4Cell.class);
            int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
            for (int widgetId : allWidgetIds) {
                Log.d("widget ", Integer.toString(widgetId));
                RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget4cell);
                String ecl = mapEclair.get(dateString);
                String ecl2 = "nlune";
                String ecl3;
                if (mapCroissant.get(dateString).equals("0")) {
                    ecl3 = ecl2.concat(ecl);
                } else {
                    ecl3 = ecl2.concat("_").concat(ecl);
                }
                Log.d("ecl3", ecl3);
                int resId = context.getResources().getIdentifier(ecl3, "drawable", context.getPackageName());
                Log.d("resid", Integer.toString(resId));
                // image lune
                remoteViews.setImageViewResource(R.id.imageLuneWidget, resId);
                // pourcentage
                remoteViews.setTextViewText(R.id.textPct, ecl.concat(" %"));
                // heures lever coucher
                remoteViews.setTextViewText(R.id.lever, heurelocale(mapLever.get(dateString), lh));
                remoteViews.setTextViewText(R.id.coucher, heurelocale(mapCoucher.get(dateString), lh));

                // feuille/fruit/racine/fleur
                if (mapJour.get(dateString).contains("Feuilles")) {
                    remoteViews.setImageViewResource(R.id.imageFeuille, R.drawable.salad30_on);
                } else {
                    remoteViews.setImageViewResource(R.id.imageFeuille, R.drawable.salad30_off);
                }

                if (mapJour.get(dateString).contains("Fruits"))
                {
                    remoteViews.setImageViewResource(R.id.imageFruit, R.drawable.apple30_on);
                }
                else {
                    remoteViews.setImageViewResource(R.id.imageFruit, R.drawable.apple30_off);
                }

                if (mapJour.get(dateString).contains("Racines"))
                {
                    remoteViews.setImageViewResource(R.id.imageRacine, R.drawable.carrot30_on);
                }
                else
                {
                    remoteViews.setImageViewResource(R.id.imageRacine, R.drawable.carrot30_off);
                }
                if (mapJour.get(dateString).contains("Fleurs"))
                {
                    remoteViews.setImageViewResource(R.id.imageFleur, R.drawable.flower30_on);
                }
                else
                {
                    remoteViews.setImageViewResource(R.id.imageFleur, R.drawable.flower30_off);
                }
                // montant / descendant
                Log.d("montant ", mapMontant.get(dateString));

                if (mapMontant.get(dateString).startsWith("0"))
                {
                    remoteViews.setImageViewResource(R.id.imageSens1, R.drawable.moon_desc_on);
                    remoteViews.setImageViewResource(R.id.imageSens2, R.drawable.moon_asc_off);
                }

                if (mapMontant.get(dateString).startsWith("1"))
                {
                    remoteViews.setImageViewResource(R.id.imageSens1, R.drawable.moon_desc_off);
                    remoteViews.setImageViewResource(R.id.imageSens2, R.drawable.moon_asc_on);
                }
                if ( mapMontant.get(dateString).startsWith("2"))
                {
                    remoteViews.setImageViewResource(R.id.imageSens1, R.drawable.moon_asc_on);
                    remoteViews.setImageViewResource(R.id.imageSens2, R.drawable.moon_desc_on);
                }
                if ( mapMontant.get(dateString).startsWith("3"))
                {
                    remoteViews.setImageViewResource(R.id.imageSens1, R.drawable.moon_desc_on);
                    remoteViews.setImageViewResource(R.id.imageSens2, R.drawable.moon_asc_on);
                }
                // apogee perigee noeud
                Log.d("Apogée",mapApogee.get(dateString));
                Log.d("Périgée",mapPerigee.get(dateString));
                Log.d("Noeud",mapNoeud.get(dateString));

                if (!mapApogee.get(dateString).equals("0"))
                {
                    remoteViews.setTextViewText(R.id.evtdesc, context.getResources().getString(R.string.apogee));
                    remoteViews.setTextViewText(R.id.evthour, heurelocale(mapApogee.get(dateString), lh));
                    remoteViews.setTextColor(R.id.evtdesc, Color.RED);
                    remoteViews.setTextColor(R.id.evthour, Color.RED);
                }
                else if (!mapPerigee.get(dateString).equals("0"))
                {
                    remoteViews.setTextViewText(R.id.evtdesc, context.getResources().getString(R.string.perigee));
                    remoteViews.setTextViewText(R.id.evthour, heurelocale(mapPerigee.get(dateString), lh));
                    remoteViews.setTextColor(R.id.evtdesc, Color.RED);
                    remoteViews.setTextColor(R.id.evthour, Color.RED);
                }
                else if (!mapNoeud.get(dateString).equals("0")) {
                    Log.d("Noeud", mapNoeud.get(dateString));
                    remoteViews.setTextViewText(R.id.evtdesc, context.getResources().getString(R.string.noeudlunaire));
                    String temp = heurelocale(mapNoeud.get(dateString).substring(0, 5), lh);
                    if (mapNoeud.get(dateString).startsWith("+", 5)) {
                        temp += "/";
                    } else if (mapNoeud.get(dateString).startsWith("-", 5)) {
                        temp += "\\";
                    }
                    remoteViews.setTextViewText(R.id.evthour, temp);
                    remoteViews.setTextColor(R.id.evtdesc, Color.RED);
                    remoteViews.setTextColor(R.id.evthour, Color.RED);
                } else {
                    remoteViews.setTextViewText(R.id.evtdesc, "");
                    remoteViews.setTextViewText(R.id.evthour, "");
                    remoteViews.setTextColor(R.id.evtdesc, Color.LTGRAY);
                    remoteViews.setTextColor(R.id.evthour, Color.LTGRAY);
                }
                // launch activity
                Intent launchActivity = new Intent(context, Lunoid.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, launchActivity, 0);
                remoteViews.setOnClickPendingIntent(R.id.LunoidWidget, pendingIntent);

                // update the widget
                appWidgetManager.updateAppWidget(widgetId, remoteViews);
            }
        } catch (Exception e) {Log.d("Exception1 :" , e.toString());}
    }
    private void ReadData(Context ctxt) {
        Date d;

        Log.d("ReadData", "begin");
        Resources myRes = ctxt.getResources();
        InputStream lundata = myRes.openRawResource(R.raw.datalune);

        InputStreamReader inputreader = new InputStreamReader(lundata);
        BufferedReader buffreader = new BufferedReader(inputreader);
        String line;
        String[] separated;
        try {
            while ((line = buffreader.readLine()) != null) {
                //Log.d("Line",line);
                separated = line.split(";");
                StringBuilder separated9 = new StringBuilder(separated[9]);
                if (hemispheresud) {
                    if (separated9.charAt(0) == '0') {
                        separated9.setCharAt(0, '1');
                    } else if (separated9.charAt(0) == '1') {
                        separated9.setCharAt(0, '0');
                    } else if (separated9.charAt(0) == '2') {
                        separated9.setCharAt(0, '3');
                    } else if (separated9.charAt(0) == '3') {
                        separated9.setCharAt(0, '2');
                    }
                }
                d = sdf.parse(separated[0]);
                if (d.compareTo(d2021.getTime()) >= 0) {
                    String temp = separated[11];
                    temp = temp.replace("Bel", "Fruits");
                    temp = temp.replace("Lio", "Fruits");
                    temp = temp.replace("Sag", "Fruits");
                    temp = temp.replace("Tau", "Racines");
                    temp = temp.replace("Vie", "Racines");
                    temp = temp.replace("Cap", "Racines");
                    temp = temp.replace("Gem", "Fleurs");
                    temp = temp.replace("Bal", "Fleurs");
                    temp = temp.replace("Ver", "Fleurs");
                    temp = temp.replace("Poi", "Feuilles");
                    temp = temp.replace("Can", "Feuilles");
                    temp = temp.replace("Sco", "Feuilles");
                    mapJour.put(separated[0], temp);
                    Log.d("jour", temp);
                } else {
                    mapJour.put(separated[0], separated[4]);
                }

                mapLever.put(separated[0], separated[1]);
                mapCoucher.put(separated[0], separated[2]);
                mapPhase.put(separated[0], separated[3]);
                // mapJour.put(separated[0], separated[4]);
                mapApogee.put(separated[0], separated[5]);
                mapPerigee.put(separated[0], separated[6]);
                mapNoeud.put(separated[0], separated[7]);
                mapMontant.put(separated[0], separated9.toString());
                mapCroissant.put(separated[0], separated[10]);
                mapSigne.put(separated[0], separated[11]);
                mapEclair.put(separated[0], separated[13]);
            }
        } catch (Exception e) {
            Log.e("Lunoid", "exception", e);
        }
        Log.d("ReadData", "end");
    }

    private static String heurelocale(String s, boolean lh) {
        if (s.equals("--:--")) {
            return s;
        } else {
            if (lh) {
                int nboffsetCal;
                Calendar cal1 = Calendar.getInstance();
                cal1.set(Calendar.HOUR_OF_DAY, Integer.parseInt(s.substring(0, 2)));
                cal1.set(Calendar.MINUTE, Integer.parseInt(s.substring(3, 5)));
                nboffsetCal = TimeZone.getDefault().getOffset(cal1.getTime().getTime()) / 1000 / 3600;

                String s1;
                String s2;

                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(s.substring(0, 2)));
                cal.set(Calendar.MINUTE, Integer.parseInt(s.substring(3, 5)));

                cal.add(Calendar.HOUR_OF_DAY, nboffsetCal);
                s1 = String.valueOf(cal.get(Calendar.HOUR_OF_DAY));
                s1 = (s1.length()==1 ? "0" + s1 : s1);
                s2 = String.valueOf(cal.get(Calendar.MINUTE));
                s2 = (s2.length()==1 ? "0" + s2 : s2);
                return s1 + ":" + s2;
            }
            else
                return s;
        }
    }
}