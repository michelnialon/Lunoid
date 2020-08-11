package com.nialon;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import androidx.fragment.app.FragmentActivity;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import static java.lang.Math.abs;

// TODO : signes du zodiaque
// todo : partager : fait
// todo : envoyer un commentaire : fait
// todo : évaluer l'application : fait
// todo : vérifier nouvelle version : fait
// todo : prendre des notes : fait

public class Lunoid extends FragmentActivity implements DatePickerDialog.OnDateSetListener {
    /**
     * Called when the activity is first created.
     */
    private SimpleDateFormat sdf;
    private SimpleDateFormat sdf2;
    static String dateString;  // 16/05/2020
    String dateString2;        // Saturday 16 May 2020
    private Calendar today = Calendar.getInstance();
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
    private int ScreenSize;

    // ads
    //private InterstitialAd mInterstitialAd;

    static Map<String, String> mapLever = new HashMap<>();
    static Map<String, String> mapCoucher = new HashMap<>();
    Map<String, String> mapPhase = new HashMap<>();
    Map<String, String> mapJour = new HashMap<>();
    Map<String, String> mapApogee = new HashMap<>();
    Map<String, String> mapPerigee = new HashMap<>();
    Map<String, String> mapNoeud = new HashMap<>();
    Map<String, String> mapComment = new HashMap<>();
    Map<String, String> mapCroissant = new HashMap<>();
    Map<String, String> mapMontant = new HashMap<>();
    Map<String, String> mapSigne= new HashMap<>();
    Map<String, String> mapConseil= new HashMap<>();
    Map<String, String> mapEclair= new HashMap<>();

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

    public void onDateChange(int year, int monthOfYear, int dayOfMonth)
    {
        int resId;

        try {
            Log.d("Lunoid", "onDateChange");

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
                textCoucher.setText(heurelocale(mapCoucher.get(dateString), selday, lh));
                textLever.setText(heurelocale(mapLever.get(dateString), selday, lh));

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
                        if (dateString2.equals("mercredi 31 janvier 2018")) {
                            Log.d("d", "lune bleue");
                            resId = getResources().getIdentifier("nlune100bleue", "drawable", getPackageName());
                            imgLune.setImageResource(resId);
                        }

                        if (textPct != null) {
                            textPct.setText(ecl.concat(" %"));
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

                // Apogee/Perigee/Noeud
                textApogee.setTextColor(!mapApogee.get(dateString).equals("0") ? Color.RED : Color.rgb(120, 120, 120));
                textPerigee.setTextColor(!mapPerigee.get(dateString).equals("0") ? Color.RED : Color.rgb(120, 120, 120));
                textNoeud.setTextColor(!mapNoeud.get(dateString).equals("0") ? Color.RED : Color.rgb(120, 120, 120));

                try {
                    textApogeeHour.setTextColor(!mapApogee.get(dateString).equals("0") ? Color.RED : Color.rgb(120, 120, 120));
                    textApogeeHour.setText(!mapApogee.get(dateString).equals("0") ? (mapApogee.get(dateString).equals("88:88") ? "--:--" : heurelocale(mapApogee.get(dateString), selday, lh)) : "--:--");

                    textPerigeeHour.setTextColor(!mapPerigee.get(dateString).equals("0") ? Color.RED : Color.rgb(120, 120, 120));
                    textPerigeeHour.setText(!mapPerigee.get(dateString).equals("0") ? (mapPerigee.get(dateString).equals("88:88") ? "--:--" : heurelocale(mapPerigee.get(dateString), selday, lh)) : "--:--");

                    textNoeudHour.setTextColor(!mapNoeud.get(dateString).equals("0") ? Color.RED : Color.rgb(120, 120, 120));
                    textNoeudHour.setText(!mapNoeud.get(dateString).equals("0") ? (mapNoeud.get(dateString).equals("88:88") ? "--:--" : heurelocale(mapNoeud.get(dateString).substring(0, 5), selday, lh)) : "--:--");
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
                        InfosStr += "\nPleine Lune";
                    }
                    if (mapEclair.get(dateString).equals("0")) {
                        InfosStr += "\nNouvelle Lune";
                    }
                } else {
                    if (mapEclair.get(dateString).equals("100")) {
                        InfosStr = "Pleine Lune";
                    }
                    if (mapEclair.get(dateString).equals("0")) {
                        InfosStr = "Nouvelle Lune";
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
            }
        }
        catch (Exception e)
        {
            Log.e("Lunoid", "exception", e);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("OnCreate", "");

        selday.set(Calendar.HOUR, 0);
        selday.set(Calendar.MINUTE, 0);
        selday.set(Calendar.SECOND, 0);

        /*
         * pour changer le format de date
         */
        /*
        Locale locale = new Locale("fr");
        Locale.setDefault(locale);
        Configuration config = getBaseContext().getResources().getConfiguration();
        config.locale = locale;
        */
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
        PublisherAdView mPublisherAdView = findViewById(R.id.publisherAdView);
        PublisherAdRequest adRequest = new PublisherAdRequest.Builder().build();
        mPublisherAdView.setAdSizes(AdSize.BANNER);
        //mPublisherAdView.setAdUnitId("ca-app-pub-4468029712209847/4219671648"); // prod ( à mettre dans le layout main )
        //mPublisherAdView.setAdUnitId("ca-app-pub-3940256099942544/6300978111"); // test
        mPublisherAdView.loadAd(adRequest);

        //final Context context = getApplicationContext();
        //SharedPreferences prefs = this.getSharedPreferences("com.nialon",Context.MODE_PRIVATE);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        lh = prefs.getBoolean("timezone", false);
        sifm = prefs.getBoolean("infosmois", false);
        hemispheresud = prefs.getBoolean("hemisphere", false);

        //Determine screen size
        if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE) {
            Log.d("Screen size : ", "Extra Large");
            ScreenSize = 4;
        }
        else if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE) {
            Log.d("Screen size : ", "Large");
            ScreenSize = 3;
        }
        else if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_NORMAL) {
            Log.d("Screen size : ", "Normal");
            ScreenSize = 2;
        }
        else if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_SMALL) {
            Log.d("Screen size : ", "Small");
            ScreenSize = 1;
        }
        else {
            Log.d("Screen size : ", "Unknown");
            ScreenSize = 0;
        }
        /* Format title */
        //TextView title =  findViewById(android.R.id.title);
        //title.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
        //title.setTextColor(Color.YELLOW);
        //title.setTextSize(20);

        /* Get the id of views */
        textLever = findViewById(R.id.textLever);
        textCoucher = findViewById(R.id.textCoucher);
        textFruits = findViewById(R.id.textFruits);
        textFleurs = findViewById(R.id.textFleurs);
        textFeuilles = findViewById(R.id.textFeuilles);
        textRacines = findViewById(R.id.textRacines);
        textApogee = findViewById(R.id.textApogee);
        textPerigee =  findViewById(R.id.textPerigee);
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
        // Affichage ou non de l'heure de perigée suivant les parametres
        if (!prefs.getBoolean("perigeetime", false))
        {
            try
            {
                textPerigeeHour.setHeight(0);
                textPerigee.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
            }
            catch (Exception e)
            {
                Log.e("Lunoid", "exception", e);
            }

        }
        if (!prefs.getBoolean("apogeetime", false))
        {
            try
            {
                textApogeeHour.setHeight(0);
                textApogee.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
            }
            catch (Exception e)
            {
                Log.e("Lunoid", "exception", e);
            }
        }
        if (!prefs.getBoolean("nodetime", false)) {
            try {
                textNoeudHour.setHeight(0);
                textNoeud.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
            } catch (Exception e) {
                Log.e("Lunoid", "exception", e);
            }
        }

        if (ScreenSize > 3) {
            textCroissant.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26.f);
            textDecroissant.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26.f);
            textDescendant.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26.f);
            textMontant.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26.f);
        }
        if (ScreenSize == 3) {
            textCroissant.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24.f);
            textDecroissant.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24.f);
            textDescendant.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24.f);
            textMontant.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24.f);
        }
        if (ScreenSize == 2) {
            textCroissant.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20.f);
            textDecroissant.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20.f);
            textDescendant.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20.f);
            textMontant.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20.f);
        }
        if (ScreenSize == 1) {
            textCroissant.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16.f);
            textDecroissant.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16.f);
            textDescendant.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16.f);
            textMontant.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16.f);
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
        calMax.set(Calendar.YEAR, 2020);
        calMax.set(Calendar.HOUR_OF_DAY, 23);
        calMax.set(Calendar.MINUTE, 59);

        sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        sdf2 = new SimpleDateFormat("EEEE dd MMMM yyyy", Locale.getDefault());

        Calendar calendar = Calendar.getInstance();
        selday.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
        selday.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
        selday.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH));
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
        textFruits.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                nextJType(mapJour, "Fruits");
            }
        });
        imageFruit.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                nextJType(mapJour, "Fruits");
            }
        });
        textFeuilles.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                nextJType(mapJour, "Feuilles");
            }
        });
        imageFeuille.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                nextJType(mapJour, "Feuilles");
            }
        });
        textRacines.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                nextJType(mapJour, "Racines");
            }
        });
        imageRacine.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                nextJType(mapJour, "Racines");
            }
        });
        textFleurs.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                nextJType(mapJour, "Fleurs");
            }
        });
        imageFleur.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                nextJType(mapJour, "Fleurs");
            }
        });
        textApogee.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                nextJAPN(mapApogee);
            }
        });
        textPerigee.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                nextJAPN(mapPerigee);
            }
        });
        textNoeud.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                nextJAPN(mapNoeud);
            }
        });
        imgLune.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                DisplayInfosDuMois(selday.get(Calendar.YEAR), selday.get(Calendar.MONTH));
            }
        });
        try
        {
            textApogeeHour.setOnClickListener(new View.OnClickListener()
            {
                public void onClick(View v)
                {
                    nextJAPN(mapApogee);
                }
            });
            textPerigeeHour.setOnClickListener(new View.OnClickListener()
            {
                public void onClick(View v)
                {
                    nextJAPN(mapPerigee);
                }
            });
            textNoeudHour.setOnClickListener(new View.OnClickListener()
            {
                public void onClick(View v)
                {
                    nextJAPN(mapNoeud);
                }
            });
        }
        catch (Exception e)
        {
            Log.e("Lunoid", "exception", e);
        }

        textCroissant.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                nextJSens(mapCroissant, "1");
            }
        });
        textDecroissant.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                nextJSens(mapCroissant, "0");
            }
        });
        textMontant.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                nextJSens(mapMontant, "1");
            }
        });
        textDescendant.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                nextJSens(mapMontant, "0");
            }
        });

        mGestureDetector = new GestureDetector(this, new LearnGestureListener());
    }  // onCreate
    class LearnGestureListener extends GestureDetector.SimpleOnGestureListener
    {
        @Override
        public boolean onSingleTapUp(MotionEvent ev)
        {
            //Log.d("onSingleTapUp",ev.toString());
            return true;
        }
        @Override
        public void onShowPress(MotionEvent ev)
        {
            //Log.d("onShowPress",ev.toString());
        }
        @Override
        public void onLongPress(MotionEvent ev)
        {
            //Log.d("onLongPress",ev.toString());
        }
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            //Log.d("onScroll",e1.toString());
            return true;
        }
        @Override
        public boolean onDown(MotionEvent ev)
        {
            //Log.d("onDownd",ev.toString());
            return true;
        }
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
        {
            Calendar c = Calendar.getInstance();
            boolean datechanged = false;

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
                c.add(Calendar.DATE, 1);
                datechanged = true;
            }

            // left to right : previous day
            if (f2 > f1 + 60 && (f2 - f1) > abs(f3 - f4) && (selday.getTime().getTime() - calMin.getTime().getTime() > 86400000)) {
                c.set(selday.get(Calendar.YEAR), selday.get(Calendar.MONTH), selday.get(Calendar.DAY_OF_MONTH));
                c.add(Calendar.DATE, -1);
                datechanged = true;
            }

            // bottom to top : display info
            if ((f3 > (f4 + 60)) && ((f3 - f4) > abs(f1 - f2))) {
                DisplayInfosDuMois(selday.get(Calendar.YEAR), selday.get(Calendar.MONTH));
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
            }
            if (datechanged) {
                onDateChange(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
            }
            return true;
        }
    }
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        return mGestureDetector.onTouchEvent(event);
    }
    @Override
    public void onResume()
    {  // After a pause OR at startup
        super.onResume();

        // afficher la date du jour au (re)démarrage
        onDateChange(selday.get(Calendar.YEAR), selday.get(Calendar.MONTH), selday.get(Calendar.DAY_OF_MONTH));
    }
    // creation des menus
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        _menu = menu;
        ShowHideInfosMenu(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), _menu);
        return true;
    }
    // gestion des menus
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Intent intent;
        String AppVersion="";

        try
        {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            AppVersion = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        switch (item.getItemId())
        {
        case R.id.item1:
            intent = new Intent(this, About.class);
            startActivity(intent);
            return true;
        case R.id.item2:
            intent = new Intent(this, Infos.class);
            startActivity(intent);
            return true;
        case R.id.item3:
            intent = new Intent(this, PrefsActivity.class);
            startActivity(intent);
            return true;
        /*
        case R.id.item4:
            intent = new Intent(new Intent(Intent.ACTION_VIEW,Uri.parse("https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=U6CX3ZNDSYFMQ")));
            startActivity(intent);
            return true;
         */

        case R.id.item5: // lien sur google play
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://details?id=com.nialon"));
            startActivity(intent);
            return true;

        case R.id.item6: // partager
        intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_SUBJECT,  getResources().getString(R.string.app_name));

        intent.putExtra(Intent.EXTRA_TEXT,
                Html.fromHtml("<html><head></head><body>" +
                        "<p>" + getResources().getString(R.string.sharemessage) + "</p>" +
                        "<p>https://play.google.com/store/apps/details?id=com.nialon</p>" +
                        "<p>https://www.facebook.com/LunoidApp</p>" +
                        "</body></html>")
        );
        startActivity(Intent.createChooser(intent, getResources().getString(R.string.app_name)));
        return true;

        case R.id.item7: // contacter le développeur
            intent = new Intent(android.content.Intent.ACTION_SEND);
            intent.setType("text/html");
            intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
            intent.putExtra(android.content.Intent.EXTRA_EMAIL,new String[] { "mnialon@gmail.com" });
            intent.putExtra(Intent.EXTRA_TEXT,
                    Html.fromHtml("<html><head></head><body>" +
                            "<p>---</p>" +
                            "<p>Version Application : " + AppVersion + "</p>" +
                            "<p>Android Version : " + Build.VERSION.SDK_INT + " (" + Build.VERSION.RELEASE + ")</p>" +
                            "<p>Device Type : " + Build.MANUFACTURER + " (" + Build.MODEL + ")</p>" +
                            "<p>Density : " + getResources().getDisplayMetrics().densityDpi + "</p>" +
                            "<p>Height : " + getResources().getDisplayMetrics().heightPixels + "</p>" +
                            "<p>Width : " + getResources().getDisplayMetrics().widthPixels + "</p>" +
                            "</body></html>")
            );
            startActivity(Intent.createChooser(intent, getResources().getString(R.string.app_name)));
            return true;

            case R.id.item8:
                DisplayInfosDuMois(selday.get(Calendar.YEAR), selday.get(Calendar.MONTH));
                return true;
        }
        return false;
    }

    private void nextJType(Map<String, String> m, String C) {
        String ds;
        Date td;

        Log.d("nextJType", "Start");

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
                    cal1 = d;
                    cal1.set(Calendar.HOUR_OF_DAY, Integer.parseInt(s.substring(0, 2)));
                    cal1.set(Calendar.MINUTE, Integer.parseInt(s.substring(3, 5)));

//                    d.setHours(Integer.parseInt(s.substring(0, 2)));
//                    d.setMinutes(Integer.parseInt(s.substring(3, 5)));
//                    nboffset = TimeZone.getDefault().getOffset(d.getTime()) / 1000 / 3600;
                    nboffsetCal = TimeZone.getDefault().getOffset(cal1.getTime().getTime()) / 1000 / 3600;
//                    nboffset = TimeZone.getTimeZone("Europe/Paris").getOffset(d.getTime())/1000/3600;
                    // nboffsetCal = TimeZone.getTimeZone("Europe/Paris").getOffset(cal1.getTime().getTime())/1000/3600;
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
                } else return s.substring(0,5);
            }
        }
    }
    private void DisplayInfosDuMois(int year, int month)
    {
        Intent intent;
        intent = new Intent(this, ConseilsDuMois.class);
        String message = "";
        String type_message;

        Log.d("DisplayInfosDuMois", (month+1) + "/" + year);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //SharedPreferences prefs = getSharedPreferences("com.nialon", MODE_PRIVATE);

        linetot.setLength(0);
        if ((year >= 2016) && (year <= 2018))
        {
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
            }
            else
            {
                message = "Pas d'informations pour cette période. Les conseils de plantation ne sont disponibles que pour les mois de Mars à Août.";
                type_message = "0";
            }
            if (type_message.equals("1"))
            {
                Log.d("message", message);
                intent.putExtra(EXTRA_MESSAGE, message);
                intent.putExtra("type_message", type_message);
                startActivity(intent);
            }
        }
        else if (year >= 2019)
        {
            String m1;

            if (month >= 0 && month <= 11)
            {
                m1 = tableauMois[month];
                sifm = prefs.getBoolean("infosmois", false);
                if (sifm)
                {
                    BuildConseilMois(month, year);
                }
                else
                {
                    BuildConseilJour(dateString, m1, dateString2);
                }

                type_message = "2";
                intent.putExtra("type_message", type_message);
                intent.putExtra("htmltxt", htmltxt);

                startActivity(intent);
            }
            else
            {
                message = "Pas d'informations pour cette période. Les conseils de plantation ne sont disponibles que pour les mois de Mars à Septembre.";
                type_message = "0";
            }
        }
        else
        {
            message = "Pas d'informations pour cette période. Les conseils de plantation ne sont disponibles qu'à partir de 2016";
            type_message = "0";
        }

        if (type_message.equals("0"))
        {
            Toast InfosToast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
            InfosToast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
            InfosToast.show();
        }
    }
    private void BuildConseilMois(int month, int year)
    {
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

        addText("<font color = #ffff00 face=\"sans-serif-light\">" + ds2 + "</font>");

       // Log.d("BuildConseilJour", ds);
        if (mapMontant.get(ds).substring(0,1).equals("0"))
        {
            s1 = "descendante";
            s2 = "";
        }
        if (mapMontant.get(ds).substring(0,1).equals("1"))
        {
            s1 = "montante";
            s2 = "";
        }
        if (mapMontant.get(ds).substring(0,1).equals("2"))
        {
            s1 = "montante";
            s2 = "descendante";
            hs12 = heurelocale(mapMontant.get(ds).substring(2, 7),selday,lh);
        }
        if (mapMontant.get(ds).substring(0,1).equals("3"))
        {
            s1 = "descendante";
            s2 = "montante";
            hs12 = heurelocale(mapMontant.get(ds).substring(2, 7), selday, lh);
        }
        if (mapJour.get(ds).length() >= 15 &&  (mapJour.get(ds)).toLowerCase().substring(0,15).equals("feuilles/fruits"))
        {
            t1 = "feuilles";
            t2 = "fruits";
            ht12 = heurelocale(mapJour.get(ds).substring(16, 21),selday,lh);
        }
        else if (mapJour.get(ds).length() >= 14 && (mapJour.get(ds)).toLowerCase().substring(0,14).equals("fruits/racines"))
        {
            t1 = "fruits";
            t2 = "racines";
            ht12 = heurelocale(mapJour.get(ds).substring(15, 20),selday,lh);
        }
        else if (mapJour.get(ds).length() >= 14 && (mapJour.get(ds)).toLowerCase().substring(0,14).equals("racines/fleurs"))
        {
            t1 = "racines";
            t2 = "fleurs";
            ht12 = heurelocale(mapJour.get(ds).substring(15, 20),selday,lh);
        }
        else if (mapJour.get(ds).length() >= 15 && (mapJour.get(ds)).toLowerCase().substring(0,15).equals("fleurs/feuilles"))
        {
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
            }
            else if ((perigeelocal.compareTo("19:00") < 0)) {
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
                if (mapNoeud.get(ds).substring(5, 6).equals("+")) {
                    addText("<font color=#ff0000>" + getString(R.string.noeudascendanta) + " " + noeudlocal);
                } else
                {
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
        }
        catch (Exception e)
        {
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
        if (!s1.equals("") && !s2.equals(""))
        {
            if (!t1.equals("") && t2.equals(""))
            {
                addTextFromFile(m1 + "_" + s1 + "_" + t1 + "_" + lg + ".txt");
                addText("<font color=#ffff00>" +  hs12 + "</font>" );
                addTextFromFile(m1 + "_" + s2 + "_" + t1 + "_" + lg + ".txt");
            }
            if (!t1.equals("") && !t2.equals(""))
            {
                if (hs12.compareTo(ht12) < 0)
                {
                    addTextFromFile(m1 + "_" + s1 + "_" + t1 + "_" + lg + ".txt");
                    addText("<font color=#ffff00>" +  hs12 + "</font>" );
                    addTextFromFile(m1 + "_" + s2 + "_" + t1 + "_" + lg + ".txt");
                    System.out.println(m1 + "_" + s2 + "_" + t1 + "_" + lg + ".txt");
                    addText("<font color=#ffff00>" +  ht12  + "</font>");
                    addTextFromFile(m1 + "_" + s2 + "_" + t2 + "_" + lg + ".txt");
                }
                if (hs12.compareTo(ht12) > 0)
                {
                    addTextFromFile(m1 + "_" + s1 + "_" + t1 + "_" + lg + ".txt");
                    addText( "<font color=#ffff00>" +  ht12 + "</font>");
                    addTextFromFile(m1 + "_" + s1 + "_" + t2 + "_" + lg + ".txt");
                    addText("<font color=#ffff00>" +  hs12 + "</font>" );
                    addTextFromFile(m1 + "_" + s2 + "_" + t2 + "_" + lg + ".txt");
                }
                if (hs12.compareTo(ht12) == 0)
                {
                    addTextFromFile(m1 + "_" + s1 + "_" + t1 + "_" + lg + ".txt");
                    addText( "<font color=#ffff00>" +  hs12 + "</font>");
                    addTextFromFile(m1 + "_" + s2 + "_" + t2 + "_" + lg + ".txt");
                }
            }
        }
    }
    private void addTextFromFile(String s)
    {
        //Log.d("addTextFromFile", s);

        try
        {
            BufferedReader br = new BufferedReader(new InputStreamReader(getAssets().open(s)));

            String st;

            while ((st = br.readLine()) != null)
            {
                //System.out.println(st);
                linetot.append("\n");
                linetot.append(st);
            }
        }
        catch (Exception e)
        {
            System.out.println("Something went wrong.");
        }
    }

    private  void addText(String s)
    {
        //Log.d("addText ", s);
        linetot.append("<br/>");
        linetot.append(s);
    }
    private void read_data()
    {
        Log.d("read_data", "begin");
        Resources myRes = getResources();
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
                mapLever.put(separated[0], separated[1]);
                mapCoucher.put(separated[0], separated[2]);
                mapPhase.put(separated[0], separated[3]);
                mapJour.put(separated[0], separated[4]);
                mapApogee.put(separated[0], separated[5]);
                mapPerigee.put(separated[0], separated[6]);
                mapNoeud.put(separated[0], separated[7]);
                mapComment.put(separated[0], separated[8]);
                mapMontant.put(separated[0], separated9.toString());
                mapCroissant.put(separated[0], separated[10]);
                mapSigne.put(separated[0],separated[11]);
                mapConseil.put(separated[0],separated[12]);
                mapEclair.put(separated[0],separated[13]);

            }
        } catch (IOException e) {
            Log.e("Lunoid", "exception", e);
        }
    }
    public static String getLever() {
        return heurelocale(mapLever.get(dateString), selday, lh);
    }
    public static String getCoucher() {
        return heurelocale(mapCoucher.get(dateString), selday, lh);
    }

    public void datePicker(View view) {
        // affichage du calendrier sur click de la date en clair
        DatePickerFragment fragment = new DatePickerFragment();
        fragment.show(getSupportFragmentManager(), "date");
    }
    public void editNote(View view)
    {
        SharedPreferences sp = getSharedPreferences("myNotes", MODE_PRIVATE);
        Map<String, ?> allEntries = sp.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            Log.d("map values", entry.getKey() + ": " + entry.getValue().toString());
        }
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
            if (!str1Notif.equals("0") && !str1Notif.equals("")) {
                sw1.setChecked(true);
            } else {
                sw1.setChecked(false);
            }
            if (today.getTime().before(selday.getTime())) {
                sw1.setEnabled(true);
            } else {
                sw1.setEnabled(false);
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.notedu) + " " + dateString + " :");
            builder.setView(root);
            builder.setPositiveButton(R.string.sauvegarder, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SharedPreferences sp = getSharedPreferences("myNotes", MODE_PRIVATE);
                    UUID uuid;
                    SharedPreferences.Editor sedt = sp.edit();
                    sedt.putString(dateString, e1.getText().toString());
                    sedt.apply();
                    if (!e1.getText().toString().equals("")) {
                        imageNote.setAlpha(1.0f);
                        imageNote.setColorFilter(Color.YELLOW);
                    } else {
                        imageNote.setAlpha(0.5f);
                        imageNote.clearColorFilter();
                    }

                    SharedPreferences spNotif = getSharedPreferences("myNotif", MODE_PRIVATE);
                    SharedPreferences.Editor sedtNotif = spNotif.edit();
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
                    InfosToast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
                    InfosToast.show();
                    // Do something
                }
            });
            builder.setNegativeButton(R.string.annuler, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast InfosToast = Toast.makeText(getApplicationContext(), R.string.notenonsauvegardee, Toast.LENGTH_SHORT);
                    InfosToast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
                    InfosToast.show();
                    // Do something
                }
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

    private Menu getMenu()
    {
        //use it like this
        return _menu;
    }
    private void ShowHideInfosMenu(int y, int mo, Menu me)
    {
        MenuItem item;
        try {
            item = me.findItem(R.id.item8);

            if (y >= 2019 && (mo >= 0 && mo <= 11)) {
                //item.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
                item.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM);
            } else {
                item.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_NEVER);
            }
        }
        catch (Exception e)
        {
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
} // Lunoid

