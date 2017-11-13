package com.nialon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import android.app.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Layout;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.ImageView;

import android.widget.TextView;
import android.widget.Toast;
import android.view.MotionEvent;

import android.os.Build;


// TODO : widget
// TODO : possibilité de sauvegarder un mémo par jour

public class Lunoid extends Activity 
{
    /** Called when the activity is first created. */
    private SimpleDateFormat sdf;
    private SimpleDateFormat sdf2;
    static String dateString ;
    String dateString2 ;
    private static Date date1;
    private Date dateDebut;
    private Date dateFin;
    private GestureDetector mGestureDetector;
    private static Boolean lh;
    public final static String EXTRA_MESSAGE = "com.nialon.Lunoid.MESSAGE";

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
    Layout lay;

    OnDateChangedListener listener = new OnDateChangedListener()
    {
        @Override
        public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            Log.d("Lunoid","ondatechanged");
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
                Log.d("datestring",dateString);

                textCoucher.setText(heurelocale(mapCoucher.get(dateString),date1,lh));
                textLever.setText(heurelocale(mapLever.get(dateString),date1,lh));

                //imgLune.setImageBitmap(mapPhase.get(dateString));
                dateString2 = sdf2.format(date1);
                Log.d("datestring2",dateString2);
                textJour.setText(dateString2);

                String ecl = mapEclair.get(dateString);
                if (ecl.equals("-"))
                {
                    String ecl2 = "lune0";
                    String ecl3;
                    int ph = Integer.parseInt(mapPhase.get(dateString));
                    ecl3 = ecl2.concat(String.format("%02d", ph));
                    Log.d("ecl3", ecl3);
                    int resId = getResources().getIdentifier(ecl3, "drawable", getPackageName());
                    Log.d("resid", Integer.toString(resId));
                    imgLune.setImageResource(resId);
                    if (textPct != null)
                    {
                        textPct.setText("");
                    }
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
                    int resId = getResources().getIdentifier(ecl3, "drawable", getPackageName());
                    Log.d("resid", Integer.toString(resId));
                    imgLune.setImageResource(resId);
                    if (textPct != null)
                    {
                        textPct.setText(ecl.concat(" %"));
                    }
                }

                // Fruits/Fleurs/Feuilles/Racines
                textFeuilles.setTextColor(mapJour.get(dateString).contains("Feuilles") ? Color.YELLOW : Color.LTGRAY);
                textFleurs.setTextColor(mapJour.get(dateString).contains("Fleurs") ? Color.YELLOW : Color.LTGRAY);
                textFruits.setTextColor(mapJour.get(dateString).contains("Fruits") ? Color.YELLOW : Color.LTGRAY);
                textRacines.setTextColor(mapJour.get(dateString).contains("Racines") ? Color.YELLOW : Color.LTGRAY);

                // Apogee/Perigee/Noeud
                textApogee.setTextColor(!mapApogee.get(dateString).equals("0") ? Color.MAGENTA : Color.LTGRAY);
                textPerigee.setTextColor(!mapPerigee.get(dateString).equals("0") ? Color.MAGENTA : Color.LTGRAY);
                textNoeud.setTextColor(!mapNoeud.get(dateString).equals("0") ? Color.MAGENTA : Color.LTGRAY);

                try
                {
                    textApogeeHour.setTextColor(!mapApogee.get(dateString).equals("0") ? Color.MAGENTA : Color.LTGRAY);
                    textApogeeHour.setText(!mapApogee.get(dateString).equals("0") ? (mapApogee.get(dateString).equals("88:88")? "--:--" : heurelocale(mapApogee.get(dateString),date1,lh)) : "--:--");

                    textPerigeeHour.setTextColor(!mapPerigee.get(dateString).equals("0") ? Color.MAGENTA : Color.LTGRAY);
                    textPerigeeHour.setText(!mapPerigee.get(dateString).equals("0") ? (mapPerigee.get(dateString).equals("88:88")? "--:--" : heurelocale(mapPerigee.get(dateString),date1,lh)) : "--:--");

                    textNoeudHour.setTextColor(!mapNoeud.get(dateString).equals("0") ? Color.MAGENTA : Color.LTGRAY);
                    textNoeudHour.setText(!mapNoeud.get(dateString).equals("0") ? (mapNoeud.get(dateString).equals("88:88")? "--:--" : heurelocale(mapNoeud.get(dateString).substring(0,5),date1,lh)) : "--:--");
                }
                catch (Exception e)
                {

                }
                // Montant/Descendant
                textMontant.setTextColor(mapMontant.get(dateString).equals("1") ? Color.YELLOW : mapMontant.get(dateString).equals("2") ? Color.YELLOW : Color.LTGRAY);
                textDescendant.setTextColor(mapMontant.get(dateString).equals("0") ? Color.YELLOW : mapMontant.get(dateString).equals("2") ? Color.YELLOW :Color.LTGRAY);

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
                try
                {
                    textPerigeeHour.setTextColor(Color.LTGRAY);
                    textPerigee.setTextColor(Color.LTGRAY);
                    textNoeudHour.setTextColor(Color.LTGRAY);
                }
                catch (Exception e)
                {

                }
                textNoeud.setTextColor(Color.LTGRAY);
            }
        } // onDateChanged
    }; // onDateChangeListener
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //setContentView(R.layout.main2);

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
        dateFin.setYear(2019 - 1900);

        sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf2 = new SimpleDateFormat("EEEE dd MMMM yyyy");

        date1= new Date(datePicker1.getYear()-1900, datePicker1.getMonth(), datePicker1.getDayOfMonth());

        datePicker1.init(datePicker1.getYear(), datePicker1.getMonth(), datePicker1.getDayOfMonth(),	listener);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            datePicker1.setCalendarViewShown(false);
            datePicker1.setSpinnersShown(true);
        }

        textJour.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                Date today = Calendar.getInstance().getTime();
                datePicker1.updateDate(today.getYear()+1900,today.getMonth(),today.getDate());
            }
        });

        textFruits.setOnClickListener(new View.OnClickListener()
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
        textRacines.setOnClickListener(new View.OnClickListener()
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
            datePicker1.updateDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        else
            Toast.makeText(getApplicationContext(), R.string.notfound, Toast.LENGTH_SHORT).show();
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
        String message;
        Log.d("y", Integer.toString(year));
        Log.d("m", Integer.toString(month));
        if ((year >= 2016) && (year <=2017)) {
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
            else {
                message = "nodata";
            }
        }
        else
        {
            message = "nodata";
        }
        Log.d("m", message);
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }
    private void read_data()
    {
        Date date1;
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
        } catch (IOException e) {}
    }
    public static String getLever()
    {
          return heurelocale(mapLever.get(dateString),date1,lh);
    }
    public static String getCoucher()
    {
          return heurelocale(mapCoucher.get(dateString),date1,lh);
    }
} // Lunoid

