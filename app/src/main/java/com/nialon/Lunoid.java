package com.nialon;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.shredzone.commons.suncalc.MoonIllumination;
import org.shredzone.commons.suncalc.MoonTimes;
import org.shredzone.commons.suncalc.SunTimes;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import static android.provider.Settings.Secure.ANDROID_ID;
import static java.lang.Math.abs;

// done : signes du zodiaque : fait
// done : partager : fait
// done : envoyer un commentaire : fait
// done : évaluer l'application : fait
// done : vérifier nouvelle version : fait
// done : prendre des notes : fait
// done : partager par sms
// done : éditer une note depuis la liste : fait
// done : option pour enlever la pub
// done : choix du lieu pour calcul heure lever/coucher : fait

// https://www.jardinlunaire.fr/calendrier-lunaire
// https://www.vercalendario.info/fr/lune/france-mois-janvier-2022.html

@SuppressWarnings("ConstantConditions")
public class Lunoid extends FragmentActivity implements DatePickerDialog.OnDateSetListener {
    /**
     * Called when the activity is first created.
     */
    private SimpleDateFormat sdf;
    private SimpleDateFormat sdf2;
    static String dateString;  // 16/05/2020
    String dateString2;        // Saturday 16 May 2020
    private final Calendar today = Calendar.getInstance();
    static Calendar selday = Calendar.getInstance();
    static Calendar calMax;
    static Calendar calMin;
    private GestureDetector mGestureDetector;
    private static Boolean lh;
    private static Boolean sifm;
    private static Boolean hemispheresud;
    public final static String EXTRA_MESSAGE = "com.nialon.Lunoid.MESSAGE";
    private static String htmltxt;
    StringBuilder linetot = new StringBuilder();
    String[] tableauMois = {"janvier", "fevrier", "mars", "avril", "mai", "juin", "juillet", "aout", "septembre", "octobre", "novembre", "decembre"};
    private Menu _menu = null;
    String temp;
    private static AtomicBoolean isRunningTest;
    private Calendar d2021;
    private Calendar d2023;
    XPath xpath = XPathFactory.newInstance().newXPath();
    static Date sunRise;
    static Date sunSet;

//todo: ads
//    private AdManagerAdView adManagerAdView;

    private SharedPreferences AppPrefs;
    boolean Cityfound = false;
    private int currentHelp;

    // ads
    //private InterstitialAd mInterstitialAd;

    static Map<String, String> mapLever = new HashMap<>();
    static Map<String, String> mapCoucher = new HashMap<>();
    static Map<String, String> mapPhase = new HashMap<>();
    static Map<String, String> mapJour = new HashMap<>();
    static Map<String, String> mapApogee = new HashMap<>();
    static Map<String, String> mapPerigee = new HashMap<>();
    static Map<String, String> mapNoeud = new HashMap<>();
    Map<String, String> mapComment = new HashMap<>();
    Map<String, String> mapCroissant = new HashMap<>();
    static Map<String, String> mapMontant = new HashMap<>();
    Map<String, String> mapSigne = new HashMap<>();
    Map<String, String> mapConseil = new HashMap<>();
    static Map<String, String> mapEclair = new HashMap<>();

    TextView textLever;
    TextView textCoucher;
    TextView textFruits;
    TextView textFleurs;
    TextView textFeuilles;
    TextView textRacines;
    TextView textApogee;
    TextView textPerigee;
    TextView textPerigeeHour;
    TextView textApogeeHour;
    TextView textNoeudHour;
    TextView textNoeud;
    TextView textCroissant;
    TextView textDecroissant;
    TextView textMontant;
    TextView textDescendant;
    TextView textJour;
    TextView textPct;
    ImageView imgLune;
    ImageView imageFeuille;
    ImageView imageFruit;
    ImageView imageRacine;
    ImageView imageFleur;
    TextView textHMD;
    TextView textHCD;
    String InfosStr;
    ImageView imageNote;
    TextView textFeuillesChg;
    TextView textRacinesChg;
    TextView textFleursChg;
    TextView textFruitsChg;
    ImageView imageTau;
    ImageView imageVir;
    ImageView imageCap;
    ImageView imageAri;
    ImageView imageLeo;
    ImageView imageSag;
    ImageView imageGem;
    ImageView imageLib;
    ImageView imageAqu;
    ImageView imagePis;
    ImageView imageCan;
    ImageView imageSco;
    TextView textTau;
    TextView textVir;
    TextView textCap;
    TextView textAri;
    TextView textLeo;
    TextView textSag;
    TextView textGem;
    TextView textLib;
    TextView textAqu;
    TextView textPis;
    TextView textCan;
    TextView textSco;
    LinearLayout lmain;
    LinearLayout lcroidec;


    @SuppressLint("SetTextI18n")
    public void onDateChange(int year, int monthOfYear, int dayOfMonth) {
        int resId;
        SimpleDateFormat fmt = new SimpleDateFormat("HH:mm", Locale.getDefault());
        double latitude = 47.23, longitude = 6.02;
        Node node;
        String xmlexpr;

        try {
            // todo: remove changement fond chaque jour
            // int resourceId = getResources().getIdentifier("ciel"+(1+dayOfMonth%12), "drawable", getPackageName());
            // lmain.setBackgroundResource(resourceId);

            Log.d("Lunoid", "onDateChange");
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            String city = prefs.getString("ville", "Paris");
            InputSource inputSource = new InputSource(getResources().openRawResource(R.raw.locations));
            xmlexpr = "/cities/city[name=\"" + city + "\"]/lat";
            Log.d("xmlexpr", xmlexpr);
            node = (Node) xpath.evaluate(xmlexpr, inputSource, XPathConstants.NODE);
            Cityfound = false;
            if (node != null) {
                latitude = Double.parseDouble(node.getTextContent());
                xmlexpr = "/cities/city[name=\"" + city + "\"]/lon";
                InputSource inputSource2 = new InputSource(getResources().openRawResource(R.raw.locations));
                node = (Node) xpath.evaluate(xmlexpr, inputSource2, XPathConstants.NODE);
                if (node != null) {
                    longitude = Double.parseDouble(node.getTextContent());
                    Cityfound = true;
                }
            }
            // todo : remove test location
            //fmt.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));
            //fmt.setTimeZone(TimeZone.getTimeZone("Indian/Antananarivo"));
            //fmt.setTimeZone(TimeZone.getTimeZone("Pacific/Noumea"));
            //fmt.setTimeZone(TimeZone.getTimeZone("America/Guadeloupe"));
            //fmt.setTimeZone(TimeZone.getTimeZone("Africa/Casablanca"));
            //fmt.setTimeZone(TimeZone.getTimeZone("Europe/Vienna"));

            //latitude = 48.85;
            //longitude = 2.35;

            //Log.d("moonrisetime", astro.moonRiseTime(48.0, 2.0, year, monthOfYear+1, dayOfMonth));
            //Log.d("moonsettime", astro.moonSetTime(48.0, 2.0, year, monthOfYear+1, dayOfMonth));

            selday.set(Calendar.YEAR, year);
            selday.set(Calendar.MONTH, monthOfYear);
            selday.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            Log.d("dayofmonth", Integer.toString(dayOfMonth));
            Log.d("monthofyear", Integer.toString(monthOfYear));
            Log.d("year", Integer.toString(year));

            dateString2 = sdf2.format(selday.getTime());
            if (selday.compareTo(calMin) >= 0 && selday.compareTo(calMax) <= 0) {
                dateString = sdf.format(selday.getTime());

                Log.d("datestring", dateString);
                Log.d("datestring2", dateString2);
                LinearLayout lzodiaque = findViewById(R.id.linearLayoutSignesZodiaque);
                LinearLayout ltextzodiaque = findViewById(R.id.linearLayoutTextesZodiaque);

                if (selday.getTimeInMillis() - d2021.getTimeInMillis() >= -1000 && prefs.getBoolean("displayzodiac", false)) {
                    lzodiaque.setVisibility(View.VISIBLE);
                } else {
                    lzodiaque.setVisibility(View.GONE);
                }
                if (selday.getTimeInMillis() - d2021.getTimeInMillis() >= -1000 && prefs.getBoolean("displayzodiactext", false)) {
                    ltextzodiaque.setVisibility(View.VISIBLE);
                } else {
                    ltextzodiaque.setVisibility(View.GONE);
                }
                // .timezone("Europe/Paris")
                // https://fr.tutiempo.net/abidjan.html?donnees=calendrier
                // https://www.sunrise-and-sunset.com/fr/moon/allemagne/berlin

                if (Cityfound) {
                    Date mtS;
                    Date mtR;
                    MoonTimes moonTimes = MoonTimes.compute().at(latitude, longitude).on(year, monthOfYear + 1, dayOfMonth).execute();
                    // todo : remove
                    //MoonTimes moonTimes = MoonTimes.compute().at(latitude, longitude).on(year,monthOfYear+1,dayOfMonth).timezone("Africa/Casablanca").execute();

                    mtR = moonTimes.getRise();
                    mtS = moonTimes.getSet();

                    if (mtR != null) {
                        textLever.setText(fmt.format(mtR));
                    } else {
                        textLever.setText("--:--");
                    }
                    if (mtS != null) {
                        textCoucher.setText(fmt.format(mtS));
                    } else {
                        textCoucher.setText("--:--");
                    }
                    SunTimes sunTimes = SunTimes.compute().at(latitude, longitude).on(year, monthOfYear + 1, dayOfMonth).execute();
                    sunRise = sunTimes.getRise();
                    sunSet = sunTimes.getSet();

                    MoonIllumination moonIllumination = MoonIllumination.compute()
                            .on(year, monthOfYear + 1, dayOfMonth)
                            .execute();
                    //double phase = moonIllumination.getPhase();
                    double percent = moonIllumination.getFraction() * 100;
                    String moonphase = (int) Math.round(percent) + "%";
                    //todo: moonphase automatique
                    //textPct.setText(moonphase);
                } else {
                    //textCoucher.setText(heurelocale(astro.moonSetTime(48.0, 2.0, year, monthOfYear + 1, dayOfMonth), selday, lh));
                    //textLever.setText(heurelocale(astro.moonRiseTime(48.0, 2.0, year, monthOfYear + 1, dayOfMonth), selday, lh));
                    textCoucher.setText(heurelocale(mapCoucher.get(dateString), selday, lh));
                    textLever.setText(heurelocale(mapLever.get(dateString), selday, lh));
                }
                textJour.setText(dateString2);

                SharedPreferences sp = getSharedPreferences("myNotes", MODE_PRIVATE);
                String str1 = sp.getString(dateString, "");
                if (!str1.equals("")) {
                    imageNote.setAlpha(1.0f);
                    imageNote.setColorFilter(Color.YELLOW);
                } else {
                    imageNote.setAlpha(0.5f);
                    imageNote.clearColorFilter();
                }

                String ecl = mapEclair.get(dateString);
                try {
                    if (ecl.equals("-")) {
                        String ecl2 = "lune0";
                        String ecl3;
                        int ph = Integer.parseInt(mapPhase.get(dateString));
                        ecl3 = ecl2.concat(String.format(Locale.getDefault(), "%02d", ph));
                        Log.d("ecl3", ecl3);
                        resId = getResources().getIdentifier(ecl3, "drawable", getPackageName());
                        Log.d("resid", Integer.toString(resId));
                        imgLune.setImageResource(resId);
                        if (textPct != null) {
                            textPct.setText("");
                        }
                    } else {
                        String ecl2 = "nlune";
                        String ecl3;
                        if (mapCroissant.get(dateString).equals("0")) {
                            ecl3 = ecl2.concat(ecl);
                        } else {
                            ecl3 = ecl2.concat("_").concat(ecl);
                        }
                        Log.d("ecl3", ecl3);
                        resId = getResources().getIdentifier(ecl3, "drawable", getPackageName());
                        Log.d("resid", Integer.toString(resId));
                        imgLune.setImageResource(resId);
                        //if (dateString2.equals("mercredi 31 janvier 2018") || dateString2.equals("samedi 31 octobre 2020")) {
                        if (mapComment.get(dateString).contains("Lune bleue")) {
                            Log.d("d", "lune bleue");
                            resId = getResources().getIdentifier("nlune100bleue", "drawable", getPackageName());
                            imgLune.setImageResource(resId);
                        }

                        if (textPct != null) {
                            if (prefs.getBoolean("displayjoursynod", false) &&
                                    selday.getTimeInMillis() - d2023.getTimeInMillis() >= -1000) {
                                textPct.setText(mapPhase.get(dateString));
                            } else {
                                textPct.setText(ecl.concat(" %"));
                            }

                        }

                    }
                } catch (Exception e) {
                    Log.e("Lunoid", "exception", e);
                }
                // Fruits/Fleurs/Feuilles/Racines
                textFeuilles.setTextColor(mapJour.get(dateString).contains("Feuilles") ? Color.YELLOW : Color.rgb(120, 120, 120));
                textFleurs.setTextColor(mapJour.get(dateString).contains("Fleurs") ? Color.YELLOW : Color.rgb(120, 120, 120));
                textFruits.setTextColor(mapJour.get(dateString).contains("Fruits") ? Color.YELLOW : Color.rgb(120, 120, 120));
                textRacines.setTextColor(mapJour.get(dateString).contains("Racines") ? Color.YELLOW : Color.rgb(120, 120, 120));
                resId = getResources().getIdentifier(mapJour.get(dateString).contains("Racines") ? "carrot30_on" : "carrot30_off", "drawable", getPackageName());
                imageRacine.setImageResource(resId);
                resId = getResources().getIdentifier(mapJour.get(dateString).contains("Fleurs") ? "flower30_on" : "flower30_off", "drawable", getPackageName());
                imageFleur.setImageResource(resId);
                resId = getResources().getIdentifier(mapJour.get(dateString).contains("Feuilles") ? "salad30_on" : "salad30_off", "drawable", getPackageName());
                imageFeuille.setImageResource(resId);
                resId = getResources().getIdentifier(mapJour.get(dateString).contains("Fruits") ? "apple30_on" : "apple30_off", "drawable", getPackageName());
                imageFruit.setImageResource(resId);
                textFeuillesChg.setTextColor(Color.rgb(120, 120, 120));
                textFruitsChg.setTextColor(Color.rgb(120, 120, 120));
                textRacinesChg.setTextColor(Color.rgb(120, 120, 120));
                textFleursChg.setTextColor(Color.rgb(120, 120, 120));
                textFeuillesChg.setText("");
                textFruitsChg.setText("");
                textRacinesChg.setText("");
                textFleursChg.setText("");

                if (mapJour.get(dateString).length() >= 15 && (mapJour.get(dateString)).toLowerCase().startsWith("feuilles/fruits")) {
                    textFeuillesChg.setText("<" + heurelocale(mapJour.get(dateString).substring(16, 21), selday, lh));
                    textFruitsChg.setText(">" + heurelocale(mapJour.get(dateString).substring(16, 21), selday, lh));
                    textFeuillesChg.setTextColor(Color.YELLOW);
                    textFruitsChg.setTextColor(Color.YELLOW);
                } else if (mapJour.get(dateString).length() >= 14 && (mapJour.get(dateString)).toLowerCase().startsWith("fruits/racines")) {
                    textFruitsChg.setText("<" + heurelocale(mapJour.get(dateString).substring(15, 20), selday, lh));
                    textRacinesChg.setText(">" + heurelocale(mapJour.get(dateString).substring(15, 20), selday, lh));
                    textFruitsChg.setTextColor(Color.YELLOW);
                    textRacinesChg.setTextColor(Color.YELLOW);
                } else if (mapJour.get(dateString).length() >= 14 && (mapJour.get(dateString)).toLowerCase().startsWith("racines/fleurs")) {
                    textRacinesChg.setText("<" + heurelocale(mapJour.get(dateString).substring(15, 20), selday, lh));
                    textFleursChg.setText(">" + heurelocale(mapJour.get(dateString).substring(15, 20), selday, lh));
                    textRacinesChg.setTextColor(Color.YELLOW);
                    textFleursChg.setTextColor(Color.YELLOW);
                } else if (mapJour.get(dateString).length() >= 15 && (mapJour.get(dateString)).toLowerCase().startsWith("fleurs/feuilles")) {
                    textFleursChg.setText("<" + heurelocale(mapJour.get(dateString).substring(16, 21), selday, lh));
                    textFeuillesChg.setText(">" + heurelocale(mapJour.get(dateString).substring(16, 21), selday, lh));
                    textFleursChg.setTextColor(Color.YELLOW);
                    textFeuillesChg.setTextColor(Color.YELLOW);
                }
                // signe zodiacale
                Log.d("sign", mapSigne.get(dateString));
                resId = getResources().getIdentifier(mapSigne.get(dateString).contains("Tau") ? "tau" : "tau_off", "drawable", getPackageName());
                imageTau.setImageResource(resId);
                resId = getResources().getIdentifier(mapSigne.get(dateString).contains("Vie") ? "vir" : "vir_off", "drawable", getPackageName());
                imageVir.setImageResource(resId);
                resId = getResources().getIdentifier(mapSigne.get(dateString).contains("Cap") ? "cap" : "cap_off", "drawable", getPackageName());
                imageCap.setImageResource(resId);
                resId = getResources().getIdentifier(mapSigne.get(dateString).contains("Bel") ? "arie" : "arie_off", "drawable", getPackageName());
                imageAri.setImageResource(resId);
                resId = getResources().getIdentifier(mapSigne.get(dateString).contains("Lio") ? "leo" : "leo_off", "drawable", getPackageName());
                imageLeo.setImageResource(resId);
                resId = getResources().getIdentifier(mapSigne.get(dateString).contains("Sag") ? "sag" : "sag_off", "drawable", getPackageName());
                imageSag.setImageResource(resId);
                resId = getResources().getIdentifier(mapSigne.get(dateString).contains("Gem") ? "gem" : "gem_off", "drawable", getPackageName());
                imageGem.setImageResource(resId);
                resId = getResources().getIdentifier(mapSigne.get(dateString).contains("Bal") ? "lib" : "lib_off", "drawable", getPackageName());
                imageLib.setImageResource(resId);
                resId = getResources().getIdentifier(mapSigne.get(dateString).contains("Ver") ? "aqu" : "aqu_off", "drawable", getPackageName());
                imageAqu.setImageResource(resId);
                resId = getResources().getIdentifier(mapSigne.get(dateString).contains("Poi") ? "pisc" : "pisc_off", "drawable", getPackageName());
                imagePis.setImageResource(resId);
                resId = getResources().getIdentifier(mapSigne.get(dateString).contains("Can") ? "can" : "can_off", "drawable", getPackageName());
                imageCan.setImageResource(resId);
                resId = getResources().getIdentifier(mapSigne.get(dateString).contains("Sco") ? "sco" : "sco_off", "drawable", getPackageName());
                imageSco.setImageResource(resId);
                textPis.setTextColor(mapSigne.get(dateString).contains("Poi") ? Color.YELLOW : Color.rgb(120, 120, 120));
                textSco.setTextColor(mapSigne.get(dateString).contains("Sco") ? Color.YELLOW : Color.rgb(120, 120, 120));
                textCan.setTextColor(mapSigne.get(dateString).contains("Can") ? Color.YELLOW : Color.rgb(120, 120, 120));
                textSag.setTextColor(mapSigne.get(dateString).contains("Sag") ? Color.YELLOW : Color.rgb(120, 120, 120));
                textLeo.setTextColor(mapSigne.get(dateString).contains("Lio") ? Color.YELLOW : Color.rgb(120, 120, 120));
                textAri.setTextColor(mapSigne.get(dateString).contains("Bel") ? Color.YELLOW : Color.rgb(120, 120, 120));
                textCap.setTextColor(mapSigne.get(dateString).contains("Cap") ? Color.YELLOW : Color.rgb(120, 120, 120));
                textVir.setTextColor(mapSigne.get(dateString).contains("Vie") ? Color.YELLOW : Color.rgb(120, 120, 120));
                textTau.setTextColor(mapSigne.get(dateString).contains("Tau") ? Color.YELLOW : Color.rgb(120, 120, 120));
                textAqu.setTextColor(mapSigne.get(dateString).contains("Ver") ? Color.YELLOW : Color.rgb(120, 120, 120));
                textLib.setTextColor(mapSigne.get(dateString).contains("Bal") ? Color.YELLOW : Color.rgb(120, 120, 120));
                textGem.setTextColor(mapSigne.get(dateString).contains("Gem") ? Color.YELLOW : Color.rgb(120, 120, 120));
                // Apogee/Perigee/Noeud
                textApogee.setTextColor(!mapApogee.get(dateString).equals("0") ? Color.RED : Color.rgb(120, 120, 120));
                textPerigee.setTextColor(!mapPerigee.get(dateString).equals("0") ? Color.RED : Color.rgb(120, 120, 120));
                textNoeud.setTextColor(!mapNoeud.get(dateString).equals("0") ? Color.RED : Color.rgb(120, 120, 120));

                try {
                    textApogeeHour.setTextColor(!mapApogee.get(dateString).equals("0") ? Color.RED : Color.rgb(120, 120, 120));
                    textApogeeHour.setText(!mapApogee.get(dateString).equals("0") ? (mapApogee.get(dateString).equals("88:88") ? "--:--" : heurelocale(mapApogee.get(dateString), selday, lh)) : "--:--");

                    textPerigeeHour.setTextColor(!mapPerigee.get(dateString).equals("0") ? Color.RED : Color.rgb(120, 120, 120));
                    textPerigeeHour.setText(!mapPerigee.get(dateString).equals("0") ? (mapPerigee.get(dateString).equals("88:88") ? "--:--" : heurelocale(mapPerigee.get(dateString), selday, lh)) : "--:--");

                    //textNoeudHour.setTextColor(!mapNoeud.get(dateString).equals("0") ? Color.RED : Color.rgb(120, 120, 120));
                    //textNoeudHour.setText(!mapNoeud.get(dateString).equals("0") ? (mapNoeud.get(dateString).equals("88:88") ? "--:--" : heurelocale(mapNoeud.get(dateString).substring(0, 5), selday, lh)) : "--:--");
                    if (mapNoeud.get(dateString).equals("0")) {
                        textNoeudHour.setTextColor(Color.rgb(120, 120, 120));
                        textNoeudHour.setText("--:--");
                    } else {
                        String temp;
                        temp = heurelocale(mapNoeud.get(dateString).substring(0, 5), selday, lh);
                        if (mapNoeud.get(dateString).endsWith("+")) {
                            temp = temp + "/";
                        }
                        if (mapNoeud.get(dateString).endsWith("-")) {
                            temp = temp + "\\";
                        }
                        textNoeudHour.setTextColor(Color.RED);
                        textNoeudHour.setText(temp);
                    }
                } catch (Exception e) {
                    Log.e("Lunoid", "exception", e);
                }
                // Montant/Descendant
                if (mapMontant.get(dateString).equals("1") || mapMontant.get(dateString).startsWith("2") || mapMontant.get(dateString).startsWith("3")) {
                    textMontant.setTextColor(Color.YELLOW);
                } else {
                    textMontant.setTextColor(Color.rgb(120, 120, 120));
                }
                if (mapMontant.get(dateString).equals("0") || mapMontant.get(dateString).startsWith("2") || mapMontant.get(dateString).startsWith("3")) {
                    textDescendant.setTextColor(Color.YELLOW);
                } else {
                    textDescendant.setTextColor(Color.rgb(120, 120, 120));
                }
                if (mapMontant.get(dateString).startsWith("2")) {
                    textHMD.setTextColor(Color.YELLOW);
                    temp = ">" + heurelocale(mapMontant.get(dateString).substring(2, 7), selday, lh) + ">";
                    textHMD.setText(temp);
                } else if (mapMontant.get(dateString).startsWith("3")) {
                    textHMD.setTextColor(Color.YELLOW);
                    temp = "<" + heurelocale(mapMontant.get(dateString).substring(2, 7), selday, lh) + "<";
                    textHMD.setText(temp);
                } else {
                    textHMD.setTextColor(Color.rgb(120, 120, 120));
                    textHMD.setText(R.string.h0);
                }
                // Croissant/Decroissant
                textCroissant.setTextColor(mapCroissant.get(dateString).equals("1") ? Color.YELLOW : (mapCroissant.get(dateString).startsWith("2") || mapCroissant.get(dateString).startsWith("3")) ? Color.YELLOW : Color.rgb(120, 120, 120));
                textDecroissant.setTextColor(mapCroissant.get(dateString).equals("0") ? Color.YELLOW : (mapCroissant.get(dateString).startsWith("2") || mapCroissant.get(dateString).startsWith("3")) ? Color.YELLOW : Color.rgb(120, 120, 120));
                if (mapCroissant.get(dateString).startsWith("2")) {
                    textHCD.setTextColor(Color.YELLOW);
                    temp = ">" + heurelocale(mapCroissant.get(dateString).substring(2, 7), selday, lh) + ">";
                    textHCD.setText(temp);
                } else if (mapCroissant.get(dateString).startsWith("3")) {
                    textHCD.setTextColor(Color.YELLOW);
                    temp = "<" + heurelocale(mapCroissant.get(dateString).substring(2, 7), selday, lh) + "<";
                    textHCD.setText(temp);
                } else {
                    textHCD.setTextColor(Color.rgb(120, 120, 120));
                    textHCD.setText(R.string.h0);
                }
                InfosStr = "";
                if (!mapComment.get(dateString).equals("")) {
                    InfosStr += mapComment.get(dateString);
                    if (mapEclair.get(dateString).equals("100")) {
                        InfosStr += "\n" + this.getResources().getString(R.string.pleinelune);
                    }
                    if (mapEclair.get(dateString).equals("0")) {
                        InfosStr += "\n" + this.getResources().getString(R.string.nouvellelune);
                    }
                } else {
                    if (mapEclair.get(dateString).equals("100")) {
                        InfosStr = this.getResources().getString(R.string.pleinelune);
                    }
                    if (mapEclair.get(dateString).equals("0")) {
                        InfosStr = this.getResources().getString(R.string.nouvellelune);
                    }
                }
                if (!InfosStr.equals("")) {
                    Toast InfosToast = Toast.makeText(getApplicationContext(), InfosStr, Toast.LENGTH_SHORT);
                    InfosToast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
                    InfosToast.show();
                }
                if (getMenu() != null) {
                    ShowHideInfosMenu(selday.get(Calendar.YEAR), selday.get(Calendar.MONTH), getMenu());
                }
            } else {
                textLever.setText("--:--");
                textCoucher.setText("--:--");
                textJour.setText(dateString2);
                textFeuilles.setTextColor(Color.LTGRAY);
                textFleurs.setTextColor(Color.LTGRAY);
                textFruits.setTextColor(Color.LTGRAY);
                textRacines.setTextColor(Color.LTGRAY);
                textMontant.setTextColor(Color.LTGRAY);
                textDescendant.setTextColor(Color.LTGRAY);
                textCroissant.setTextColor(Color.LTGRAY);
                textDecroissant.setTextColor(Color.LTGRAY);
                textApogee.setTextColor(Color.LTGRAY);
                textApogeeHour.setTextColor(Color.LTGRAY);
                resId = getResources().getIdentifier("carrot30_off", "drawable", getPackageName());
                imageRacine.setImageResource(resId);
                resId = getResources().getIdentifier("apple30_off", "drawable", getPackageName());
                imageFruit.setImageResource(resId);
                resId = getResources().getIdentifier("flower30_off", "drawable", getPackageName());
                imageFleur.setImageResource(resId);
                resId = getResources().getIdentifier("salad30_off", "drawable", getPackageName());
                imageFeuille.setImageResource(resId);
                if (textPct != null) {
                    textPct.setText(("0 %"));
                }
                resId = getResources().getIdentifier("nlune_0", "drawable", getPackageName());
                imgLune.setImageResource(resId);
                try {
                    textPerigeeHour.setTextColor(Color.LTGRAY);
                    textPerigee.setTextColor(Color.LTGRAY);
                    textNoeudHour.setTextColor(Color.LTGRAY);
                } catch (Exception e) {
                    Log.e("Lunoid", "exception", e);
                }
                textNoeud.setTextColor(Color.LTGRAY);
                Toast InfosToast;
                InfosToast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.pleaseUpgrade), Toast.LENGTH_LONG);

                InfosToast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
                InfosToast.show();
            }
        } catch (Exception e) {
            Log.e("Lunoid", "exception", e);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("OnCreate", "");
        // Pour enlever la barre de titre :
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Pour enlever le nom de l'application dans la barre de titre
        getActionBar().setDisplayShowTitleEnabled(false);
        selday.set(Calendar.HOUR, 0);
        selday.set(Calendar.MINUTE, 0);
        selday.set(Calendar.SECOND, 0);
        d2021 = Calendar.getInstance();
        d2021.set(Calendar.DAY_OF_MONTH, 1);
        d2021.set(Calendar.MONTH, 0);
        d2021.set(Calendar.YEAR, 2021);
        d2021.set(Calendar.HOUR, 0);
        d2021.set(Calendar.MINUTE, 0);
        d2021.set(Calendar.SECOND, 0);
        d2023 = Calendar.getInstance();
        d2023.set(Calendar.DAY_OF_MONTH, 1);
        d2023.set(Calendar.MONTH, 0);
        d2023.set(Calendar.YEAR, 2023);
        d2023.set(Calendar.HOUR, 0);
        d2023.set(Calendar.MINUTE, 0);
        d2023.set(Calendar.SECOND, 0);

// todo: remove (pour changer le format de date)
//        Locale locale = new Locale("fr");
//        Locale.setDefault(locale);
//        Configuration config = getBaseContext().getResources().getConfiguration();
//        config.locale = locale;


        sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        sdf2 = new SimpleDateFormat("EEEE dd MMMM yyyy", Locale.getDefault());

        AppPrefs = PreferenceManager.getDefaultSharedPreferences(this);
// todo: remove location paris
//  sdf.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));

        setContentView(R.layout.main);

        // ads interstial
        /*
        MobileAds.initialize(this, "ca-app-pub-4468029712209847~1248699460");
        mInterstitialAd = new InterstitialAd(this);
        // mInterstitialAd.setAdUnitId("ca-app-pub-4468029712209847/1655893570"); // ad de prod
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712"); // ad de tests interstitiel
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }
        */
        // ads banner
        /*
        PublisherAdView mPublisherAdView = findViewById(R.id.publisherAdView);
        PublisherAdRequest adRequest = new PublisherAdRequest.Builder().build();
        mPublisherAdView.setAdSizes(AdSize.BANNER);
        //mPublisherAdView.setAdUnitId("ca-app-pub-4468029712209847/4219671648"); // prod ( à mettre dans le layout main )
        //mPublisherAdView.setAdUnitId("ca-app-pub-3940256099942544/6300978111"); // test
        mPublisherAdView.loadAd(adRequest);
         */
// todo: ads
//        adManagerAdView = findViewById(R.id.adView);
//        AdSize adSize = getAdSize();
//        adManagerAdView.setAdSizes(adSize);
//        adManagerAdView.loadAd(new AdManagerAdRequest.Builder().build());

        //adManagerAdView.setAdSizes(AdSize.SMART_BANNER);
        //adManagerAdView.setAdUnitId("ca-app-pub-4468029712209847/4219671648");  // prod
        //adManagerAdView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");  // test
        // Start loading the ad.

        //final Context context = getApplicationContext();
        //SharedPreferences prefs = this.getSharedPreferences("com.nialon",Context.MODE_PRIVATE);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
        //lh = prefs.getBoolean("timezone", false);
        lh = true;
        sifm = prefs.getBoolean("infosmois", false);
        hemispheresud = prefs.getBoolean("hemisphere", false);

        //reviewmanager = ReviewManagerFactory.create(this);
        /*
        reviewmanager = new FakeReviewManager(this);
        Log.d("request", "create");

        Task<ReviewInfo> request = reviewmanager.requestReviewFlow();
        Log.d("request", "reviewflow"+ request.toString());
        request.addOnCompleteListener(task -> {
            Log.e("request", "complete");
            if (task.isSuccessful()) {
                reviewInfo = task.getResult();
            } else {
            }
        });
         */

        //Determine screen size
        int screenSize;
        if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE) {
            Log.d("Screen size : ", "Extra Large");
            screenSize = 4;
        } else if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE) {
            Log.d("Screen size : ", "Large");
            screenSize = 3;
        } else if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_NORMAL) {
            Log.d("Screen size : ", "Normal");
            screenSize = 2;
        } else if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_SMALL) {
            Log.d("Screen size : ", "Small");
            screenSize = 1;
        } else {
            Log.d("Screen size : ", "Unknown");
            screenSize = 0;
        }
        /* Format title */
        //TextView title =  findViewById(android.R.id.title);
        //title.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
        //title.setTextColor(Color.YELLOW);
        //title.setTextSize(20);
        //private ReviewManager reviewmanager;
        //private ReviewInfo reviewInfo;
        Typeface m_Typeface = Typeface.createFromAsset(this.getAssets(), "led_counter-7.ttf");
        //m_Typeface = Typeface.createFromAsset(this.getAssets(), "LED.Font.ttf");

        /* Get the id of views */
        textLever = findViewById(R.id.textLever);
        textCoucher = findViewById(R.id.textCoucher);
        textFruits = findViewById(R.id.textFruits);
        textFleurs = findViewById(R.id.textFleurs);
        textFeuilles = findViewById(R.id.textFeuilles);
        textRacines = findViewById(R.id.textRacines);
        textApogee = findViewById(R.id.textApogee);
        textPerigee = findViewById(R.id.textPerigee);
        textPerigeeHour =  findViewById(R.id.textPerigeeHour);
        textApogeeHour = findViewById(R.id.textApogeeHour);
        textNoeudHour =  findViewById(R.id.textNoeudHour);
        textNoeud =  findViewById(R.id.textNoeud);
        textCroissant =  findViewById(R.id.textCroissant);
        textDecroissant = findViewById(R.id.textDecroissant);
        textMontant =  findViewById(R.id.textMontant);
        textDescendant = findViewById(R.id.textDescendant);
        textJour = findViewById(R.id.TextJour);
        textPct = findViewById(R.id.textPct);
        imgLune = findViewById(R.id.imageLune);
        imageFeuille = findViewById(R.id.imageFeuille);
        imageFleur = findViewById(R.id.imageFleur);
        imageFruit = findViewById(R.id.imageFruit);
        imageRacine = findViewById(R.id.imageRacine);
        imageNote = findViewById(R.id.imgNote);
        textHMD = findViewById(R.id.textHMD);
        textHCD = findViewById(R.id.textHCD);
        textFeuillesChg = findViewById(R.id.textFeuillesChg);
        textRacinesChg = findViewById(R.id.textRacinesChg);
        textFleursChg = findViewById(R.id.textFleursChg);
        textFruitsChg = findViewById(R.id.textFruitsChg);
        imageTau = findViewById(R.id.imageTau);
        imageVir = findViewById(R.id.imageVir);
        imageCap = findViewById(R.id.imageCap);
        imageAri = findViewById(R.id.imageAri);
        imageLeo = findViewById(R.id.imageLeo);
        imageSag = findViewById(R.id.imageSag);
        imageGem = findViewById(R.id.imageGem);
        imageLib = findViewById(R.id.imageLib);
        imageAqu = findViewById(R.id.imageAqu);
        imagePis = findViewById(R.id.imagePis);
        imageCan = findViewById(R.id.imageCan);
        imageSco = findViewById(R.id.imageSco);
        textTau = findViewById(R.id.textTau);
        textVir = findViewById(R.id.textVie);
        textCap = findViewById(R.id.textCap);
        textAri = findViewById(R.id.textBel);
        textLeo = findViewById(R.id.textLeo);
        textSag = findViewById(R.id.textSag);
        textGem = findViewById(R.id.textGem);
        textLib = findViewById(R.id.textBal);
        textAqu = findViewById(R.id.textVer);
        textPis = findViewById(R.id.textPis);
        textCan = findViewById(R.id.textCan);
        textSco = findViewById(R.id.textSco);
        textTau.setText(getResources().getString(R.string.Taurus).substring(0, 3));
        textVir.setText(getResources().getString(R.string.Virgo).substring(0, 3));
        textCap.setText(getResources().getString(R.string.Capricorn).substring(0, 3));
        textAri.setText(getResources().getString(R.string.Aries).substring(0, 3));
        textLeo.setText(getResources().getString(R.string.Leo).substring(0, 3));
        textSag.setText(getResources().getString(R.string.Sagittarius).substring(0, 3));
        textGem.setText(getResources().getString(R.string.Gemini).substring(0, 3));
        textLib.setText(getResources().getString(R.string.Libra).substring(0, 3));
        textAqu.setText(getResources().getString(R.string.Aquarius).substring(0, 3));
        textPis.setText(getResources().getString(R.string.Pisces).substring(0, 3));
        textCan.setText(getResources().getString(R.string.Cancer).substring(0, 3));
        textSco.setText(getResources().getString(R.string.Scorpio).substring(0, 3));
        lmain = findViewById(R.id.linearLayoutMain);
        lcroidec = findViewById(R.id.linearLayoutCroiDec);

        textLever.setTypeface(m_Typeface);
        textCoucher.setTypeface(m_Typeface);
        textHCD.setTypeface(m_Typeface);
        textHMD.setTypeface(m_Typeface);
        textApogeeHour.setTypeface(m_Typeface);
        textPerigeeHour.setTypeface(m_Typeface);
        textNoeudHour.setTypeface(m_Typeface);
        textFeuillesChg.setTypeface(m_Typeface);
        textFleursChg.setTypeface(m_Typeface);
        textRacinesChg.setTypeface(m_Typeface);
        textFruitsChg.setTypeface(m_Typeface);
        lmain.setBackgroundResource(R.drawable.ciel2);

        // Affichage ou non de l'heure de perigée suivant les parametres
        if (!prefs.getBoolean("perigeetime", false)) {
            try {
                //textPerigeeHour.setHeight(0);
                textPerigeeHour.setVisibility(View.GONE);
                textPerigee.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 25);
            } catch (Exception e) {
                Log.e("Lunoid", "exception", e);
            }

        }
        if (!prefs.getBoolean("apogeetime", false)) {
            try {
                //textApogeeHour.setHeight(0);
                textApogeeHour.setVisibility(View.GONE);
                textApogee.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 25);
            } catch (Exception e) {
                Log.e("Lunoid", "exception", e);
            }
        }
        if (!prefs.getBoolean("nodetime", false)) {
            try {
                //textNoeudHour.setHeight(0);
                textNoeudHour.setVisibility(View.GONE);
                textNoeud.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 25);
            } catch (Exception e) {
                Log.e("Lunoid", "exception", e);
            }
        }
        try {
            if (!prefs.getBoolean("displaycroidec", true)) {
                //textNoeudHour.setHeight(0);
                lcroidec.setVisibility(View.GONE);
            } else {
                //textNoeudHour.setHeight(0);
                lcroidec.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            Log.e("Lunoid", "exception", e);
        }

        if (screenSize > 3) {
            textCroissant.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 26.f);
            textDecroissant.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 26.f);
            textDescendant.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 26.f);
            textMontant.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 26.f);
        }
        if (screenSize == 3) {
            textCroissant.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24.f);
            textDecroissant.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24.f);
            textDescendant.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24.f);
            textMontant.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24.f);
        }
        if (screenSize == 2) {
            textCroissant.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20.f);
            textDecroissant.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20.f);
            textDescendant.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20.f);
            textMontant.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20.f);
        }
        if (screenSize == 1) {
            textCroissant.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16.f);
            textDecroissant.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16.f);
            textDescendant.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16.f);
            textMontant.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16.f);
        }
        if (screenSize >= 2) {
            ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) imageNote.getLayoutParams();
            marginParams.setMargins(0, 20, 0, 0);
        }


        /* read data from csv text file */
        read_data();

        calMin = Calendar.getInstance();
        calMin.set(Calendar.DAY_OF_MONTH, 1);
        calMin.set(Calendar.MONTH, 0);
        calMin.set(Calendar.YEAR, 2016);
        calMin.set(Calendar.HOUR_OF_DAY, 0);
        calMin.set(Calendar.MINUTE, 0);

        calMax = Calendar.getInstance();
        calMax.set(Calendar.DAY_OF_MONTH, 31);
        calMax.set(Calendar.MONTH, 11);
        // todo: update last date tous les ans
        calMax.set(Calendar.YEAR, 2025);
        calMax.set(Calendar.HOUR_OF_DAY, 23);
        calMax.set(Calendar.MINUTE, 59);

        //Calendar calendar = Calendar.getInstance();
        /*
        selday.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
        selday.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
        selday.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH));
        */

        selday.set(Calendar.HOUR, 0);
        selday.set(Calendar.MINUTE, 0);
        selday.set(Calendar.SECOND, 0);

        // ads
        /* show ad interstitial */
        /*
                if (mInterstitialAd.isLoaded())
                {
                    mInterstitialAd.show();
                } else
                {
                    Log.d("TAG", "The interstitial wasn't loaded yet.");
                }
        */
        textFruits.setOnClickListener(v -> nextJType(mapJour, "Fruits"));
        imageFruit.setOnClickListener(v -> nextJType(mapJour, "Fruits"));
        textFeuilles.setOnClickListener(v -> nextJType(mapJour, "Feuilles"));
        imageFeuille.setOnClickListener(v -> nextJType(mapJour, "Feuilles"));
        textRacines.setOnClickListener(v -> nextJType(mapJour, "Racines"));
        imageRacine.setOnClickListener(v -> nextJType(mapJour, "Racines"));
        textFleurs.setOnClickListener(v -> nextJType(mapJour, "Fleurs"));
        imageFleur.setOnClickListener(v -> nextJType(mapJour, "Fleurs"));
        textApogee.setOnClickListener(v -> nextJAPN(mapApogee));
        textPerigee.setOnClickListener(v -> nextJAPN(mapPerigee));
        textNoeud.setOnClickListener(v -> nextJAPN(mapNoeud));
        imgLune.setOnClickListener(v -> DisplayInfosDuMois(selday.get(Calendar.YEAR), selday.get(Calendar.MONTH)));
        try {
            textApogeeHour.setOnClickListener(v -> nextJAPN(mapApogee));
            textPerigeeHour.setOnClickListener(v -> nextJAPN(mapPerigee));
            textNoeudHour.setOnClickListener(v -> nextJAPN(mapNoeud));
        } catch (Exception e) {
            Log.e("Lunoid", "exception", e);
        }

        textCroissant.setOnClickListener(v -> nextJSens(mapCroissant, "1"));
        textDecroissant.setOnClickListener(v -> nextJSens(mapCroissant, "0"));
        textMontant.setOnClickListener(v -> nextJSens(mapMontant, "1"));
        textDescendant.setOnClickListener(v -> nextJSens(mapMontant, "0"));

        mGestureDetector = new GestureDetector(this, new LearnGestureListener());
    }  // onCreate

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.main);
        onDateChange(selday.get(Calendar.YEAR), selday.get(Calendar.MONTH), selday.get(Calendar.DAY_OF_MONTH));
        Log.d("On", "ConfigurationChanged");

        // Checks the orientation of the screen
       /* if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {

        }

       else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {

        }*/

    }

    public void onClickRiseSet(View v) {
        //  + " " + TimeZone.getDefault().getID()
        Toast InfosToast;
        if (Cityfound) {
            InfosToast = Toast.makeText(getApplicationContext(), AppPrefs.getString("ville", "Paris"), Toast.LENGTH_SHORT);
        } else {
            InfosToast = Toast.makeText(getApplicationContext(), "Paris", Toast.LENGTH_SHORT);
        }
        InfosToast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
        InfosToast.show();
    }

    public void onClickConstellation(View v) {
        String ConstellationName = "Unknown";
        int id = v.getId();
        if (id == R.id.imagePis) {
            ConstellationName = getResources().getString(R.string.Pisces);
        } else if (id == R.id.imageSco) {
            ConstellationName = getResources().getString(R.string.Scorpio);
        } else if (id == R.id.imageCan) {
            ConstellationName = getResources().getString(R.string.Cancer);
        } else if (id == R.id.imageSag) {
            ConstellationName = getResources().getString(R.string.Sagittarius);
        } else if (id == R.id.imageLeo) {
            ConstellationName = getResources().getString(R.string.Leo);
        } else if (id == R.id.imageAri) {
            ConstellationName = getResources().getString(R.string.Aries);
        } else if (id == R.id.imageCap) {
            ConstellationName = getResources().getString(R.string.Capricorn);
        } else if (id == R.id.imageVir) {
            ConstellationName = getResources().getString(R.string.Virgo);
        } else if (id == R.id.imageTau) {
            ConstellationName = getResources().getString(R.string.Taurus);
        } else if (id == R.id.imageAqu) {
            ConstellationName = getResources().getString(R.string.Aquarius);
        } else if (id == R.id.imageLib) {
            ConstellationName = getResources().getString(R.string.Libra);
        } else if (id == R.id.imageGem) {
            ConstellationName = getResources().getString(R.string.Gemini);
        }
        Toast InfosToast = Toast.makeText(getApplicationContext(), ConstellationName, Toast.LENGTH_SHORT);
        InfosToast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
        InfosToast.show();
    }

    class LearnGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapUp(MotionEvent ev) {
            //Log.d("onSingleTapUp",ev.toString());
            return true;
        }

        @Override
        public void onShowPress(MotionEvent ev) {
            //Log.d("onShowPress",ev.toString());
        }

        @Override
        public void onLongPress(MotionEvent ev) {
            //Log.d("onLongPress",ev.toString());
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            //Log.d("onScroll",e1.toString());
            return true;
        }

        @Override
        public boolean onDown(MotionEvent ev) {
            //Log.d("onDownd",ev.toString());
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Calendar c = Calendar.getInstance();
            boolean datechanged = false;
            try {
                Log.d("e1", e1.toString());
                Log.d("e2", e2.toString());
                float f1 = e1.getX();
                //Log.d("e1x",f1.toString());
                float f2 = e2.getX();
                //Log.d("e2x",f2.toString());
                float f3 = e1.getY();
                float f4 = e2.getY();

                // right to left : next day
                if (f1 > f2 + 60 && (f1 - f2) > abs(f3 - f4) && (calMax.getTime().getTime() - selday.getTime().getTime() > 86400000)) {
                    c.set(selday.get(Calendar.YEAR), selday.get(Calendar.MONTH), selday.get(Calendar.DAY_OF_MONTH));
                    c.add(Calendar.DAY_OF_MONTH, 1);
                    datechanged = true;

                    currentHelp = 0;
                    DisplayLocalHelp(0);
                }

                // left to right : previous day
                if (f2 > f1 + 60 && (f2 - f1) > abs(f3 - f4) && (selday.getTime().getTime() - calMin.getTime().getTime() > 86400000)) {
                    c.set(selday.get(Calendar.YEAR), selday.get(Calendar.MONTH), selday.get(Calendar.DAY_OF_MONTH));
                    c.add(Calendar.DAY_OF_MONTH, -1);
                    datechanged = true;
                    DisplayLocalHelp(1);
                }

                // bottom to top : display info
                if ((f3 > (f4 + 60)) && ((f3 - f4) > abs(f1 - f2))) {
                    DisplayInfosDuMois(selday.get(Calendar.YEAR), selday.get(Calendar.MONTH));
                    DisplayLocalHelp(3);
                }

                // top to bottom : restore current day
                if ((f4 > (f3 + 60)) && ((f4 - f3) > abs(f1 - f2))) {
                    Log.d("fling", "top to bottom");
                    if (isRunningTest()) {
                        c.set(Calendar.YEAR, 2020);
                        c.set(Calendar.MONTH, 0);
                        c.set(Calendar.DAY_OF_MONTH, 1);
                    }
                    datechanged = true;
                    DisplayLocalHelp(2);
                }
                if (datechanged) {
                    onDateChange(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    @Override
    public void onResume() {  // After a pause OR at startup
        super.onResume();

        // afficher la date du jour au (re)démarrage
        onDateChange(selday.get(Calendar.YEAR), selday.get(Calendar.MONTH), selday.get(Calendar.DAY_OF_MONTH));
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int resourceId = getResources().getIdentifier(prefs.getString("bg", "ciel0"), "drawable", getPackageName());
        if (resourceId != 0) {
            lmain.setBackgroundResource(resourceId);
        }

//todo : ads
//  Resume the AdManagerAdView.
//  adManagerAdView.resume();
    }

    @Override
    public void onPause() {
//todo: ads
// Pause the AdManagerAdView.
//        adManagerAdView.pause();

        super.onPause();
    }

    @Override
    public void onDestroy() {
//todo: ads
// Destroy the AdManagerAdView.
//  adManagerAdView.destroy();

        super.onDestroy();
    }

    // creation des menus
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        _menu = menu;
        ShowHideInfosMenu(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), _menu);
        return true;
    }

    // gestion des menus
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent;
        String AppVersion = "";

        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            AppVersion = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        int id = item.getItemId();
        if (id == R.id.item1) {
            intent = new Intent(this, About.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.item2) {
            intent = new Intent(this, Infos.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.item3) {
            intent = new Intent(this, PrefsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.item4) {
            intent = new Intent(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.paypal.com/donate/?hosted_button_id=3NNUT67K42CEA")));
            startActivity(intent);
            return true;
        } else if (id == R.id.item5) { // lien sur google play
            intent = new Intent(Intent.ACTION_VIEW);
            //intent.setData(Uri.parse("market://details?id=com.nialon"));
            intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.nialon"));
            intent.setPackage("com.android.vending");
            startActivity(intent);
            return true;
        } else if (id == R.id.item6) { // partager
            intent = new Intent(Intent.ACTION_SEND);
            intent.setType("message/rfc822");
            intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));

            intent.putExtra(Intent.EXTRA_TEXT,
                    Html.fromHtml("<html><head></head><body>" +
                            "<p>" + getResources().getString(R.string.sharemessage) + "</p>" +
                            "<p>https://play.google.com/store/apps/details?id=com.nialon</p>" +
                            "<p>https://www.facebook.com/LunoidApp</p>" +
                            "</body></html>")
            );
            startActivity(Intent.createChooser(intent, getResources().getString(R.string.app_name)));
            return true;
        } else if (id == R.id.item7) { // contacter le développeur
            @SuppressLint("HardwareIds") String android_id = Settings.Secure.getString(this.getContentResolver(), ANDROID_ID);

            intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/html");
            intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"mnialon@gmail.com"});
            intent.putExtra(Intent.EXTRA_TEXT,
                    Html.fromHtml("<html><head></head><body>" +
                            "---<br />" +
                            "Version Application : " + AppVersion + "<br />" +
                            "Android Version : " + Build.VERSION.SDK_INT + " (" + Build.VERSION.RELEASE + ")<br />" +
                            "Device Type : " + Build.MANUFACTURER + " (" + Build.MODEL + ")<br />" +
                            "Density : " + getResources().getDisplayMetrics().densityDpi + "<br />" +
                            "Height : " + getResources().getDisplayMetrics().heightPixels + "<br />" +
                            "Width : " + getResources().getDisplayMetrics().widthPixels + "<br />" +
                            "City : " + AppPrefs.getString("city", "Paris") + "<br />" +
                            "Timezone : " + TimeZone.getDefault().getID() + "<br />" +
                            "id : " + android_id + "<br />" +
                            "DST : " + TimeZone.getDefault().getDSTSavings() + "<br />" +
                            "---<br />" +
                            "</body></html>")
            );
            startActivity(Intent.createChooser(intent, getResources().getString(R.string.app_name)));
            return true;
        } else if (id == R.id.item8) {
            DisplayInfosDuMois(selday.get(Calendar.YEAR), selday.get(Calendar.MONTH));
            return true;
        } else if (id == R.id.item9) {
            DisplayAllNotes();
            return true;
/*
            case R.id.item10:
                Log.w("menu", "item10");
                ManageReview();
                return true;
 */
        } else if (id == R.id.item10) {
            SharedPreferences sp = getSharedPreferences("myNotes", MODE_PRIVATE);
            SimpleDateFormat sdfx = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/html");
            //intent.putExtra(Intent.EXTRA_MIME_TYPES, "text/html");
            intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name) + " - " + getResources().getString(R.string.listNotes));
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
            StringBuilder htmlNotes = new StringBuilder("<html><head></head><body>");
            for (LinkedHashMap.Entry<String, ?> entry : sortedMap.entrySet()) {
                String dNote = entry.getKey();
                String tNote = entry.getValue().toString();
                htmlNotes.append("   ").append(dNote).append("    ").append(tNote).append("<br/>");

            }
            htmlNotes.append("</body></html>");
            intent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(htmlNotes.toString()));
            intent.putExtra(Intent.EXTRA_HTML_TEXT, Html.fromHtml(htmlNotes.toString()));

            startActivity(Intent.createChooser(intent, getResources().getString(R.string.app_name) + " - " + getResources().getString(R.string.listNotes)));
            return true;
        } else if (id == R.id.item11) {
            DisplayMensuel(selday.get(Calendar.YEAR), selday.get(Calendar.MONTH));
            return true;
        }

        return false;
    }

    private void nextJType(Map<String, String> m, String C) {
        String ds;
        Date td;

        Log.d("nextJType", "Start");
        DisplayLocalHelp(5);

        Calendar c = Calendar.getInstance();
        c.set(selday.get(Calendar.YEAR), selday.get(Calendar.MONTH), selday.get(Calendar.DAY_OF_MONTH));

        do {
            c.add(Calendar.DATE, 1);
            ds = sdf.format(c.getTime());
            td = c.getTime();
        }
        while ((td.compareTo(calMax.getTime()) <= 0) && (!m.get(ds).contains(C)));

        if (td.compareTo(calMax.getTime()) <= 0) {
            Log.d("nextJType", "found");

            onDateChange(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        } else {
            Log.d("nextJType", "not found");
            Toast.makeText(getApplicationContext(), R.string.notfound, Toast.LENGTH_SHORT).show();
        }
    }

    private void nextJSens(Map<String, String> m, String sens) {
        String ds;
        Date td;

        DisplayLocalHelp(5);
        Log.d("nextjSens", "Start");

        Calendar c = Calendar.getInstance();
        c.set(selday.get(Calendar.YEAR), selday.get(Calendar.MONTH), selday.get(Calendar.DAY_OF_MONTH));

        do {
            c.add(Calendar.DATE, 1);
            ds = sdf.format(c.getTime());
            td = c.getTime();
        }
        while ((td.compareTo(calMax.getTime()) <= 0) && !(m.get(ds).startsWith("2") || m.get(ds).startsWith("3") || (m.get(ds).equals(sens))));

        if (td.compareTo(calMax.getTime()) <= 0) {
            Log.d("nextJSens", "found");
            onDateChange(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        } else {
            Log.d("nextJSens", "Not found");
            Toast.makeText(getApplicationContext(), R.string.notfound, Toast.LENGTH_SHORT).show();
        }
    }

    private void nextJAPN(Map<String, String> m) {
        String ds;
        Date td;

        DisplayLocalHelp(5);
        Log.d("nextjAPN", "Start");

        Calendar c = Calendar.getInstance();
        c.set(selday.get(Calendar.YEAR), selday.get(Calendar.MONTH), selday.get(Calendar.DAY_OF_MONTH));

        do {
            c.add(Calendar.DATE, 1);
            ds = sdf.format(c.getTime());
            td = c.getTime();
        }
        while ((td.compareTo(calMax.getTime()) <= 0) && (!(m.get(ds).length() >= 5)));

        if (td.compareTo(calMax.getTime()) <= 0) {
            Log.d("NextJAPN", "Found :");
            selday.set(Calendar.YEAR, c.get(Calendar.YEAR));
            selday.set(Calendar.MONTH, c.get(Calendar.MONTH));
            selday.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH));
            onDateChange(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        } else {
            Log.d("NextJAPN", "Not found ");
            Toast.makeText(getApplicationContext(), R.string.notfound, Toast.LENGTH_SHORT).show();
        }
    }

    private static String heurelocale(String s, Calendar d, boolean lh) {
        if (s == null) {
            return "--:--";
        } else {
            if (s.equals("--:--")) {
                return s;
            } else {
                if (lh) {
//                    int nboffset;
                    int nboffsetCal;
                    Calendar cal1;
                    cal1 = (Calendar) d.clone();
                    cal1.set(Calendar.HOUR_OF_DAY, Integer.parseInt(s.substring(0, 2)));
                    cal1.set(Calendar.MINUTE, Integer.parseInt(s.substring(3, 5)));

//                    d.setHours(Integer.parseInt(s.substring(0, 2)));
//                    d.setMinutes(Integer.parseInt(s.substring(3, 5)));
//                    nboffset = TimeZone.getDefault().getOffset(d.getTime()) / 1000 / 3600;

                    nboffsetCal = TimeZone.getDefault().getOffset(cal1.getTime().getTime()) / 1000 / 3600;
//                    nboffset = TimeZone.getTimeZone("Europe/Paris").getOffset(d.getTime())/1000/3600;
                    // todo: time offset test
                    //nboffsetCal = TimeZone.getTimeZone("Europe/Paris").getOffset(cal1.getTime().getTime())/1000/3600;
                    //Log.d("d",d.toString());
//                    Log.d("nb",String.valueOf(nboffset));
                    Log.d("nbcal", String.valueOf(nboffsetCal));
                    String s1;
                    String s2;

                    Calendar cal = Calendar.getInstance();
                    cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(s.substring(0, 2)));
                    cal.set(Calendar.MINUTE, Integer.parseInt(s.substring(3, 5)));

                    cal.add(Calendar.HOUR_OF_DAY, nboffsetCal);
                    s1 = String.valueOf(cal.get(Calendar.HOUR_OF_DAY));
                    s1 = (s1.length() == 1 ? "0" + s1 : s1);
                    s2 = String.valueOf(cal.get(Calendar.MINUTE));
                    s2 = (s2.length() == 1 ? "0" + s2 : s2);

                    return s1 + ":" + s2;
                } else return s.substring(0, 5);
            }

        }

    }
/*
    private void ManageReview()
    {
        Log.e("request ManageReview", "request");
        Log.e("request reviewinfo", reviewInfo.toString());
        Task<Void> flow = reviewmanager.launchReviewFlow(Lunoid.this, reviewInfo);

        Log.e("request ManageReview", "request2");
        flow.addOnCompleteListener(task -> {

        });
    }

 */

    private void DisplayAllNotes() {

        Intent intent;

        try {
            intent = new Intent(this, DisplayNotes.class);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void DisplayMensuel(int year, int month) {

        Intent intent;

        try {
            intent = new Intent(this, DisplayMensuel.class);
            intent.putExtra("month", month);
            intent.putExtra("year", year);
            startActivityForResult(intent, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == Activity.RESULT_OK) {
                // TODO Extract the data returned from the child Activity.
                int y = data.getIntExtra("y", 0);
                int m = data.getIntExtra("m", 0);
                int d = data.getIntExtra("d", 0);
                Log.d("y", String.valueOf(y));
                Log.d("m", String.valueOf(m));
                Log.d("d", String.valueOf(d));
                onDateChange(selday.get(Calendar.YEAR), selday.get(Calendar.MONTH), d);
            }
        }
    }

    private void DisplayInfosDuMois(int year, int month) {
        Intent intent;
        intent = new Intent(this, ConseilsDuMois.class);
        String message = "";
        String type_message;

        Log.d("DisplayInfosDuMois", (month + 1) + "/" + year);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //SharedPreferences prefs = getSharedPreferences("com.nialon", MODE_PRIVATE);

        linetot.setLength(0);
        if ((year >= 2016) && (year <= 2018)) {
            type_message = "1";
            if ((month == 2)) {
                message = "mars"  + year;
            } else if ((month == 3)) {
                message = "avril"  + year;
            } else if ((month == 4)) {
                message = "mai"  + year;
            } else if ((month == 5)) {
                message = "juin"  + year;
            } else if ((month == 6)) {
                message = "juillet"  + year;
            } else if ((month == 7)) {
                message = "aout"  + year;
            } else {
                message = "Pas d'informations pour cette période. Les conseils de plantation ne sont disponibles que pour les mois de Mars à Août.";
                type_message = "0";
            }
            if (type_message.equals("1")) {
                Log.d("message", message);
                intent.putExtra(EXTRA_MESSAGE, message);
                intent.putExtra("type_message", type_message);
                startActivity(intent);
            }
        } else if (year >= 2019) {
            String m1;

            if (month >= 0 && month <= 11) {
                m1 = tableauMois[month];
                sifm = prefs.getBoolean("infosmois", false);
                if (sifm) {
                    BuildConseilMois(month, year);
                } else {
                    BuildConseilJour(dateString, m1, dateString2);
                }

                type_message = "2";
                intent.putExtra("type_message", type_message);
                intent.putExtra("htmltxt", htmltxt);

                startActivity(intent);
            } else {
                message = "Pas d'informations pour cette période. Les conseils de plantation ne sont disponibles que pour les mois de Mars à Septembre.";
                type_message = "0";
            }
        } else {
            message = "Pas d'informations pour cette période. Les conseils de plantation ne sont disponibles qu'à partir de 2016";
            type_message = "0";
        }

        if (type_message.equals("0")) {
            Toast InfosToast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
            InfosToast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
            InfosToast.show();
        }
    }

    private void BuildConseilMois(int month, int year) {
        Calendar cal = new GregorianCalendar(year, month, 1);
        int nbdays = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        Date d1 = new Date();
        String m;
        String ds;
        String ds2;

        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.YEAR, year);

        m = tableauMois[month];

        for (int d = 1; d <= nbdays; d++) {
            cal.set(Calendar.DAY_OF_MONTH, d);
            d1.setTime(cal.getTimeInMillis());
            ds = sdf.format(d1);
            ds2 = sdf2.format(d1);
            BuildConseilJour(ds, m, ds2);
            //Log.d("d", d.toString());
        }
    }

    private void BuildConseilJour(String ds, String m1, String ds2) {
        String hs12 = "";
        String ht12;
        String s1 = "";
        String s2 = "";
        String t1;
        String t2;

        try {
            addText("<font color = #ffff00 face=\"sans-serif-light\">" + ds2 + "</font>");

            // Log.d("BuildConseilJour", ds);
            if (mapMontant.get(ds).startsWith("0")) {
                s1 = "descendante";
                s2 = "";
            }
            if (mapMontant.get(ds).startsWith("1")) {
                s1 = "montante";
                s2 = "";
            }
            if (mapMontant.get(ds).startsWith("2")) {
                s1 = "montante";
                s2 = "descendante";
                hs12 = heurelocale(mapMontant.get(ds).substring(2, 7), selday, lh);
            }
            if (mapMontant.get(ds).startsWith("3")) {
                s1 = "descendante";
                s2 = "montante";
                hs12 = heurelocale(mapMontant.get(ds).substring(2, 7), selday, lh);
            }
            if (mapJour.get(ds).length() >= 15 && (mapJour.get(ds)).toLowerCase().startsWith("feuilles/fruits")) {
                t1 = "feuilles";
                t2 = "fruits";
                ht12 = heurelocale(mapJour.get(ds).substring(16, 21), selday, lh);
            } else if (mapJour.get(ds).length() >= 14 && (mapJour.get(ds)).toLowerCase().startsWith("fruits/racines")) {
                t1 = "fruits";
                t2 = "racines";
                ht12 = heurelocale(mapJour.get(ds).substring(15, 20), selday, lh);
            } else if (mapJour.get(ds).length() >= 14 && (mapJour.get(ds)).toLowerCase().startsWith("racines/fleurs")) {
                t1 = "racines";
                t2 = "fleurs";
                ht12 = heurelocale(mapJour.get(ds).substring(15, 20), selday, lh);
            } else if (mapJour.get(ds).length() >= 15 && (mapJour.get(ds)).toLowerCase().startsWith("fleurs/feuilles")) {
                t1 = "fleurs";
                t2 = "feuilles";
                ht12 = heurelocale(mapJour.get(ds).substring(16, 21), selday, lh);
            } else {
                t1 = mapJour.get(ds).toLowerCase();
                t2 = "";
                ht12 = "";
            }
            BuildHTMLJour(s1, s2, t1, t2, m1, ht12, hs12);

            testApogee(selday, ds);
            testPerigee(selday, ds);
            testNoeud(selday, ds);
        } catch (Exception e) {
            Log.e("Lunoid", "exception", e);
        }
        addText("************");
        htmltxt = linetot.toString();
    }

    private void testApogee(Calendar d1, String ds) {
        if (!mapApogee.get(ds).equals("0")) {
            String apogeelocal = heurelocale(mapApogee.get(ds), d1, lh);

            addText("<font color=#ff0000>" + getString(R.string.apogeea) + " " + apogeelocal);
            int h1 = Integer.parseInt(apogeelocal.substring(0, 2));
            if (apogeelocal.compareTo("05:00") < 0) {
                h1 += 5;
                addText(" -> " + getString(R.string.nepasjardineravant) + " " + String.format(Locale.getDefault(), "%02d:%2s", h1, apogeelocal.substring(3, 5)) + "</font>");
            } else if ((apogeelocal.compareTo("19:00") < 0)) {
                h1 -= 5;
                addText(" -> " + getString(R.string.nepasjardinerentre) + " " + String.format(Locale.getDefault(), "%02d:%2s", h1, apogeelocal.substring(3, 5)) + " - " + String.format(Locale.getDefault(), "%02d:%2s", h1 + 10, apogeelocal.substring(3, 5)) + "</font>");
            } else if ((apogeelocal.compareTo("19:00") >= 0)) {
                h1 -= 5;
                addText(" -> " + getString(R.string.nepasjardinerapres) + " " + String.format(Locale.getDefault(), "%02d:%2s", h1, apogeelocal.substring(3, 5)) + "</font>");
            }
        }
    }

    private void testPerigee(Calendar d1, String ds) {
        if (!mapPerigee.get(ds).equals("0")) {
            String perigeelocal = heurelocale(mapPerigee.get(ds), d1, lh);
            addText("<font color=#ff0000>" + getString(R.string.perigeea) + " " + perigeelocal);
            int h1 = Integer.parseInt(perigeelocal.substring(0, 2));
            if (perigeelocal.compareTo("05:00") < 0) {
                h1 += 5;
                addText(" -> " + getString(R.string.nepasjardineravant) + " " + String.format(Locale.getDefault(), "%02d:%2s", h1, perigeelocal.substring(3, 5)) + "</font>");
            } else if ((perigeelocal.compareTo("19:00") < 0)) {
                h1 -= 5;
                addText(" -> " + getString(R.string.nepasjardinerentre) + " " + String.format(Locale.getDefault(), "%02d:%2s", h1, perigeelocal.substring(3, 5)) + " - " + String.format(Locale.getDefault(), "%02d:%2s", h1 + 10, perigeelocal.substring(3, 5)) + "</font>");
            } else if ((perigeelocal.compareTo("19:00") >= 0)) {
                h1 -= 5;
                addText(" -> " + getString(R.string.nepasjardinerapres) + " " + String.format(Locale.getDefault(), "%02d:%2s", h1, perigeelocal.substring(3, 5)) + "</font>");
            }
        }
    }

    private void testNoeud(Calendar d1, String ds) {
        try {
            Log.d("testnoeud", ds);
            if (!mapNoeud.get(ds).equals("0")) {
                String noeudlocal = heurelocale(mapNoeud.get(ds), d1, lh);
                Log.d("noeudlocal", noeudlocal);
                if (mapNoeud.get(ds).startsWith("+", 5)) {
                    addText("<font color=#ff0000>" + getString(R.string.noeudascendanta) + " " + noeudlocal);
                } else {
                    addText("<font color=#ff0000>" + getString(R.string.noeuddescendanta) + " " + noeudlocal);

                }
                int h1 = Integer.parseInt(noeudlocal.substring(0, 2));
                if (noeudlocal.compareTo("05:00") < 0) {
                    h1 += 5;
                    addText(" -> " + getString(R.string.nepasjardineravant) + " " + String.format(Locale.getDefault(),"%02d:%2s", h1, noeudlocal.substring(3,5)) + "</font>");
                } else if ((noeudlocal.compareTo("19:00") < 0)) {
                    h1 -= 5;
                    addText(" -> " + getString(R.string.nepasjardinerentre) + " " + String.format(Locale.getDefault(),"%02d:%2s", h1, noeudlocal.substring(3,5)) + " - " + String.format(Locale.getDefault(),"%02d:%2s", h1+10, noeudlocal.substring(3,5)) + "</font>");
                } else if ((noeudlocal.compareTo("19:00") >= 0)) {
                    h1 -= 5;
                    addText(" -> " + getString(R.string.nepasjardinerapres) + " " + String.format(Locale.getDefault(),"%02d:%2s", h1, noeudlocal.substring(3,5)) + "</font>");
                }
            }
        } catch (Exception e) {
            Log.e("testNoeud", e.toString());
        }
    }

    private void BuildHTMLJour(String s1, String s2, String t1, String t2, String m1, String ht12, String hs12) {
        //Log.d("BuildHTMLJour", s1);
        String lg;
        String lgi;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        lgi = prefs.getString("lnginfos", "1");
        if (lgi.equals("2"))
            lg = "en";
        else
            lg = "fr";
        //Log.d("d",  "<font color = #ffff00 face=\"sans-serif-light\">Date:" + jour + "</font>");
        if (!s1.equals("") && s2.equals("")) {
            if (!t1.equals("") && t2.equals("")) {
                addTextFromFile(m1 + "_" + s1 + "_" + t1 + "_" + lg + ".txt");
            }
            if (!t1.equals("") && !t2.equals("")) {
                addTextFromFile(m1 + "_" + s1 + "_" + t1 + "_" + lg + ".txt");
                addText("<font color=#ffff00>" + ht12 + "</font>");
                addTextFromFile(m1 + "_" + s1 + "_" + t2 + "_" + lg + ".txt");
            }
        }
        if (!s1.equals("") && !s2.equals("")) {
            if (!t1.equals("") && t2.equals("")) {
                addTextFromFile(m1 + "_" + s1 + "_" + t1 + "_" + lg + ".txt");
                addText("<font color=#ffff00>" +  hs12 + "</font>" );
                addTextFromFile(m1 + "_" + s2 + "_" + t1 + "_" + lg + ".txt");
            }
            if (!t1.equals("") && !t2.equals("")) {
                if (hs12.compareTo(ht12) < 0) {
                    addTextFromFile(m1 + "_" + s1 + "_" + t1 + "_" + lg + ".txt");
                    addText("<font color=#ffff00>" +  hs12 + "</font>" );
                    addTextFromFile(m1 + "_" + s2 + "_" + t1 + "_" + lg + ".txt");
                    System.out.println(m1 + "_" + s2 + "_" + t1 + "_" + lg + ".txt");
                    addText("<font color=#ffff00>" +  ht12  + "</font>");
                    addTextFromFile(m1 + "_" + s2 + "_" + t2 + "_" + lg + ".txt");
                }
                if (hs12.compareTo(ht12) > 0) {
                    addTextFromFile(m1 + "_" + s1 + "_" + t1 + "_" + lg + ".txt");
                    addText( "<font color=#ffff00>" +  ht12 + "</font>");
                    addTextFromFile(m1 + "_" + s1 + "_" + t2 + "_" + lg + ".txt");
                    addText("<font color=#ffff00>" +  hs12 + "</font>" );
                    addTextFromFile(m1 + "_" + s2 + "_" + t2 + "_" + lg + ".txt");
                }
                if (hs12.compareTo(ht12) == 0) {
                    addTextFromFile(m1 + "_" + s1 + "_" + t1 + "_" + lg + ".txt");
                    addText( "<font color=#ffff00>" +  hs12 + "</font>");
                    addTextFromFile(m1 + "_" + s2 + "_" + t2 + "_" + lg + ".txt");
                }
            }
        }
    }

    private void addTextFromFile(String s) {
        //Log.d("addTextFromFile", s);

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(getAssets().open(s)));

            String st;

            while ((st = br.readLine()) != null) {
                //System.out.println(st);
                linetot.append("\n");
                linetot.append(st);
            }
        } catch (Exception e) {
            System.out.println("Something went wrong.");
        }
    }

    private  void addText(String s) {
        //Log.d("addText ", s);
        linetot.append("<br/>");
        linetot.append(s);
    }

    private void read_data() {
        Date d;

        Log.d("read_data", "begin");
        Resources myRes = getResources();
        InputStream lundata = myRes.openRawResource(R.raw.datalune);

        InputStreamReader inputreader = new InputStreamReader(lundata);
        BufferedReader buffreader = new BufferedReader(inputreader);
        String line;
        String[] separated;
        try {
            while (( line = buffreader.readLine()) != null) {
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
                    //Log.d("jour", temp);
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
                mapComment.put(separated[0], separated[8]);
                mapMontant.put(separated[0], separated9.toString());
                mapCroissant.put(separated[0], separated[10]);
                mapSigne.put(separated[0], separated[11]);
                mapConseil.put(separated[0], separated[12]);
                mapEclair.put(separated[0], separated[13]);
            }
        } catch (Exception e) {
            Log.e("Lunoid", "exception", e);
        }
    }

    public static String getLever() {
        return heurelocale(mapLever.get(dateString), selday, lh);
    }

    public static String getCoucher() {
        return heurelocale(mapCoucher.get(dateString), selday, lh);
    }

    public static Date getSunRise() {
        return sunRise;
    }

    public static Date getSunSet() {
        return sunSet;
    }

    public void datePicker(View view) {
        // affichage du calendrier sur click de la date en clair
        DisplayLocalHelp(6);
        DatePickerFragment fragment = new DatePickerFragment();
        fragment.show(getSupportFragmentManager(), "date");
    }

    public void editNote(View view) {
        DisplayLocalHelp(4);
        SharedPreferences sp = getSharedPreferences("myNotes", MODE_PRIVATE);
        //Map<String, ?> allEntries = sp.getAll();

        String str1 = sp.getString(dateString, "");
        SharedPreferences spNotif = getSharedPreferences("myNotif", MODE_PRIVATE);
        final String str1Notif = spNotif.getString(dateString, "");
        Log.d("editNote", "start");
        try {
            View root = RelativeLayout.inflate(this, R.layout.editnote, null);
            final EditText e1 = root.findViewById(R.id.editText);
            final Switch sw1 = root.findViewById(R.id.switch1);
            e1.setText(str1);
            Log.d("str1notif", str1Notif);

            sw1.setChecked(!str1Notif.equals("0") && !str1Notif.equals(""));
            sw1.setEnabled(today.getTime().before(selday.getTime()));

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.notedu) + " " + dateString + " :");
            builder.setView(root);
            builder.setPositiveButton(R.string.sauvegarder, (dialog, which) -> {
                SharedPreferences sp1 = getSharedPreferences("myNotes", MODE_PRIVATE);
                UUID uuid;
                SharedPreferences.Editor sedt = sp1.edit();
                sedt.putString(dateString, e1.getText().toString());
                sedt.apply();
                if (!e1.getText().toString().equals("")) {
                    imageNote.setAlpha(1.0f);
                    imageNote.setColorFilter(Color.YELLOW);
                } else {
                    imageNote.setAlpha(0.5f);
                    imageNote.clearColorFilter();
                }

                SharedPreferences spNotif1 = getSharedPreferences("myNotif", MODE_PRIVATE);
                SharedPreferences.Editor sedtNotif = spNotif1.edit();
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

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        // date sélectionnée dans le calendrier
        Log.d("onDateSet", "ondateset " + year + " " + (month + 1) + " " + day);

        onDateChange(year, month, day);
    }

    private Menu getMenu() {
        //use it like this
        return _menu;
    }

    private void ShowHideInfosMenu(int y, int mo, Menu me) {
        MenuItem item;
        try {
            item = me.findItem(R.id.item8);

            if (y >= 2019 && (mo >= 0 && mo <= 11)) {
                //item.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
                item.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);
            } else {
                item.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_NEVER);
            }
            item = me.findItem(R.id.item2);
            item.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);
            item = me.findItem(R.id.item11);
            item.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);
            //item.setShowAsAction(MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        } catch (Exception e) {
            Log.e("Lunoid", "exception", e);
        }
    }

    public UUID scheduleNotification(String txtNotif) {
        long currentTime;
        Calendar cal1 = Calendar.getInstance();
        currentTime = cal1.getTimeInMillis();
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

    public static synchronized boolean isRunningTest() {
        if (null == isRunningTest) {
            boolean istest;

            try {
                Class.forName("androidx.test.espresso.Espresso");
                istest = true;
                Log.d("isTest", "true");
            } catch (ClassNotFoundException e) {
                istest = false;
                Log.d("isTest", "false");
            }

            isRunningTest = new AtomicBoolean(istest);
        }

        return isRunningTest.get();
    }

    final private SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            try {
                Log.d("changed : ", key);
                if (key.equals("nodetime")) {
                    if (!sharedPreferences.getBoolean("nodetime", false)) {
                        textNoeudHour.setVisibility(View.GONE);
                        textNoeud.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 25);
                    } else {
                        textNoeudHour.setVisibility(View.VISIBLE);
                        textNoeud.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
                    }
                }
                if (key.equals("apogeetime")) {
                    if (!sharedPreferences.getBoolean("apogeetime", false)) {
                        textApogeeHour.setVisibility(View.GONE);
                        textApogee.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 25);
                    } else {
                        textApogeeHour.setVisibility(View.VISIBLE);
                        textApogee.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
                    }
                }
                if (key.equals("perigeetime")) {
                    if (!sharedPreferences.getBoolean("perigeetime", false)) {
                        textPerigeeHour.setVisibility(View.GONE);
                        textPerigee.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 25);
                    } else {
                        textPerigeeHour.setVisibility(View.VISIBLE);
                        textPerigee.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
                    }
                }
                if (key.equals("displaycroidec")) {
                    if (!sharedPreferences.getBoolean("displaycroidec", true)) {
                        lcroidec.setVisibility(View.GONE);
                    } else {
                        lcroidec.setVisibility(View.VISIBLE);
                    }
                }
                if (key.equals("bg")) {
                    int resourceId = getResources().getIdentifier(sharedPreferences.getString("bg", "ciel0"), "drawable", getPackageName());
                    if (resourceId != 0) {
                        lmain.setBackgroundResource(resourceId);
                    }
                }
            } catch (Exception e) {
                Log.e("Lunoid", "exception", e);
            }
        }
    };

//todo: ads
//    private AdSize getAdSize() {
//        Display display = getWindowManager().getDefaultDisplay();
//        DisplayMetrics outMetrics = new DisplayMetrics();
//        display.getMetrics(outMetrics);
//        float widthPixels = outMetrics.widthPixels;
//        float density = outMetrics.density;
//        int adWidth = (int) (widthPixels / density);
//        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth);
//    }

    private void DisplayLocalHelp(int hitem) {
        if (!AppPrefs.getBoolean("neplusaff", false)) {
            View lhelp = RelativeLayout.inflate(getApplicationContext(), R.layout.localhelp, null);
            final TextView tvhelp = lhelp.findViewById(R.id.txtLocalHelp);
            final TextView tvhelpNr = lhelp.findViewById(R.id.txtLocalHelpNr);
            final CheckBox cbhelp = lhelp.findViewById(R.id.checkNoHelp);

            String[] arHints = getResources().getStringArray(R.array.hints);
            if (hitem != 999) {
                currentHelp = hitem;
            }
            tvhelp.setText(arHints[currentHelp]);
            tvhelpNr.setText("( " + (currentHelp + 1) + "/" + arHints.length + " )");
            AlertDialog.Builder builder = new AlertDialog.Builder(Lunoid.this);

            builder.setView(lhelp);
            builder.setPositiveButton(R.string.next, (dialog, which) -> nextHelp());
            builder.setNegativeButton(R.string.prev, (dialog, which) -> prevHelp());
            builder.setNeutralButton(R.string.finish, (dialog, which) -> {
                if (cbhelp.isChecked()) {
                    Log.d("cbhelp", "checked");
                    // désactiver l'affichage de l'aide dans la config
                    SharedPreferences.Editor edPrefs = AppPrefs.edit();
                    edPrefs.putBoolean("neplusaff", true);
                    edPrefs.apply();
                } else {
                    Log.d("cbhelp", " not checked");
                }
            });
            builder.show();
        }
    }

    public void prevHelp() {
        Log.d("Help", "prev");

        if (currentHelp > 0) {
            currentHelp--;
        } else {
            currentHelp = getResources().getStringArray(R.array.hints).length - 1;
        }
        DisplayLocalHelp(999);
    }

    public void nextHelp() {
        Log.d("Help", "next");
        if (currentHelp < getResources().getStringArray(R.array.hints).length - 1) {
            currentHelp++;
        } else {
            currentHelp = 0;
        }
        DisplayLocalHelp(999);
    }
} // Lunoid