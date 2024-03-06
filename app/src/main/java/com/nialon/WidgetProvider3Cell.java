package com.nialon;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;

import org.shredzone.commons.suncalc.MoonTimes;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import androidx.annotation.RequiresApi;

public class WidgetProvider3Cell extends AppWidgetProvider {
//    public static final String WIDGET_IDS_KEY = "mywidgetproviderwidgetids";
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
    private Calendar d2021;
    boolean Cityfound = false;
    double latitude = 47.23, longitude = 6.02;
    Node node;
    String xmlexpr;
    XPath xpath = XPathFactory.newInstance().newXPath();
    SimpleDateFormat fmt = new SimpleDateFormat("HH:mm", Locale.getDefault());
    Date mtS;
    Date mtR;

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        Log.d("widget1cell", "onEnabled");
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d("widget1cell", "onupdate");
        try {
            sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            dateString = sdf.format(Calendar.getInstance().getTime());
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            lh = prefs.getBoolean("timezone", false);
            lh = true;
            Log.d("datestring", dateString);
            d2021 = Calendar.getInstance();
            d2021.set(Calendar.DAY_OF_MONTH, 1);
            d2021.set(Calendar.MONTH, 0);
            d2021.set(Calendar.YEAR, 2021);
            d2021.set(Calendar.HOUR, 0);
            d2021.set(Calendar.MINUTE, 0);
            d2021.set(Calendar.SECOND, 0);
            String city = prefs.getString("ville", "Paris");
            InputSource inputSource = new InputSource(context.getResources().openRawResource(R.raw.locations));
            xmlexpr = "/cities/city[name=\"" + city + "\"]/lat";
            Log.d("xmlexpr", xmlexpr);
            node = (Node) xpath.evaluate(xmlexpr, inputSource, XPathConstants.NODE);
            Cityfound = false;
            if (node != null) {
                latitude = Double.parseDouble(node.getTextContent());
                xmlexpr = "/cities/city[name=\"" + city + "\"]/lon";
                InputSource inputSource2 = new InputSource(context.getResources().openRawResource(R.raw.locations));
                node = (Node) xpath.evaluate(xmlexpr, inputSource2, XPathConstants.NODE);
                if (node != null) {
                    longitude = Double.parseDouble(node.getTextContent());
                    Cityfound = true;
                }
            }
            if (Cityfound) {
                MoonTimes moonTimes = MoonTimes.compute().at(latitude, longitude).on(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH) + 1, Calendar.getInstance().get(Calendar.DAY_OF_MONTH)).execute();
                // todo : remove
                //MoonTimes moonTimes = MoonTimes.compute().at(latitude, longitude).on(year,monthOfYear+1,dayOfMonth).timezone("Africa/Casablanca").execute();

                mtR = moonTimes.getRise();
                mtS = moonTimes.getSet();
            }

            ReadData(context);
            ComponentName thisWidget = new ComponentName(context, WidgetProvider3Cell.class);
            int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
            for (int widgetId : allWidgetIds) {
                Log.d("widget ", Integer.toString(widgetId));
                RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget3cell);
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

                if (mtR != null) {
                    remoteViews.setTextViewText(R.id.lever, fmt.format(mtR));

                } else {
                    remoteViews.setTextViewText(R.id.lever, "--:--");
                }
                if (mtS != null) {
                    remoteViews.setTextViewText(R.id.coucher, fmt.format(mtS));
                } else {
                    remoteViews.setTextViewText(R.id.coucher, "--:--");
                }
                /*
                remoteViews.setTextViewText(R.id.lever, heurelocale(mapLever.get(dateString), lh));
                remoteViews.setTextViewText(R.id.coucher, heurelocale(mapCoucher.get(dateString), lh));
*/
                // feuille/fruit/racine/fleur
                if (mapJour.get(dateString).contains("Feuilles")) {
                    remoteViews.setImageViewResource(R.id.imageFeuille, R.drawable.salad30_on);
                } else {
                    remoteViews.setImageViewResource(R.id.imageFeuille, R.drawable.salad30_off);
                }

                if (mapJour.get(dateString).contains("Fruits")) {
                    remoteViews.setImageViewResource(R.id.imageFruit, R.drawable.apple30_on);
                } else {
                    remoteViews.setImageViewResource(R.id.imageFruit, R.drawable.apple30_off);
                }

                if (mapJour.get(dateString).contains("Racines")) {
                    remoteViews.setImageViewResource(R.id.imageRacine, R.drawable.carrot30_on);
                } else {
                    remoteViews.setImageViewResource(R.id.imageRacine, R.drawable.carrot30_off);
                }
                if (mapJour.get(dateString).contains("Fleurs")) {
                    remoteViews.setImageViewResource(R.id.imageFleur, R.drawable.flower30_on);
                } else {
                    remoteViews.setImageViewResource(R.id.imageFleur, R.drawable.flower30_off);
                }

                // launch main application on click

                // launch activity
                Intent launchActivity = new Intent(context, Lunoid.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, launchActivity, PendingIntent.FLAG_IMMUTABLE);
                remoteViews.setOnClickPendingIntent(R.id.LunoidWidget, pendingIntent);


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
        try
        {
            while (( line = buffreader.readLine()) != null) {
                //Log.d("Line",line);
                separated = line.split(";");
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
                mapCroissant.put(separated[0], separated[10]);
                mapSigne.put(separated[0], separated[11]);
                mapEclair.put(separated[0], separated[13]);
            }
        } catch (Exception e) {
            Log.e("Lunoid", "exception", e);
        }
        Log.d("ReadData", "end");
    }
}