package com.nialon;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.app.DatePickerDialog;
import android.widget.DatePicker;
import java.util.Date;

import java.util.Calendar;

public class DatePickerFragment
        extends DialogFragment
        implements DatePickerDialog.OnDateSetListener
{
    @Override
    @NonNull
    public Dialog onCreateDialog( Bundle savedInstanceState)
    {
        Date dateDebutFin;

        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        DatePickerDialog dialog = new DatePickerDialog(getActivity(),android.R.style.Theme_Holo_Dialog, (DatePickerDialog.OnDateSetListener)getActivity(), year, month, day);
        dateDebutFin = new Date();
        dateDebutFin.setDate(31);
        dateDebutFin.setMonth(11);
        dateDebutFin.setYear(2020 - 1900);
        dialog.getDatePicker().setMaxDate(dateDebutFin.getTime());
        dateDebutFin.setDate(1);
        dateDebutFin.setMonth(4);
        dateDebutFin.setYear(2012 - 1900);
        dialog.getDatePicker().setMinDate(dateDebutFin.getTime());

        return dialog;
    }

    public void onDateSet(DatePicker view, int year, int month, int day)
    {
        // Do something with the date chosen by the user
    }
}
