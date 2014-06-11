package com.ace.fuzzyclock;

import java.util.Date;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;

public class FuzzyClock extends AppWidgetProvider {

  
     @Override
     public void onDisabled(Context context) {
        super.onDisabled(context);
        context.stopService(new Intent(context, UpdateService.class));
     }

     @Override
     public void onEnabled(Context context) {
         super.onEnabled(context);
         context.startService(new Intent(UpdateService.ACTION_UPDATE));
     }
     
     @Override
     public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
         super.onUpdate(context, appWidgetManager, appWidgetIds);
         context.startService(new Intent(UpdateService.ACTION_UPDATE));
     }
     

   

            public static class UpdateService extends Service {
        
            static final String ACTION_UPDATE = "com.ace.fuzzyclock.action.UPDATE";
      
        // Adding actions to react
        private final static IntentFilter ActionFilter;
        
          static {
            ActionFilter = new IntentFilter();
            ActionFilter.addAction(Intent.ACTION_TIME_TICK);
            ActionFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
            ActionFilter.addAction(Intent.ACTION_TIME_CHANGED);
            ActionFilter.addAction(Intent.ACTION_POWER_CONNECTED);
            ActionFilter.addAction(Intent.ACTION_POWER_DISCONNECTED);            
          }
      
          // registering receiver for actions.
          
          @Override
          public void onCreate() {
              super.onCreate();
              
              
              registerReceiver(ChangeReceiver, ActionFilter);
              update();
          }
          
          // running update() on service start.
          
          @Override
          public void onStart(Intent intent, int startId) {
              super.onStart(intent, startId);
                  update();
          }
          
          // unregistering the reciever on service destroy.
          @Override
          public void onDestroy() {
              super.onDestroy();
              unregisterReceiver(ChangeReceiver);
          }
          
          
          //update method. All the logics is here.
          
          public void update() {

            String min;
            String hour;
            Integer fm;
            Integer fh;
            Date curDate;
            Integer pad;
          
            curDate = new Date();
            
            //getting current Minutes and Hours
              fm=curDate.getMinutes();
              fh=curDate.getHours();
              
              //we don't need 24 format, and we don't care about am/pm
              if (fh>11) {
                fh=fh-12;
              }
              
              min="";
            hour="";
            pad=0;

          
            //building strings for a result
            if ((fm>=0) && (fm<=2)) {
                min=getString(R.string.exact);
                pad=1;
                fh=(fh==0)?12:fh;
              }

            if ((fm>2) && (fm<=7)) {
                min=getString(R.string.min5f);
                pad=0;
              }
            if ((fm>7) && (fm<=12)) {
                min=getString(R.string.min10f);
                pad=0;
              }
            if ((fm>12) && (fm<18)) {
                min=getString(R.string.min15f);
                pad=0;
              }
              if ((fm>=18) && (fm<25)) {
                min=getString(R.string.min20f);
                pad=0;
              }
              if ((fm>=25) && (fm<35)) {
                min=getString(R.string.min30);
                pad=0;
              }
              if ((fm>=35) && (fm<42)) { 
                min=getString(R.string.min20t);
                pad=1;
                fh++;
              }
              if ((fm>=42) && (fm<49)) {
                min=getString(R.string.min15t);
                pad=1;
                fh++;

              }
              if ((fm>=49) && (fm<53)) {
                min=getString(R.string.min10t);
                pad=1;
                fh++;

              }
              if ((fm>=53) && (fm<58)) {
                min=getString(R.string.min5t);
                pad=1;
                fh++;

              }
              if ((fm>=58) && (fm<60)) {
                min=getString(R.string.exact);
                pad=1;
                fh++;

              }
              
                          
              //We have two arrays of strings in resources, so just getting the needed array and needed entry.
              Resources res = getResources();
              String[] hours = (pad==1)?res.getStringArray(R.array.hours1):res.getStringArray(R.array.hours2);
              hour=hours[fh];

              //layout changes.
              RemoteViews updateViews = new RemoteViews(getPackageName(), R.layout.main);
              updateViews.setTextViewText(R.id.min_textview, min);
              updateViews.setTextViewText(R.id.hour_textview, hour);
              
              SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
              
              int color = prefs.getInt(Settings.COLOR, 3);
              String min_size = prefs.getString(Settings.MIN_SIZE, getResources().getString(R.string.default_min_size));
              String hour_size = prefs.getString(Settings.HOUR_SIZE, getResources().getString(R.string.default_hour_size));
              
        
              // Push update for this widget to the home screen
              ComponentName thisWidget = new ComponentName(this, FuzzyClock.class);
              AppWidgetManager manager = AppWidgetManager.getInstance(this);
              int[] appWidgetIds = manager.getAppWidgetIds(thisWidget);
              
              

      		String[] colors = getResources().getStringArray(R.array.color_values);
    		updateViews.setTextColor(R.id.min_textview, Color.parseColor(colors[color]));
    		updateViews.setTextColor(R.id.hour_textview, Color.parseColor(colors[color]));
              
              Intent intent = new Intent(this, Settings.class);
              intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[0]);  // Identifies the particular widget...
              intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
              // Make the pending intent unique...
            //intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
           //   PendingIntent pendIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            //  PendingIntent pendIntent = PendingIntent.getActivity(this, 0, intent, 0);

            //  updateViews.setOnClickPendingIntent(R.id.min_textview, pendIntent);
            //  updateViews.setOnClickPendingIntent(R.id.hour_textview, pendIntent);

 
              
              manager.updateAppWidget(thisWidget, updateViews);
              
              // logging the update just in case.
              Log.i("::updated::", Integer.toString(curDate.getHours()) + ":" + Integer.toString(fm));
          }
          
          //running update() on receiving the action set in filter.
          private final BroadcastReceiver ChangeReceiver = new BroadcastReceiver() {
              @Override
              public void onReceive(Context context, Intent intent) {
                  update();
              }
          };
          
          @Override
          public IBinder onBind(Intent intent) {
              // We don't need to bind to this service
              return null;
          }
      }



}