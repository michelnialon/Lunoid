package com.nialon;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
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

public class WidgetProvider1Cell extends AppWidgetProvider {
    public static final String WIDGET_IDS_KEY = "mywidgetproviderwidgetids";
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
    public void onDisabled(Context context) {
        super.onDisabled(context);
        Log.d("widget1cell", "onDisabled");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // Chain up to the super class so the onEnabled, etc callbacks get dispatched
        super.onReceive(context, intent);
        // Handle a different Intent
        Log.d("widget1cell", "onReceive()" + intent.getAction());

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d("widget1cell", "onupdate");
        try {
            sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            date1 = Calendar.getInstance().getTime();
            dateString = sdf.format(date1);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            lh = prefs.getBoolean("timezone", false);
            Log.d("datestring", dateString);

            ReadData(context);
            ComponentName thisWidget = new ComponentName(context, WidgetProvider1Cell.class);
            int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
            for (int widgetId : allWidgetIds) {
                Log.d("widget ", Integer.toString(widgetId));
                RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget1cell);
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
                remoteViews.setImageViewResource(R.id.imageLuneWidget, resId);
                remoteViews.setTextViewText(R.id.textPct, ecl.concat(" %"));

                // launch main application on click
/*
                Intent launchActivity = new Intent(context, Lunoid.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, launchActivity, PendingIntent.FLAG_UPDATE_CURRENT);
                remoteViews.setOnClickPendingIntent(R.id.LunoidWidget, pendingIntent);
*/
/*
                // update widget on click
                Intent updateIntent = new Intent();
                updateIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                //updateIntent.putExtra(WidgetProvider1Cell.WIDGET_IDS_KEY, allWidgetIds);
                updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, allWidgetIds);
                // onUpdate is only called when AppWidgetManager.EXTRA_APPWIDGET_IDS is set to a non empty array.
                PendingIntent pendingIntent = PendingIntent.getBroadcast(
                        context, 0, updateIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                remoteViews.setOnClickPendingIntent(R.id.LunoidWidget, pendingIntent);
*/
                appWidgetManager.updateAppWidget(widgetId, remoteViews);
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
                mapLever.put(separated[0], separated[1]);
                mapCoucher.put(separated[0], separated[2]);
                mapPhase.put(separated[0], separated[3]);
                mapJour.put(separated[0], separated[4]);
                mapApogee.put(separated[0], separated[5]);
                mapPerigee.put(separated[0], separated[6]);
                mapNoeud.put(separated[0], separated[7]);
                mapMontant.put(separated[0], separated[9]);
                mapCroissant.put(separated[0], separated[10]);
                mapEclair.put(separated[0], separated[13]);
            }
        } catch (IOException e)
        {
            Log.e("Lunoid", "exception", e);
        }
        Log.d("ReadData", "end");
    }
}