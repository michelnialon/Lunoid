package com.nialon;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class WidgetProvider2Cell extends AppWidgetProvider {
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
    static SimpleDateFormat sdf;
    static String dateString;
    static Date date1;
    static Boolean lh;

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        Log.d("widget1cell", "onEnabled");
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
    {
        Log.d("widget1cell", "onupdate");
        try
        {
            sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            date1 = Calendar.getInstance().getTime();
            dateString = sdf.format(date1);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            lh = prefs.getBoolean("timezone", false);
            Log.d("datestring", dateString);

            ReadData(context);
            ComponentName thisWidget = new ComponentName(context, WidgetProvider2Cell.class);
            int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
            for (int widgetId : allWidgetIds)
            {
                Log.d("widget ", Integer.toString(widgetId));
                RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget2cell);
                String ecl = mapEclair.get(dateString);
                if (ecl.equals("-"))
                {
                    String ecl2 = "lune0";
                    String ecl3;
                    int ph = Integer.parseInt(mapPhase.get(dateString));
                    ecl3 = ecl2.concat(String.format(Locale.getDefault(), "%02d", ph));
                    Log.d("ecl3", ecl3);
                    int resId = context.getResources().getIdentifier(ecl3, "drawable", context.getPackageName());
                    Log.d("resid", Integer.toString(resId));
                    remoteViews.setImageViewResource(R.id.imageLuneWidget, resId);
                }
                else
                {
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
                    remoteViews.setTextViewText(R.id.lever, heurelocale(mapLever.get(dateString), date1, lh));
                    remoteViews.setTextViewText(R.id.coucher, heurelocale(mapCoucher.get(dateString), date1, lh));
                    appWidgetManager.updateAppWidget(widgetId, remoteViews);
                }
            }
        } catch (Exception e) {Log.d("Exception1 :" , e.toString());}
    }
    private void ReadData(Context ctxt)
    {
        Log.d("ReadData", "begin");
        Resources myRes = ctxt.getResources();
        InputStream lundata = myRes.openRawResource(R.raw.datalune);

        InputStreamReader inputreader = new InputStreamReader(lundata);
        BufferedReader buffreader = new BufferedReader(inputreader);
        String line;
        String[] separated;
        try
        {
            while (( line = buffreader.readLine()) != null)
            {
                //Log.d("Line",line);
                separated = line.split(";");
                mapLever.put(separated[0],separated[1]);
                mapCoucher.put(separated[0],separated[2]);
                mapPhase.put(separated[0],separated[3]);
                mapJour.put(separated[0],separated[4]);
                mapEclair.put(separated[0],separated[13]);
                mapCroissant.put(separated[0],separated[10]);
                mapApogee.put(separated[0],separated[5]);
                mapPerigee.put(separated[0],separated[6]);
                mapNoeud.put(separated[0],separated[7]);
                mapMontant.put(separated[0],separated[9]);
            }
        } catch (IOException e)
        {
            Log.e("Lunoid", "exception", e);
        }
        Log.d("ReadData", "end");
    }
    private static String heurelocale(String s, Date d, boolean lh)
    {
        if (s.equals("--:--"))
        {
            return s;
        }
        else
        {
            if (lh)
            {
                int nboffsetCal;
                Calendar cal1 = Calendar.getInstance();
                cal1.setTime(d);
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