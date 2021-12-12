package com.nialon;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class LocalHelp extends AppCompatActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.localhelp);

    }

    public void prevHelp(View view) {
        Log.d("prevHelp", view.toString());
    }
}