package com.nialon;

import android.util.Log;

public class astro {
    static double INVERSE_SIDEREAL_DAY = 1.002737909350795;
    static double D2R = Math.PI / 180.0;
    static double R2D = 180.0 / Math.PI;
    static double PI = Math.PI;
    static double PIx2 = 2.0 * Math.PI;
    static double[] Rise_time = new double[2];
    static double[] Set_time = new double[2];
    static double[] Sky = new double[3];
    static double[] RAn = new double[3];
    static double[] Decl = new double[3];
    static boolean Moonrise = false;
    static boolean Moonset = false;
    static double K1 = 15.0 * D2R * INVERSE_SIDEREAL_DAY;
    static double[] VHz = new double[3];
    static double Rise_az = 0.0;
    static double Set_az = 0.0;

    public static String moonRiseTime(double lat, double lon, int year, int month, int day) {
        double zone;
        double jd;
        zone = Math.round(0.0 / 60.0);       // UTC
        jd = julian_day(year, month, day) - 2451545.0;   // Julian day relative to Jan 1, 2000 at noon
        double[][] mp = new double[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++)
                mp[i][j] = 0.0;
        }
        lon /= 360.0;
        double tz = zone / 24;
        double t0 = lst(lon, jd, tz);        // Local sidereal time
        jd += tz;                         // Moon position at start of day
        Log.d("jd", Double.toString(jd));
        for (int k = 0; k < 3; k++) {
            moon(jd);
            mp[k][0] = Sky[0];
            mp[k][1] = Sky[1];
            mp[k][2] = Sky[2];
            jd += 0.5;
        }

        Log.d("jd", Double.toString(jd));
        if (mp[1][0] <= mp[0][0])
            mp[1][0] += PIx2;
        if (mp[2][0] <= mp[1][0])
            mp[2][0] += PIx2;
        RAn[0] = mp[0][0];
        Decl[0] = mp[0][1];
        Moonrise = false;
        Moonset = false;

        Log.d("t0", Double.toString(t0));
        Log.d("lat", Double.toString(lat));

        Log.d("mp00", Double.toString(mp[0][0]));
        Log.d("mp01", Double.toString(mp[0][1]));
        Log.d("mp02", Double.toString(mp[0][2]));
        Log.d("mp10", Double.toString(mp[1][0]));
        Log.d("mp11", Double.toString(mp[1][1]));
        Log.d("mp12", Double.toString(mp[1][2]));
        Log.d("mp20", Double.toString(mp[2][0]));
        Log.d("mp21", Double.toString(mp[2][1]));
        Log.d("mp22", Double.toString(mp[2][2]));
        for (int k = 0; k < 24; k++)      // Check each hour of this day
        {
            double ph = (k + 1) / 24.0;

            RAn[2] = interpolate(mp[0][0], mp[1][0], mp[2][0], ph);
            Decl[2] = interpolate(mp[0][1], mp[1][1], mp[2][1], ph);

            VHz[2] = test_moon(k, t0, lat, mp[1][2]);
            RAn[0] = RAn[2];             // Advance to next hour
            Decl[0] = Decl[2];
            VHz[0] = VHz[2];
        }

        return zero((int) Rise_time[0]) + "h" + zero((int) Rise_time[1]) + "m";
    }

    public static String moonSetTime(double lat, double lon, int year, int month, int day) {
        double zone = Math.round(0.0 / 60.0);       // UTC
        double jd = julian_day(year, month, day) - 2451545.0;   // Julian day relative to Jan 1, 2000 at noon
        double[][] mp = new double[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++)
                mp[i][j] = 0.0;
        }
        lon /= 360.0;
        double tz = zone / 24;
        double t0 = lst(lon, jd, tz);        // Local sidereal time
        jd += tz;                         // Moon position at start of day
        for (int k = 0; k < 3; k++) {
            moon(jd);
            mp[k][0] = Sky[0];
            mp[k][1] = Sky[1];
            mp[k][2] = Sky[2];
            jd += 0.5;
        }
        if (mp[1][0] <= mp[0][0])
            mp[1][0] += PIx2;
        if (mp[2][0] <= mp[1][0])
            mp[2][0] += PIx2;
        RAn[0] = mp[0][0];
        Decl[0] = mp[0][1];
        Moonrise = false;
        Moonset = false;

        for (int k = 0; k < 24; k++)      // Check each hour of this day
        {
            double ph = (k + 1.0) / 24.0;

            RAn[2] = interpolate(mp[0][0], mp[1][0], mp[2][0], ph);
            Decl[2] = interpolate(mp[0][1], mp[1][1], mp[2][1], ph);

            VHz[2] = test_moon(k, t0, lat, mp[1][2]);
            RAn[0] = RAn[2];             // Advance to next hour
            Decl[0] = Decl[2];
            VHz[0] = VHz[2];
        }

        return zero((int) Set_time[0]) + "h" + zero((int) Set_time[1]) + "m";
    }

    // Returns value for sign of argument
    private static int sgn(double x) {

        return Double.compare(x, 0.0);
    }

    // Determine the Julian day from the calendar date (Jean Meeus, "Astronomical Algorithms", Willmann-Bell, 1991)
    private static double julian_day(int year, int month, int day) {
        double y, m, a, b, jd;

        boolean gregorian = true;
        if (year < 1582)
            gregorian = false;
        else if (year == 1582) {
            if ((month < 10) || ((month == 10) && (day < 15)))
                gregorian = false;
        }
        if (month > 2) {
            y = year;
            m = month;
        } else {
            y = year - 1;
            m = month + 12;
        }

        a = Math.floor(y / 100);
        if (gregorian)
            b = 2 - a + Math.floor(a / 4);
        else
            b = 0.0;
        jd = Math.floor(365.25 * (y + 4716)) + Math.floor(30.6001 * (m + 1)) + day + b - 1524.5;

        return jd;
    }

    private static String zero(int value) {
        String val;

        if (value < 10) {
            if (value < 0)
                val = "-0" + Math.abs(value);
            else
                val = "0" + value;
        } else
            val = "" + value;
        return (val.replace('.', ','));
    }

    // Local Sidereal Time
    private static double lst(double lon, double jd2000, double z) {
        double t = jd2000 / 36525.0;
        double s = 24110.54841 + (t * (8640184.812866 + (t * (0.093104 - (t * 0.0000062))))) + (86400.0 * ((INVERSE_SIDEREAL_DAY * z) + lon));
        s /= 86400;
        s -= Math.floor(s);

        return s * 360 * D2R;
    }

    private static void moon(double jd) {
        double d, f, g, h, m, n, s, u, v, w;

        h = 0.606434 + 0.03660110129 * jd;
        m = 0.374897 + 0.03629164709 * jd;
        f = 0.259091 + 0.0367481952 * jd;
        d = 0.827362 + 0.03386319198 * jd;
        n = 0.347343 - 0.00014709391 * jd;
        g = 0.993126 + 0.0027377785 * jd;
        h -= Math.floor(h);
        m -= Math.floor(m);
        f -= Math.floor(f);
        d -= Math.floor(d);
        n -= Math.floor(n);
        g -= Math.floor(g);
        h *= PIx2;
        m *= PIx2;
        f *= PIx2;
        d *= PIx2;
        n *= PIx2;
        g *= PIx2;
        v = 0.39558 * Math.sin(f + n);
        v += 0.082 * Math.sin(f);
        v += 0.03257 * Math.sin(m - f - n);
        v += 0.01092 * Math.sin(m + f + n);
        v += 0.00666 * Math.sin(m - f);
        v -= 0.00644 * Math.sin(m + f - 2 * d + n);
        v -= 0.00331 * Math.sin(f - 2 * d + n);
        v -= 0.00304 * Math.sin(f - 2 * d);
        v -= 0.0024 * Math.sin(m - f - 2 * d - n);
        v += 0.00226 * Math.sin(m + f);
        v -= 0.00108 * Math.sin(m + f - 2 * d);
        v -= 0.00079 * Math.sin(f - n);
        v += 0.00078 * Math.sin(f + 2 * d + n);

        u = 1 - 0.10828 * Math.cos(m);
        u -= 0.0188 * Math.cos(m - 2 * d);
        u -= 0.01479 * Math.cos(2 * d);
        u += 0.00181 * Math.cos(2 * m - 2 * d);
        u -= 0.00147 * Math.cos(2 * m);
        u -= 0.00105 * Math.cos(2 * d - g);
        u -= 0.00075 * Math.cos(m - 2 * d + g);

        w = 0.10478 * Math.sin(m);
        w -= 0.04105 * Math.sin(2 * f + 2 * n);
        w -= 0.0213 * Math.sin(m - 2 * d);
        w -= 0.01779 * Math.sin(2 * f + n);
        w += 0.01774 * Math.sin(n);
        w += 0.00987 * Math.sin(2 * d);
        w -= 0.00338 * Math.sin(m - 2 * f - 2 * n);
        w -= 0.00309 * Math.sin(g);
        w -= 0.0019 * Math.sin(2 * f);
        w -= 0.00144 * Math.sin(m + n);
        w -= 0.00144 * Math.sin(m - 2 * f - n);
        w -= 0.00113 * Math.sin(m + 2 * f + 2 * n);
        w -= 0.00094 * Math.sin(m - 2 * d + g);
        w -= 0.00092 * Math.sin(2 * m - 2 * d);
        s = w / Math.sqrt(u - (v * v));          // Moon's right ascension
        Log.d("h", Double.toString(h));
        Log.d("s", Double.toString(s));
        Sky[0] = h + Math.atan(s / Math.sqrt(1 - (s * s)));
        Log.d("sky0", Double.toString(Sky[0]));
        s = v / Math.sqrt(u);                    // Moon's declination
        Sky[1] = Math.atan(s / Math.sqrt(1 - (s * s)));
        Sky[2] = 60.40974 * Math.sqrt(u);        // Moon's parallax
        Log.d("sky1", Double.toString(Sky[1]));
        Log.d("sky2", Double.toString(Sky[2]));
    }

    // 3-point interpolation
    private static double interpolate(double f0, double f1, double f2, double p) {
        double a = f1 - f0;
        double b = f2 - f1 - a;

        return f0 + p * ((2 * a) + (b * ((2 * p) - 1)));
    }

    // Test an hour for an event
    private static double test_moon(int k, double t0, double lat, double plx) {
        double[] ha = new double[3];
        double a, b, d, e, z;
        double min, time;
        double az, hz, nz, dz;

        if (RAn[2] < RAn[0])
            RAn[2] += PIx2;

        ha[0] = t0 - RAn[0] + (k * K1);
        ha[2] = t0 - RAn[2] + (k * K1) + K1;

        ha[1] = (ha[2] + ha[0]) / 2.0;          // Hour angle at half hour

        if (k == 12) {
            Log.d("ha0 ", Double.toString(ha[0]) + " " + Double.toString(ha[1]) + " " + Double.toString(ha[2]));
            Log.d("RAn0", Double.toString(RAn[0]));
            Log.d("RAn1", Double.toString(RAn[1]));
            Log.d("RAn2", Double.toString(RAn[2]));
            Log.d("t0", Double.toString(t0));
            Log.d("k", Integer.toString(k));

        }

        Decl[1] = (Decl[2] + Decl[0]) / 2;        // Declination at half hour
        double s = Math.sin(D2R * lat);
        double c = Math.cos(D2R * lat);
        // Refraction + sun semidiameter at horizon + parallax correction
        z = Math.cos(D2R * (90.567 - (41.685 / plx)));
        if (k == 12) {
            Log.d("z", Double.toString(z));
            Log.d("s", Double.toString(s));
            Log.d("c", Double.toString(c));
            Log.d("decl0", Double.toString(Decl[0]));
            Log.d("decl1", Double.toString(Decl[1]));
            Log.d("decl2", Double.toString(Decl[2]));
        }
        if (k <= 0)                         // First call
            VHz[0] = (s * Math.sin(Decl[0])) + (c * Math.cos(Decl[0]) * Math.cos(ha[0])) - z;
        VHz[2] = (s * Math.sin(Decl[2])) + (c * Math.cos(Decl[2]) * Math.cos(ha[2])) - z;

        if (sgn(VHz[0]) == sgn(VHz[2]))
            return VHz[2];                      // No event this hour

        VHz[1] = (s * Math.sin(Decl[1])) + (c * Math.cos(Decl[1]) * Math.cos(ha[1])) - z;

        a = (2 * VHz[2]) - (4 * VHz[1]) + (2 * VHz[0]);
        b = (4 * VHz[1]) - (3 * VHz[0]) - VHz[2];
        if (k == 12) {
            Log.d("a", Double.toString(a));
            Log.d("b", Double.toString(b));
        }
        d = (b * b) - (4 * a * VHz[0]);
        if (d < 0)
            return VHz[2];                      // No event this hour

        d = Math.sqrt(d);
        e = (-b + d) / (2 * a);
        if ((e > 1) || (e < 0))
            e = (-b - d) / (2 * a);
        time = k + e + (1.0 / 120.0);             // Time of an event + round up
        double hr = Math.floor(time);
        min = Math.floor((time - hr) * 60);
        hz = ha[0] + (e * (ha[2] - ha[0]));   // Azimuth of the moon at the event
        nz = -Math.cos(Decl[1]) * Math.sin(hz);
        dz = (c * Math.sin(Decl[1])) - (s * Math.cos(Decl[1]) * Math.cos(hz));
        az = Math.atan2(nz, dz) * R2D;
        if (az < 0)
            az += 360;
        if (k == 12) {
            Log.d("k ", Integer.toString(k) + " " + Double.toString(VHz[0]) + " " + Double.toString(VHz[2]));
            Log.d("t0 ", Double.toString(t0) + " " + Double.toString(lat) + " " + Double.toString(plx));
        }

        if ((VHz[0] < 0) && (VHz[2] > 0)) {
            Rise_time[0] = hr;
            Rise_time[1] = min;
            Rise_az = az;
            Moonrise = true;
        }

        if ((VHz[0] > 0) && (VHz[2] < 0)) {
            Set_time[0] = hr;
            Set_time[1] = min;
            Set_az = az;
            Moonset = true;
        }

        return VHz[2];
    }
}
