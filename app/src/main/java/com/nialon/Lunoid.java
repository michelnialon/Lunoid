package com.nialon;

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

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.text.Layout;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.ImageView;

import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.MotionEvent;

import android.os.Build;

// ads
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;

import static android.icu.lang.UCharacter.toLowerCase;


// TODO : widget : fait
// TODO : possibilité de sauvegarder un mémo par jour
// TODO : pub : fait
// TODO : signes du zodiaque

public class Lunoid extends FragmentActivity implements DatePickerDialog.OnDateSetListener
{
    /** Called when the activity is first created. */
    private SimpleDateFormat sdf;
    private SimpleDateFormat sdf2;
    static String dateString;
    String dateString2 ;
    private static Date date1;
    private Date dateDebut;
    private Date dateFin;
    private GestureDetector mGestureDetector;
    private static Boolean lh;
    public final static String EXTRA_MESSAGE = "com.nialon.Lunoid.MESSAGE";
    // ads
    private InterstitialAd mInterstitialAd;

    static Map<String, String> mapLever = new HashMap<String, String>();
    static Map<String, String> mapCoucher = new HashMap<String, String>();
    Map<String, String> mapPhase = new HashMap<String, String>();
    Map<String, String> mapJour = new HashMap<String, String>();
    Map<String, String> mapApogee = new HashMap<String, String>();
    Map<String, String> mapPerigee = new HashMap<String, String>();
    Map<String, String> mapNoeud = new HashMap<String, String>();
    Map<String, String> mapComment = new HashMap<String, String>();
    Map<String, String> mapCroissant = new HashMap<String, String>();
    Map<String, String> mapMontant = new HashMap<String, String>();
    Map<String, String> mapQuartier= new HashMap<String, String>();
    Map<String, String> mapSigne= new HashMap<String, String>();
    Map<String, String> mapConseil= new HashMap<String, String>();
    Map<String, String> mapEclair= new HashMap<String, String>();

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
    Layout lay;

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
                    ecl3 = ecl2.concat(String.format("%02d", ph));
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
            textFeuilles.setTextColor(mapJour.get(dateString).contains("Feuilles") ? Color.YELLOW : Color.LTGRAY);
            textFleurs.setTextColor(mapJour.get(dateString).contains("Fleurs") ? Color.YELLOW : Color.LTGRAY);
            textFruits.setTextColor(mapJour.get(dateString).contains("Fruits") ? Color.YELLOW : Color.LTGRAY);
            textRacines.setTextColor(mapJour.get(dateString).contains("Racines") ? Color.YELLOW : Color.LTGRAY);
            resId = getResources().getIdentifier(mapJour.get(dateString).contains("Racines") ? "racine30x30" : "racine30x30_grey", "drawable", getPackageName());
            imageRacine.setImageResource(resId);
            resId = getResources().getIdentifier(mapJour.get(dateString).contains("Fleurs") ? "fleur30x30" : "fleur30x30_grey", "drawable", getPackageName());
            imageFleur.setImageResource(resId);
            resId = getResources().getIdentifier(mapJour.get(dateString).contains("Feuilles") ? "feuille30x30" : "feuille30x30_grey", "drawable", getPackageName());
            imageFeuille.setImageResource(resId);
            resId = getResources().getIdentifier(mapJour.get(dateString).contains("Fruits") ? "fruit30x30" : "fruit30x30_grey", "drawable", getPackageName());
            imageFruit.setImageResource(resId);

            // Apogee/Perigee/Noeud
            textApogee.setTextColor(!mapApogee.get(dateString).equals("0") ? Color.RED : Color.LTGRAY);
            textPerigee.setTextColor(!mapPerigee.get(dateString).equals("0") ? Color.RED : Color.LTGRAY);
            textNoeud.setTextColor(!mapNoeud.get(dateString).equals("0") ? Color.RED : Color.LTGRAY);

            try
            {
                textApogeeHour.setTextColor(!mapApogee.get(dateString).equals("0") ? Color.RED : Color.LTGRAY);
                textApogeeHour.setText(!mapApogee.get(dateString).equals("0") ? (mapApogee.get(dateString).equals("88:88")? "--:--" : heurelocale(mapApogee.get(dateString),date1,lh)) : "--:--");

                textPerigeeHour.setTextColor(!mapPerigee.get(dateString).equals("0") ? Color.RED : Color.LTGRAY);
                textPerigeeHour.setText(!mapPerigee.get(dateString).equals("0") ? (mapPerigee.get(dateString).equals("88:88")? "--:--" : heurelocale(mapPerigee.get(dateString),date1,lh)) : "--:--");

                textNoeudHour.setTextColor(!mapNoeud.get(dateString).equals("0") ? Color.RED : Color.LTGRAY);
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
                textMontant.setTextColor(Color.LTGRAY);
            }
            if (mapMontant.get(dateString).equals("0") || mapMontant.get(dateString).startsWith("2") || mapMontant.get(dateString).startsWith("3")) {
                textDescendant.setTextColor(Color.YELLOW);
            }
            else {
                textDescendant.setTextColor(Color.LTGRAY);
            }

            // Croissant/Decroissant
            textCroissant.setTextColor(mapCroissant.get(dateString).equals("1") ? Color.YELLOW : mapCroissant.get(dateString).equals("2") ? Color.YELLOW : Color.LTGRAY);
            textDecroissant.setTextColor(mapCroissant.get(dateString).equals("0") ? Color.YELLOW : mapCroissant.get(dateString).equals("2") ? Color.YELLOW : Color.LTGRAY);
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
        PublisherAdView mPublisherAdView = (PublisherAdView) findViewById(R.id.publisherAdView);
        PublisherAdRequest adRequest = new PublisherAdRequest.Builder().build();
        mPublisherAdView.setAdSizes(AdSize.BANNER);
        //mPublisherAdView.setAdUnitId("ca-app-pub-4468029712209847/4219671648"); // prod ( à mettre dans le manifest )
        //mPublisherAdView.setAdUnitId("ca-app-pub-3940256099942544/6300978111"); // test
        mPublisherAdView.loadAd(adRequest);

        //final Context context = getApplicationContext();
        //SharedPreferences prefs = this.getSharedPreferences("com.nialon",Context.MODE_PRIVATE);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        lh = prefs.getBoolean("timezone", false);

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
        /** Format title */
        TextView title = (TextView) findViewById(android.R.id.title);
        //title.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
        //title.setTextColor(Color.YELLOW);
        //title.setTextSize(20);

        /** Get the id of views */
        datePicker1 = (DatePicker)findViewById(R.id.datePicker1);
        textLever = (TextView) findViewById(R.id.textLever);
        textCoucher = (TextView) findViewById(R.id.textCoucher);
        textFruits = (TextView) findViewById(R.id.textFruits);
        textFleurs = (TextView) findViewById(R.id.textFleurs);
        textFeuilles = (TextView) findViewById(R.id.textFeuilles);
        textRacines = (TextView) findViewById(R.id.textRacines);
        textApogee = (TextView) findViewById(R.id.textApogee);
        textPerigee = (TextView) findViewById(R.id.textPerigee);
        textPerigeeHour = (TextView) findViewById(R.id.textPerigeeHour);
        textApogeeHour = (TextView) findViewById(R.id.textApogeeHour);
        textNoeudHour = (TextView) findViewById(R.id.textNoeudHour);
        textNoeud = (TextView) findViewById(R.id.textNoeud);
        textCroissant = (TextView) findViewById(R.id.textCroissant);
        textDecroissant = (TextView) findViewById(R.id.textDecroissant);
        textMontant = (TextView) findViewById(R.id.textMontant);
        textDescendant = (TextView) findViewById(R.id.textDescendant);
        textJour = (TextView) findViewById(R.id.TextJour);
        textPct = (TextView) findViewById(R.id.textPct);
        imgLune = (ImageView)findViewById(R.id.imageLune);
        imageFeuille = (ImageView)findViewById(R.id.imageFeuille);
        imageFleur = (ImageView) findViewById(R.id.imageFleur);
        imageFruit = (ImageView) findViewById(R.id.imageFruit);
        imageRacine = (ImageView) findViewById(R.id.imageRacine);

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

        /** read data from csv text file */
        read_data();

        dateDebut = new Date();
        dateFin = new Date();
        dateDebut.setDate(1); // du 01/05/2012
        dateDebut.setMonth(4);
        dateDebut.setYear(2012 - 1900);
        dateFin.setDate(1);   // au 31/12/2013
        dateFin.setMonth(0);
        dateFin.setYear(2020 - 1900);

        sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf2 = new SimpleDateFormat("EEEE dd MMMM yyyy");

        date1= new Date(datePicker1.getYear()-1900, datePicker1.getMonth(), datePicker1.getDayOfMonth());

        datePicker1.init(datePicker1.getYear(), datePicker1.getMonth(), datePicker1.getDayOfMonth(),	listener);
        // HoneyComb = 3
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
        {
            datePicker1.setCalendarViewShown(false);
            datePicker1.setSpinnersShown(true);
        }

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
        case R.id.item4:
            intent = new Intent(new Intent(Intent.ACTION_VIEW,Uri.parse("https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=U6CX3ZNDSYFMQ")));
            startActivity(intent);
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
//        ScrollView layout = (ScrollView) findViewById(R.layout.conseilsdumois);
//        layout.setBackgroundColor(Color.BLACK);
        intent = new Intent(this, ConseilsDuMois.class);
        String message = "";
        String message2= "";
        String tableauMois[] = {"janvier", "fevrier", "mars" , "avril","mai", "juin", "juillet" , "aout", "septembre", "octobre", "novembre" , "decembre"};

        Log.d("y", Integer.toString(year));
        Log.d("m", Integer.toString(month));

        if ((year >= 2016) && (year <=2018))
        {
            message2 = "1";
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
                message = "Pas d'informations pour cette période";
                message2 = "0";
            }
            if (message2.equals("1"))
            {
                Log.d("message", message);
                intent.putExtra(EXTRA_MESSAGE, message);
                intent.putExtra("type_message", message2);
                startActivity(intent);
            }
        }
        else if (year >= 2019)
        {
            String hs12;
            String ht12;
            String m1;
            String s1;
            String s2;
            String t1;
            String t2;

            if (month >= 2 && month <= 7)
            {
                hs12 = "";
                s1 = "";
                s2 = "";
                m1 = tableauMois[month];
                Log.d("montant", mapMontant.get(dateString).toLowerCase());
                Log.d("jour", mapJour.get(dateString).toLowerCase());
                Log.d("taille", Integer.toString(mapMontant.get(dateString).toLowerCase().length()));
               // Log.d("sub", mapMontant.get(dateString).toLowerCase().substring(0,13));
                if (mapMontant.get(dateString).substring(0,1).equals("0"))
                {
                    s1 = "descendante";
                    s2 = "";
                }
                if (mapMontant.get(dateString).substring(0,1).equals("1"))
                {
                    s1 = "montante";
                    s2 = "";
                }
                if (mapMontant.get(dateString).substring(0,1).equals("2"))
                {
                    s1 = "montante";
                    s2 = "descendante";
                    hs12 = mapMontant.get(dateString).substring(2,7);
                }
                if (mapMontant.get(dateString).substring(0,1).equals("3"))
                {
                    s1 = "descendante";
                    s2 = "montante";
                    hs12 = mapMontant.get(dateString).substring(2,7);
                }
                if (mapJour.get(dateString).length() >= 15 &&  (mapJour.get(dateString)).toLowerCase().substring(0,15).equals("feuilles/fruits"))
                {
                    t1 = "feuilles";
                    t2 =  "fruits";
                    ht12 = mapJour.get(dateString).substring(15,21);
                }
                else if (mapJour.get(dateString).length() >= 14 && (mapJour.get(dateString)).toLowerCase().substring(0,14).equals("fruits/racines"))
                {
                    t1 = "fruits";
                    t2 = "racines";
                    ht12 = mapJour.get(dateString).substring(14,20);
                }
                else if (mapJour.get(dateString).length() >= 14 && (mapJour.get(dateString)).toLowerCase().substring(0,14).equals("racines/fleurs"))
                {
                    t1 = "racines";
                    t2 = "fleurs";
                    ht12 = mapJour.get(dateString).substring(14,20);
                }
                else if (mapJour.get(dateString).length() >= 15 && (mapJour.get(dateString)).toLowerCase().substring(0,15).equals("fleurs/feuilles"))
                {
                    t1 = "fleurs";
                    t2 = "feuilles";
                    ht12 = mapJour.get(dateString).substring(15,21);
                }
                else
                {
                    t1 = mapJour.get(dateString).toLowerCase();
                    t2 = "";
                    ht12 = "";
                }
                message2 = "2";
                message += tableauMois[month] + "_";
                message += mapMontant.get(dateString).equals("1") ? "montante_" : "descendante_";
                message += (mapJour.get(dateString)).toLowerCase();
                message += "_fr.txt";
                Log.d("message", message);
                intent.putExtra(EXTRA_MESSAGE, message);
                intent.putExtra("hs12", hs12);
                intent.putExtra("ht12", ht12);
                intent.putExtra("m1", m1);
                intent.putExtra("s1", s1);
                intent.putExtra("s2", s2);
                intent.putExtra("t1", t1);
                intent.putExtra("t2", t2);
                intent.putExtra("type_message", message2);

                startActivity(intent);
            }
            else
            {
                message = "Pas d'informations pour cette période";
                message2 = "0";
            }
        }
        else
        {
            message = "Pas d'informations pour cette période";
            message2 = "0";
        }
        if (message2.equals("0"))
        {
            Toast.makeText(getApplicationContext(), "Pas de données pour ce jour", Toast.LENGTH_SHORT).show();
        }

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

} // Lunoid

