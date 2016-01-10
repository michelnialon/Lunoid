package com.nialon;
import java.io.IOException;
import java.util.Random;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
public class MyWidgetProvider extends AppWidgetProvider {

	  
	  @Override
	  public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) 
	  {
		  
		Log.d("widget","onupdate");
		try
	      {
			int number = (new Random().nextInt(100));
			    // Get all ids
			    ComponentName thisWidget = new ComponentName(context, MyWidgetProvider.class);
			    int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
			    for (int widgetId : allWidgetIds) 
			    {
			      RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);
			      // Set the text
			      //remoteViews.setTextViewText(R.id.lever, "12:34");
			      
			    	 remoteViews.setTextViewText(R.id.lever, Lunoid.getLever());
			    	 remoteViews.setTextViewText(R.id.coucher, Lunoid.getCoucher());
			      //remoteViews.setTextViewText(R.id.lever,String.valueOf(number));
			    
		
			      // Register an onClickListener
			      Intent intent = new Intent(context, MyWidgetProvider.class);
		
			      intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
			      intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
		
			      PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			      remoteViews.setOnClickPendingIntent(R.id.lever, pendingIntent);
			      remoteViews.setOnClickPendingIntent(R.id.coucher, pendingIntent);
			      appWidgetManager.updateAppWidget(widgetId, remoteViews);
			    }
	      }catch (Exception e) {Log.d("Exception1 :" , e.toString());}
	  }
	}