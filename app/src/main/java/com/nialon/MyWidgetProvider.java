package com.nialon;
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

import android.app.PendingIntent;
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
public class MyWidgetProvider extends AppWidgetProvider
{

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
    static String dateString ;
    static Date date1;
    static Boolean lh;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d("widget", "onupdate");
        ComponentName thisWidget = new ComponentName(context, MyWidgetProvider.class);
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
        for (int widgetId : allWidgetIds) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget2sav);
            Intent intent = new Intent(context, MyWidgetProvider.class);
            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.lever, pendingIntent);
            remoteViews.setOnClickPendingIntent(R.id.coucher, pendingIntent);
            remoteViews.setOnClickPendingIntent(R.id.imageLune, pendingIntent);
            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        Log.d("widget", "onEnabled");

    }
	public void onUpdate2(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
    {
		Log.d("widget","onupdate");
		try
        {
            Log.d("widget", "init");
            sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            date1 = Calendar.getInstance().getTime();
            dateString = sdf.format(date1);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            lh = prefs.getBoolean("timezone", false);
            Log.d("datestring", dateString);

            ReadData(context);
		    // Get all ids
		    ComponentName thisWidget = new ComponentName(context, MyWidgetProvider.class);
		    int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
		    for (int widgetId : allWidgetIds)
		    {
		        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget2sav);
		        // Set the text

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
                    if (mapCroissant.get(dateString).equals("0"))
                    {
                        ecl3 = ecl2.concat(ecl);
                    }
                    else
                    {
                        ecl3 = ecl2.concat("_").concat(ecl);
                    }
                    Log.d("ecl3", ecl3);
                    int resId = context.getResources().getIdentifier(ecl3, "drawable", context.getPackageName());
                    Log.d("resid", Integer.toString(resId));
                    remoteViews.setImageViewResource(R.id.imageLuneWidget, resId);
                    remoteViews.setTextViewText(R.id.textPct,ecl.concat(" %"));
                }

                remoteViews.setTextViewText(R.id.lever, heurelocale(mapLever.get(dateString), date1, lh));
                remoteViews.setTextViewText(R.id.coucher, heurelocale(mapCoucher.get(dateString), date1, lh));
                Log.d("jour ", mapJour.get(dateString));
                if (mapJour.get(dateString).contains("Feuilles")) {
                    remoteViews.setImageViewResource(R.id.imageFeuille, R.drawable.salad30_on);
                }
                else
                {
                    remoteViews.setImageViewResource(R.id.imageFeuille, R.drawable.salad30_off);
                }

                if (mapJour.get(dateString).contains(" Fruits"))
                {
                    remoteViews.setImageViewResource(R.id.imageFruit, R.drawable.apple30_on);
                }
                else {
                    remoteViews.setImageViewResource(R.id.imageFruit, R.drawable.apple30_off);
                }

                if (mapJour.get(dateString).contains(" Racines"))
                {
                    remoteViews.setImageViewResource(R.id.imageRacine, R.drawable.carrot30_on);
                }
                else
                {
                    remoteViews.setImageViewResource(R.id.imageRacine, R.drawable.carrot30_off);
                }
                if (mapJour.get(dateString).contains(" Fleurs"))
                {
                    remoteViews.setImageViewResource(R.id.imageFleur, R.drawable.flower30_on);
                }
                else
                {
                    remoteViews.setImageViewResource(R.id.imageFleur, R.drawable.flower30_off);
                }


                /*
                // ANDPNA :apogee
                Log.d("apogee ", mapApogee.get(dateString));
                remoteViews.setTextViewText(R.id.heureevt, "88:88");
                remoteViews.setTextViewText(R.id.descevt, "-----");
                remoteViews.setTextColor(R.id.heureevt, Color.rgb(96,96,96));
                remoteViews.setTextColor(R.id.descevt, Color.rgb(96,96,96));

                if (!mapApogee.get(dateString).equals("0"))
                {
                    remoteViews.setTextViewText(R.id.descevt, "A----");
                    remoteViews.setTextViewText(R.id.heureevt, heurelocale(mapApogee.get(dateString),date1,lh));
                    remoteViews.setTextColor(R.id.heureevt, Color.RED);
                    remoteViews.setTextColor(R.id.descevt, Color.RED);
                }
                else {
                    // perigee
                    Log.d("perigee ", mapPerigee.get(dateString));
                    if (!mapPerigee.get(dateString).equals("0"))
                    {
                        remoteViews.setTextViewText(R.id.descevt, "---P-");
                        remoteViews.setTextViewText(R.id.heureevt, heurelocale(mapPerigee.get(dateString),date1,lh));
                        remoteViews.setTextColor(R.id.heureevt, Color.RED);
                        remoteViews.setTextColor(R.id.descevt, Color.RED);
                    } else
                    {
                        // noeud
                        Log.d("noeud ", mapNoeud.get(dateString));
                        if (mapNoeud.get(dateString).equals("0")) {
                            remoteViews.setTextViewText(R.id.descevt, "-----");
                        } else if (mapNoeud.get(dateString).equals("88:88")) {
                            remoteViews.setTextViewText(R.id.descevt, "-N---");
                            remoteViews.setTextColor(R.id.heureevt, Color.RED);
                            remoteViews.setTextColor(R.id.descevt, Color.RED);
                        } else if (mapNoeud.get(dateString).substring(5, 6).equals("+")) {
                            remoteViews.setTextViewText(R.id.descevt, "---NA");
                            remoteViews.setTextColor(R.id.heureevt, Color.RED);
                            remoteViews.setTextColor(R.id.descevt, Color.RED);
                            remoteViews.setTextViewText(R.id.heureevt, heurelocale(mapNoeud.get(dateString).substring(0, 5), date1, lh));
                        } else if (mapNoeud.get(dateString).substring(5, 6).equals("-")) {
                            remoteViews.setTextViewText(R.id.descevt, "-ND--");
                            remoteViews.setTextColor(R.id.heureevt, Color.RED);
                            remoteViews.setTextColor(R.id.descevt, Color.RED);
                            remoteViews.setTextViewText(R.id.heureevt, heurelocale(mapNoeud.get(dateString).substring(0, 5), date1, lh));
                        }
                    }
                }
                // montant / descendant
                Log.d("montant ", mapMontant.get(dateString));

                switch (mapMontant.get(dateString))
                {
                    case "0":
                    remoteViews.setImageViewResource(R.id.flechemontdesc, R.drawable.flechedescendante);
                    break;

                    case "1":
                    remoteViews.setImageViewResource(R.id.flechemontdesc, R.drawable.flechemontante);
                    break;

                    case "2":
                    case "3":
                    remoteViews.setImageViewResource(R.id.flechemontdesc, R.drawable.flechemontantedescendante);
                    break;
                }
                */

//                if (mapMontant.get(dateString).equals("0"))
//                {
//                    remoteViews.setImageViewResource(R.id.flechemontdesc, R.drawable.flechedescendante);
//                }
//                else if (mapMontant.get(dateString).equals("1"))
//                {
//                    remoteViews.setImageViewResource(R.id.flechemontdesc, R.drawable.flechemontante);
//                }
//                else if (mapMontant.get(dateString).equals("2"))
//                {
//                    remoteViews.setImageViewResource(R.id.flechemontdesc, R.drawable.flechemontantedescendante);
//                }
/*

		        // Register an onClickListener
		        Intent intent = new Intent(context, MyWidgetProvider.class);
		        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
		        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
		        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		        remoteViews.setOnClickPendingIntent(R.id.lever, pendingIntent);
		        remoteViews.setOnClickPendingIntent(R.id.coucher, pendingIntent);
                remoteViews.setOnClickPendingIntent(R.id.imageLune, pendingIntent);
		        appWidgetManager.updateAppWidget(widgetId, remoteViews);
		        */
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
        } catch (IOException e) {
            Log.e("Lunoid", "exception", e);
        }
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
                int nboffset;

                d.setHours(Integer.valueOf(s.substring(0, 2)));
                d.setMinutes(Integer.valueOf(s.substring(3, 5)));
                nboffset = TimeZone.getDefault().getOffset(d.getTime())/1000/3600;
                String s1;
                String s2;

                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.HOUR_OF_DAY, Integer.valueOf(s.substring(0, 2)));
                cal.set(Calendar.MINUTE, Integer.valueOf(s.substring(3, 5)));

                cal.add(Calendar.HOUR_OF_DAY, nboffset);
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