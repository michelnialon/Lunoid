package com.nialon;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
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
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

// TODO : signes du zodiaque
// todo : partager
// todo : envoyer un commentaire
// todo : évaluer l'application
// todo : vérifier nouvelle version



public class Lunoid extends FragmentActivity implements DatePickerDialog.OnDateSetListener
{
    /** Called when the activity is first created. */
    private SimpleDateFormat sdf;
    private SimpleDateFormat sdf2;
    static String dateString;
    String dateString2 ;
    static Date date1;
    private Date dateDebut;
    private Date dateFin;
    private GestureDetector mGestureDetector;
    private static Boolean lh;
    private static Boolean sifm;
    public final static String EXTRA_MESSAGE = "com.nialon.Lunoid.MESSAGE";
    private static String htmltxt;
    StringBuilder linetot = new StringBuilder();
    String tableauMois[] = {"janvier", "fevrier", "mars" , "avril","mai", "juin", "juillet" , "aout", "septembre", "octobre", "novembre" , "decembre"};
    private Menu _menu = null;

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
   // Map<String, String> mapQuartier= new HashMap<>();
    Map<String, String> mapSigne= new HashMap<>();
    Map<String, String> mapConseil= new HashMap<>();
    Map<String, String> mapEclair= new HashMap<>();

    TextView textLever;
    TextView textCoucher;
    DatePicker datePicker1;
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
    String InfosStr;

    public void onDateChange(int year, int monthOfYear, int dayOfMonth)
    {
        int resId;

        Log.d("Lunoid","onDateChange");
        Calendar cc = new GregorianCalendar();

        cc.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        cc.set(Calendar.MONTH,monthOfYear);
        cc.set(Calendar.YEAR, year);

        date1.setTime(cc.getTimeInMillis());
        Log.d("dayofmonth", Integer.toString(dayOfMonth));
        Log.d("monthofyear", Integer.toString(monthOfYear));
        Log.d("year", Integer.toString(year));

        if (date1.compareTo(dateDebut) >= 0 &&	date1.compareTo(dateFin) <=0)
        {
            dateString = sdf.format(date1);
            dateString2 = sdf2.format(date1);
            Log.d("datestring",dateString);
            Log.d("datestring2",dateString2);
            textCoucher.setText(heurelocale(mapCoucher.get(dateString),date1,lh));
            textLever.setText(heurelocale(mapLever.get(dateString),date1,lh));

            textJour.setText(dateString2);

            String ecl = mapEclair.get(dateString);
            try {
                if (ecl.equals("-")) {
                    String ecl2 = "lune0";
                    String ecl3;
                    int ph = Integer.parseInt(mapPhase.get(dateString));
                    ecl3 = ecl2.concat(String.format(Locale.getDefault(),"%02d", ph));
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
            }
            catch (Exception e)
            {
                Log.e("Lunoid", "exception", e);
            }

            // Fruits/Fleurs/Feuilles/Racines
            textFeuilles.setTextColor(mapJour.get(dateString).contains("Feuilles") ? Color.YELLOW : Color.rgb(120,120,120));
            textFleurs.setTextColor(mapJour.get(dateString).contains("Fleurs") ? Color.YELLOW : Color.rgb(120,120,120));
            textFruits.setTextColor(mapJour.get(dateString).contains("Fruits") ? Color.YELLOW : Color.rgb(120,120,120));
            textRacines.setTextColor(mapJour.get(dateString).contains("Racines") ? Color.YELLOW : Color.rgb(120,120,120));
            resId = getResources().getIdentifier(mapJour.get(dateString).contains("Racines") ? "racine30x30" : "racine30x30_grey", "drawable", getPackageName());
            imageRacine.setImageResource(resId);
            resId = getResources().getIdentifier(mapJour.get(dateString).contains("Fleurs") ? "fleur30x30" : "fleur30x30_grey", "drawable", getPackageName());
            imageFleur.setImageResource(resId);
            resId = getResources().getIdentifier(mapJour.get(dateString).contains("Feuilles") ? "feuille30x30" : "feuille30x30_grey", "drawable", getPackageName());
            imageFeuille.setImageResource(resId);
            resId = getResources().getIdentifier(mapJour.get(dateString).contains("Fruits") ? "fruit30x30" : "fruit30x30_grey", "drawable", getPackageName());
            imageFruit.setImageResource(resId);

            // Apogee/Perigee/Noeud
            textApogee.setTextColor(!mapApogee.get(dateString).equals("0") ? Color.RED : Color.rgb(120,120,120));
            textPerigee.setTextColor(!mapPerigee.get(dateString).equals("0") ? Color.RED : Color.rgb(120,120,120));
            textNoeud.setTextColor(!mapNoeud.get(dateString).equals("0") ? Color.RED : Color.rgb(120,120,120));

            try
            {
                textApogeeHour.setTextColor(!mapApogee.get(dateString).equals("0") ? Color.RED : Color.rgb(120,120,120));
                textApogeeHour.setText(!mapApogee.get(dateString).equals("0") ? (mapApogee.get(dateString).equals("88:88")? "--:--" : heurelocale(mapApogee.get(dateString),date1,lh)) : "--:--");

                textPerigeeHour.setTextColor(!mapPerigee.get(dateString).equals("0") ? Color.RED : Color.rgb(120,120,120));
                textPerigeeHour.setText(!mapPerigee.get(dateString).equals("0") ? (mapPerigee.get(dateString).equals("88:88")? "--:--" : heurelocale(mapPerigee.get(dateString),date1,lh)) : "--:--");

                textNoeudHour.setTextColor(!mapNoeud.get(dateString).equals("0") ? Color.RED : Color.rgb(120,120,120));
                textNoeudHour.setText(!mapNoeud.get(dateString).equals("0") ? (mapNoeud.get(dateString).equals("88:88")? "--:--" : heurelocale(mapNoeud.get(dateString).substring(0,5),date1,lh)) : "--:--");
            }
            catch (Exception e)
            {
                Log.e("Lunoid", "exception", e);
            }
            // Montant/Descendant
            if (mapMontant.get(dateString).equals("1") || mapMontant.get(dateString).startsWith("2") || mapMontant.get(dateString).startsWith("3")) {
                textMontant.setTextColor(Color.YELLOW);
            }
            else {
                textMontant.setTextColor(Color.rgb(120,120,120));
            }
            if (mapMontant.get(dateString).equals("0") || mapMontant.get(dateString).startsWith("2") || mapMontant.get(dateString).startsWith("3")) {
                textDescendant.setTextColor(Color.YELLOW);
            }
            else {
                textDescendant.setTextColor(Color.rgb(120,120,120));
            }

            // Croissant/Decroissant
            textCroissant.setTextColor(mapCroissant.get(dateString).equals("1") ? Color.YELLOW : mapCroissant.get(dateString).equals("2") ? Color.YELLOW : Color.rgb(120,120,120));
            textDecroissant.setTextColor(mapCroissant.get(dateString).equals("0") ? Color.YELLOW : mapCroissant.get(dateString).equals("2") ? Color.YELLOW : Color.rgb(120,120,120));
            InfosStr = "";
            if (!mapComment.get(dateString).equals("")) {
                InfosStr += mapComment.get(dateString);
                if (mapEclair.get(dateString).equals("100")) {
                    InfosStr += "\nPleine Lune";
                }
                if (mapEclair.get(dateString).equals("0")) {
                    InfosStr += "\nNouvelle Lune";
                }
            }
            else {
                if (mapEclair.get(dateString).equals("100")) {
                    InfosStr = "Pleine Lune";
                }
                if (mapEclair.get(dateString).equals("0")) {
                    InfosStr = "Nouvelle Lune";
                }
            }
            if (!InfosStr.equals("")) {
                Toast InfosToast = Toast.makeText(getApplicationContext(), InfosStr, Toast.LENGTH_SHORT);
                InfosToast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
                InfosToast.show();
            }
            ShowHideInfosMenu(year, monthOfYear, getMenu());

        }
        else
        {
            textLever.setText("--:--");
            textCoucher.setText("--:--");
            textJour.setText(R.string.nodata);
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
            resId = getResources().getIdentifier("racine30x30_grey", "drawable", getPackageName());
            imageRacine.setImageResource(resId);
            resId = getResources().getIdentifier("fruit30x30_grey", "drawable", getPackageName());
            imageFruit.setImageResource(resId);
            resId = getResources().getIdentifier("fleur30x30_grey", "drawable", getPackageName());
            imageFleur.setImageResource(resId);
            resId = getResources().getIdentifier("feuille30x30_grey", "drawable", getPackageName());
            imageFeuille.setImageResource(resId);
            if (textPct != null)
            {
                textPct.setText(("0 %"));
            }
            resId = getResources().getIdentifier("nlune_0", "drawable", getPackageName());
            imgLune.setImageResource(resId);
            try
            {
                textPerigeeHour.setTextColor(Color.LTGRAY);
                textPerigee.setTextColor(Color.LTGRAY);
                textNoeudHour.setTextColor(Color.LTGRAY);
            }
            catch (Exception e)
            {
                Log.e("Lunoid", "exception", e);
            }
            textNoeud.setTextColor(Color.LTGRAY);
        }

    }
    OnDateChangedListener listener = new OnDateChangedListener()
    {
        @Override
        public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth)
        {
            try {
                onDateChange(year, monthOfYear, dayOfMonth);
            }
            catch (Exception e)
            {
                Log.e("Lunoid", "exception", e);
            }
        } // onDateChanged
    }; // onDateChangeListener

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        /* to remove */
/*
        Locale locale = new Locale("fr");
        Locale.setDefault(locale);
        Configuration config = getBaseContext().getResources().getConfiguration();
        config.locale = locale;
*/
        /* to remove */
        setContentView(R.layout.main);
        //setContentView(R.layout.main2);

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

        //Determine screen size
        if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE) {
            Log.d("Screen size : ", "Extra Large");
        }
        else if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE) {
            Log.d("Screen size : ", "Large");
        }
        else if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_NORMAL) {
            Log.d("Screen size : ", "Normal");
        }
        else if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_SMALL) {
            Log.d("Screen size : ", "Small");
        }
        else {
            Log.d("Screen size : ", "Unknown");
        }
        /* Format title */
        //TextView title =  findViewById(android.R.id.title);
        //title.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
        //title.setTextColor(Color.YELLOW);
        //title.setTextSize(20);

        /* Get the id of views */
        datePicker1 = findViewById(R.id.datePicker1);
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
        if (!prefs.getBoolean("nodetime", false))
        {
            try
            {
                textNoeudHour.setHeight(0);
                textNoeud.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
            }
            catch (Exception e)
            {
                Log.e("Lunoid", "exception", e);
            }
        }

        /* read data from csv text file */
        read_data();

        dateDebut = new Date();
        dateFin = new Date();
        dateDebut.setDate(1); // du 01/05/2012
        dateDebut.setMonth(4);
        dateDebut.setYear(2012 - 1900);
        dateFin.setDate(1);   //
        dateFin.setMonth(0);
        dateFin.setYear(2021 - 1900);

        sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        sdf2 = new SimpleDateFormat("EEEE dd MMMM yyyy", Locale.getDefault());

        date1= new Date(datePicker1.getYear()-1900, datePicker1.getMonth(), datePicker1.getDayOfMonth());

        datePicker1.init(datePicker1.getYear(), datePicker1.getMonth(), datePicker1.getDayOfMonth(),	listener);

        datePicker1.setCalendarViewShown(false);
        datePicker1.setSpinnersShown(true);


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
                nextJ(mapJour, "Fruits", false);
            }
        });
        imageFruit.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                nextJ(mapJour, "Fruits", false);
            }
        });
        textFeuilles.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                nextJ(mapJour, "Feuilles",false);
            }
        });
        imageFeuille.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                nextJ(mapJour, "Feuilles", false);
            }
        });
        textRacines.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                nextJ(mapJour, "Racines", false);
            }
        });
        imageRacine.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                nextJ(mapJour, "Racines", false);
            }
        });
        textFleurs.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                nextJ(mapJour, "Fleurs", false);
            }
        });
        imageFleur.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                nextJ(mapJour, "Fleurs", false);
            }
        });
        textApogee.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                nextJ(mapApogee, "0",true);
            }
        });
        textPerigee.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                nextJ(mapPerigee, "0", true);
            }
        });
        textNoeud.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                nextJ(mapNoeud, "0", true);
            }
        });
        imgLune.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                DisplayInfosDuMois(datePicker1.getYear(), datePicker1.getMonth());
            }
        });
        try
        {
            textApogeeHour.setOnClickListener(new View.OnClickListener()
            {
                public void onClick(View v)
                {
                    nextJ(mapApogee, "0",true);
                }
            });
            textPerigeeHour.setOnClickListener(new View.OnClickListener()
            {
                public void onClick(View v)
                {
                    nextJ(mapPerigee, "0", true);
                }
            });
            textNoeudHour.setOnClickListener(new View.OnClickListener()
            {
                public void onClick(View v)
                {
                    nextJ(mapNoeud, "0", true);
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
                nextJ(mapCroissant, "1", false);
            }
        });
        textDecroissant.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                nextJ(mapCroissant, "0",false);
            }
        });
        textMontant.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                nextJ(mapMontant, "1", false);
            }
        });
        textDescendant.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                nextJ(mapMontant, "0", false);
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
            Calendar c= Calendar.getInstance();
            c.set(datePicker1.getYear(), datePicker1.getMonth(), datePicker1.getDayOfMonth());

            //Log.d("d",e1.toString());
            //Log.d("e2",e2.toString());
            Float f1 = e1.getX();
            //Log.d("e1x",f1.toString());
            Float f2 = e2.getX();
            //Log.d("e2x",f2.toString());
            Float f3 = e1.getY();
            Float f4 = e2.getY();
            if (f1>f2+60)
            {
                c.add(Calendar.DATE, 1);
                Log.d("y:",Integer.toString(c.get(Calendar.YEAR)));
                Log.d("m:",Integer.toString(c.get(Calendar.MONTH)));
                Log.d("d:",Integer.toString(c.get(Calendar.DAY_OF_MONTH)));
                datePicker1.updateDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
                //        	  Toast.makeText(getApplicationContext(), "plus", Toast.LENGTH_SHORT).show();
            }
            else if (f2>f1+60)
            {
                c.add(Calendar.DATE, -1);
                Log.d("y:",Integer.toString(c.get(Calendar.YEAR)));
                Log.d("m:",Integer.toString(c.get(Calendar.MONTH)));
                Log.d("d:",Integer.toString(c.get(Calendar.DAY_OF_MONTH)));
                datePicker1.updateDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
                //       	  Toast.makeText(getApplicationContext(), "moins", Toast.LENGTH_SHORT).show();
            }
            else if (f3>f4+60 || f4>f3+60)
            {
                DisplayInfosDuMois(datePicker1.getYear(), datePicker1.getMonth());
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
        listener.onDateChanged(datePicker1, datePicker1.getYear(), datePicker1.getMonth(), datePicker1.getDayOfMonth());
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
        switch (item.getItemId())
        {
        case R.id.item1:
            intent = new Intent(this, About.class);
            startActivity(intent);
            return true;
        case R.id.item2:
            intent = new Intent(this, infos.class);
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
                Html.fromHtml(new StringBuilder()
                        .append("<html><head></head><body>")
                        .append("<p>Cette application devrait t'intéresser.</p>")
                        .append("<p>(Pour jardiner en fonction de la lune)</p>")
                        .append("<p>https://play.google.com/store/apps/details?id=com.nialon></p>")
                        .append("<p>https://www.facebook.com/LunoidApp></p>")
                        .append("</body></html>")
                        .toString())
        );
        startActivity(Intent.createChooser(intent, getResources().getString(R.string.app_name)));
        return true;

        case R.id.item7: // contacter le développeur
            intent = new Intent(android.content.Intent.ACTION_SEND);
            intent.setType("text/html");
            intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
            intent.putExtra(android.content.Intent.EXTRA_EMAIL,new String[] { "mnialon@gmail.com" });
            startActivity(Intent.createChooser(intent, getResources().getString(R.string.app_name)));
            return true;

        case R.id.item8:
            DisplayInfosDuMois(datePicker1.getYear(), datePicker1.getMonth());
            return true;
        }
        return false;
    }
    private void nextJ(Map<String, String>m, String C, boolean b)
    {
        String ds;
        Date td;

        Log.d("nextj", C);
        Log.d("datefin", dateFin.toString());
        Calendar c= Calendar.getInstance();
        //c.set(today.getYear(), today.getMonth(), today.getDate());
        c.set(datePicker1.getYear(), datePicker1.getMonth(), datePicker1.getDayOfMonth());
        td = c.getTime();
        while ((td.compareTo(dateDebut) <0))
        {
            c.add(Calendar.DATE, 1);
            td = c.getTime();
        }

        if (b)
        {
            do
            {
                c.add(Calendar.DATE, 1);
                ds = sdf.format(c.getTime());
                td = c.getTime();
            }
            while ((td.compareTo(dateFin) <0) && (!(m.get(ds).length()>=5)));
        }
        else
        {
            do
            {
                c.add(Calendar.DATE, 1);
                ds = sdf.format(c.getTime());
                td = c.getTime();
            }
            while ((td.compareTo(dateFin) <0) && (!m.get(ds).equals(C)));
        }
        if (td.compareTo(dateFin) <0)
        {
            Log.d("1", c.toString());
            datePicker1.updateDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        }
        else {
            Toast.makeText(getApplicationContext(), R.string.notfound, Toast.LENGTH_SHORT).show();
        }
    }
    private static String heurelocale(String s, Date d, boolean lh)
    {
        if (s == null)
        {
            return "--:--";
        }
        else {
            if (s.equals("--:--")) {
                return s;
            } else {
                if (lh) {
                    int nboffset;

                    d.setHours(Integer.valueOf(s.substring(0, 2)));
                    d.setMinutes(Integer.valueOf(s.substring(3, 5)));
                    nboffset = TimeZone.getDefault().getOffset(d.getTime()) / 1000 / 3600;
                    //nboffset = TimeZone.getTimeZone("Europe/Paris").getOffset(d.getTime())/1000/3600;
                    //Log.d("d",d.toString());
                    //Log.d("nb",String.valueOf(nboffset));
                    String s1;
                    String s2;

                    Calendar cal = Calendar.getInstance();
                    cal.set(Calendar.HOUR_OF_DAY, Integer.valueOf(s.substring(0, 2)));
                    cal.set(Calendar.MINUTE, Integer.valueOf(s.substring(3, 5)));

                    cal.add(Calendar.HOUR_OF_DAY, nboffset);
                    s1 = String.valueOf(cal.get(Calendar.HOUR_OF_DAY));
                    s1 = (s1.length() == 1 ? "0" + s1 : s1);
                    s2 = String.valueOf(cal.get(Calendar.MINUTE));
                    s2 = (s2.length() == 1 ? "0" + s2 : s2);
                    return s1 + ":" + s2;
                } else
                    return s;
            }
        }
    }
    private void DisplayInfosDuMois(int year, int month)
    {
        Intent intent;
        intent = new Intent(this, ConseilsDuMois.class);
        String message = "";
        String type_message;

        Log.d("DisplayInfosDuMois", Integer.toString(month) + "/" + Integer.toString(year));

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        linetot.setLength(0);
        if ((year >= 2016) && (year <=2018))
        {
            type_message = "1";
            if ((month == 2)) {
                message = "mars"  + Integer.toString(year);
            } else if ((month == 3)) {
                message = "avril"  + Integer.toString(year);
            } else if ((month == 4)) {
                message = "mai"  + Integer.toString(year);
            } else if ((month == 5)) {
                message = "juin"  + Integer.toString(year);
            } else if ((month == 6)) {
                message = "juillet"  + Integer.toString(year);
            } else if ((month == 7)) {
                message = "aout"  + Integer.toString(year);
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

            if (month >= 2 && month <= 8)
            {
                m1 = tableauMois[month];
                sifm = prefs.getBoolean("infosmois", false);
                if (sifm)
                {
                    BuildConseilMois(month, year);
                }
                else
                {
                    BuildConseilJour(dateString, date1, m1, dateString2);
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

        for (Integer d=1; d<= nbdays; d++)
        {
            cal.set(Calendar.DAY_OF_MONTH, d);
            d1.setTime(cal.getTimeInMillis());
            ds = sdf.format(d1);
            ds2 = sdf2.format(d1);
            BuildConseilJour(ds, d1, m, ds2);
            Log.d("d", d.toString());
        }
    }
    private void BuildConseilJour(String ds, Date date1, String m1 , String ds2)
    {
        String hs12="";
        String ht12;
        String s1="";
        String s2="";
        String t1;
        String t2;

        addText("<font color = #ffff00 face=\"sans-serif-light\">" + ds2 + "</font>");

        Log.d("BuildConseilJour", ds);
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
            hs12 = mapMontant.get(ds).substring(2,7);
        }
        if (mapMontant.get(ds).substring(0,1).equals("3"))
        {
            s1 = "descendante";
            s2 = "montante";
            hs12 = mapMontant.get(ds).substring(2,7);
        }
        if (mapJour.get(ds).length() >= 15 &&  (mapJour.get(ds)).toLowerCase().substring(0,15).equals("feuilles/fruits"))
        {
            t1 = "feuilles";
            t2 =  "fruits";
            ht12 = mapJour.get(ds).substring(15,21);
        }
        else if (mapJour.get(ds).length() >= 14 && (mapJour.get(ds)).toLowerCase().substring(0,14).equals("fruits/racines"))
        {
            t1 = "fruits";
            t2 = "racines";
            ht12 = mapJour.get(ds).substring(14,20);
        }
        else if (mapJour.get(ds).length() >= 14 && (mapJour.get(ds)).toLowerCase().substring(0,14).equals("racines/fleurs"))
        {
            t1 = "racines";
            t2 = "fleurs";
            ht12 = mapJour.get(ds).substring(14,20);
        }
        else if (mapJour.get(ds).length() >= 15 && (mapJour.get(ds)).toLowerCase().substring(0,15).equals("fleurs/feuilles"))
        {
            t1 = "fleurs";
            t2 = "feuilles";
            ht12 = mapJour.get(ds).substring(15,21);
        }
        else
        {
            t1 = mapJour.get(ds).toLowerCase();
            t2 = "";
            ht12 = "";
        }
        BuildHTMLJour( s1,  s2,  t1,  t2,  m1,  ht12,  hs12, ds2);

        testApogee(date1, ds);
        testPerigee(date1, ds);
        testNoeud(date1, ds);
        addText("************");
        htmltxt = linetot.toString();
    }
    private void testApogee(Date d1, String ds)
    {
        if (!mapApogee.get(ds).equals("0"))
        {
            String apogeelocal = heurelocale(mapApogee.get(ds),d1 ,lh);
            addText("<font color=#ff0000>Apogée à " +apogeelocal);
            Integer h1 = Integer.parseInt(apogeelocal.substring(0,2));
            if (apogeelocal.compareTo("05:00") < 0)
            {
                h1 +=5;
                addText(" -> Ne pas jardiner avant " + h1.toString() + "h"  + "</font>");
            }
            else if ((apogeelocal.compareTo("19:00") < 0))
            {
                h1 -= 5;
                addText(" -> Ne pas jardiner entre " + h1.toString() + "h et " + ((Integer)(h1+10)).toString() + "h </font>");
            }
            else if ((apogeelocal.compareTo("19:00") >= 0))
            {
                h1 -= 5;
                addText(" -> Ne pas jardiner après " + h1.toString() + "h </font>");
            }
        }
    }
    private void testPerigee(Date d1, String ds)
    {
        if (!mapPerigee.get(ds).equals("0"))
        {
            String perigeelocal = heurelocale(mapPerigee.get(ds),d1 ,lh);
            addText("<font color=#ff0000>Périgée à " + perigeelocal);
            Integer h1 = Integer.parseInt(perigeelocal.substring(0,2));
            if (perigeelocal.compareTo("05:00") < 0)
            {
                h1 +=5;
                addText(" -> Ne pas jardiner avant " + h1.toString() + "h"  + "</font>");
            }
            else if ((perigeelocal.compareTo("19:00") < 0))
            {
                h1 -= 5;
                addText(" -> Ne pas jardiner entre " + h1.toString() + "h et " + ((Integer)(h1+10)).toString() + "h </font>");
            }
            else if ((perigeelocal.compareTo("19:00") >= 0))
            {
                h1 -= 5;
                addText(" -> Ne pas jardiner après " + h1.toString() + "h </font>");
            }
        }
    }
    private void testNoeud(Date d1, String ds)
    {
        Log.d("testnoeud", ds);
        if (!mapNoeud.get(ds).equals("0"))
        {
            String noeudlocal = heurelocale(mapNoeud.get(ds),d1 ,lh);
            addText("<font color=#ff0000>Nœud à " + noeudlocal);
            Integer h1 = Integer.parseInt(noeudlocal.substring(0,2));
            if (noeudlocal.compareTo("05:00") < 0)
            {
                h1 +=5;
                addText(" -> Ne pas jardiner avant " + h1.toString() + "h"  + "</font>");
            }
            else if ((noeudlocal.compareTo("19:00") < 0))
            {
                h1 -= 5;
                addText(" -> Ne pas jardiner entre " + h1.toString() + "h et " + ((Integer)(h1+10)).toString() + "h </font>");
            }
            else if ((noeudlocal.compareTo("19:00") >= 0))
            {
                h1 -= 5;
                addText(" -> Ne pas jardiner après " + h1.toString() + "h </font>");
            }
        }
    }
    private void BuildHTMLJour(String s1, String s2, String t1, String t2, String m1, String ht12, String hs12, String jour)
    {
        Log.d("BuildHTMLJour", s1);

        Log.d("d",  "<font color = #ffff00 face=\"sans-serif-light\">Date:" + jour + "</font>");
        if (!s1.equals("") && s2.equals(""))
        {
            if (!t1.equals("") && t2.equals(""))
            {
                addTextFromFile(m1 + "_" + s1 + "_" + t1 + "_fr.txt");
            }
            if (!t1.equals("") && !t2.equals(""))
            {
                addTextFromFile(m1 + "_" + s1 + "_" + t1 + "_fr.txt");
                addText("<font color=#ffff00>" + ht12 + "</font>");
                addTextFromFile(m1 + "_" + s1 + "_" + t2 + "_fr.txt");
            }
        }
        if (!s1.equals("") && !s2.equals(""))
        {
            if (!t1.equals("") && t2.equals(""))
            {
                addTextFromFile(m1 + "_" + s1 + "_" + t1 + "_fr.txt");
                addText("<font color=#ffff00>" +  hs12 + "</font>" );
                addTextFromFile(m1 + "_" + s2 + "_" + t1 + "_fr.txt");
            }
            if (!t1.equals("") && !t2.equals(""))
            {
                if (hs12.compareTo(ht12) < 0)
                {
                    addTextFromFile(m1 + "_" + s1 + "_" + t1 + "_fr.txt");
                    addText("<font color=#ffff00>" +  hs12 + "</font>" );
                    addTextFromFile(m1 + "_" + s2 + "_" + t1 + "_fr.txt");
                    System.out.println(m1 + "_" + s2 + "_" + t1 + "_fr.txt");
                    addText("<font color=#ffff00>" +  ht12  + "</font>");
                    addTextFromFile(m1 + "_" + s2 + "_" + t2 + "_fr.txt");
                }
                if (hs12.compareTo(ht12) > 0)
                {
                    addTextFromFile(m1 + "_" + s1 + "_" + t1 + "_fr.txt");
                    addText( "<font color=#ffff00>" +  ht12 + "</font>");
                    addTextFromFile(m1 + "_" + s1 + "_" + t2 + "_fr.txt");
                    addText("<font color=#ffff00>" +  hs12 + "</font>" );
                    addTextFromFile(m1 + "_" + s2 + "_" + t2 + "_fr.txt");
                }
                if (hs12.compareTo(ht12) == 0)
                {
                    addTextFromFile(m1 + "_" + s1 + "_" + t1 + "_fr.txt");
                    addText( "<font color=#ffff00>" +  hs12 + "</font>");
                    addTextFromFile(m1 + "_" + s2 + "_" + t2 + "_fr.txt");
                }
            }
        }
    }
    private void addTextFromFile(String s)
    {
        Log.d("addTextFromFile", s);

        try
        {
            BufferedReader br = new BufferedReader(new InputStreamReader(getAssets().open(s)));

            String st;

            while ((st = br.readLine()) != null)
            {
                System.out.println(st);
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
        Log.d("addText ", s);
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
            while (( line = buffreader.readLine()) != null)
            {
                //Log.d("Line",line);
                separated = line.split(";");
                //date1 = new Date(separated[0]);
                //Log.d("Date :", date1.toString());
                //textLever.setText(separated[1]);
                mapLever.put(separated[0],separated[1]);
                mapCoucher.put(separated[0],separated[2]);
                mapPhase.put(separated[0],separated[3]);
                mapJour.put(separated[0],separated[4]);
                mapApogee.put(separated[0],separated[5]);
                mapPerigee.put(separated[0],separated[6]);
                mapNoeud.put(separated[0],separated[7]);
                mapComment.put(separated[0],separated[8]);
                mapMontant.put(separated[0],separated[9]);
                mapCroissant.put(separated[0],separated[10]);
                mapSigne.put(separated[0],separated[11]);
                mapConseil.put(separated[0],separated[12]);
                mapEclair.put(separated[0],separated[13]);
            }
        } catch (IOException e) {
            Log.e("Lunoid", "exception", e);
        }
    }
    public static String getLever()
    {
          return heurelocale(mapLever.get(dateString),date1,lh);
    }
    public static String getCoucher()
    {
          return heurelocale(mapCoucher.get(dateString),date1,lh);
    }

    public void datePicker(View view)
    {

        DatePickerFragment fragment = new DatePickerFragment();
        fragment.show(getSupportFragmentManager(), "date");
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day)
    {
        Calendar cal = new GregorianCalendar(year, month, day);
        Log.d("onDateSet", "");
        setDate(cal);
        onDateChange(year, month, day);
        datePicker1.updateDate(year, month, day);
    }

    private void setDate(final Calendar calendar)
    {
        final DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG);

        ((TextView) findViewById(R.id.TextJour)).setText(dateFormat.format(calendar.getTime()));
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

            if (y >= 2019 && (mo >= 2 && mo <= 8)) {
                item.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
            } else {
                item.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_NEVER);
            }
        }
        catch (Exception e)
        {

        }
    }

} // Lunoid

