package com.nialon;

import android.app.Dialog;
import android.os.Bundle;
import android.app.DatePickerDialog;
import android.widget.DatePicker;
import java.util.Calendar;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class DatePickerFragment
        extends DialogFragment
        implements DatePickerDialog.OnDateSetListener
{
    @Override
    @NonNull
    public Dialog onCreateDialog( Bundle savedInstanceState)
    {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        DatePickerDialog dialog = new DatePickerDialog(getActivity(),android.R.style.Theme_DeviceDefault_Dialog, (DatePickerDialog.OnDateSetListener)getActivity(), year, month, day);

        c.set(Lunoid.calMax.get(Calendar.YEAR), Lunoid.calMax.get(Calendar.MONTH), Lunoid.calMax.get(Calendar.DAY_OF_MONTH));
        dialog.getDatePicker().setMaxDate(c.getTimeInMillis());

        c.set(Lunoid.calMin.get(Calendar.YEAR), Lunoid.calMin.get(Calendar.MONTH), Lunoid.calMin.get(Calendar.DAY_OF_MONTH));
        dialog.getDatePicker().setMinDate(c.getTimeInMillis());
        return dialog;
    }

    public void onDateSet(DatePicker view, int year, int month, int day)
    {
        // Do something with the date chosen by the user
    }
}
