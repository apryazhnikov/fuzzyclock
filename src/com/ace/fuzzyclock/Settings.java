package com.ace.fuzzyclock;

import java.util.Date;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RemoteViews;
import android.widget.Spinner;


public class Settings extends Activity {

    private EditText minSize;
    private EditText hourSize;
    private Button saveBtn;
    private CheckBox enableShadow;
    private int widgetID;
    private Spinner colorSpinner;
    public SharedPreferences prefs;
    private String min;
    private String hour;
	public static final String MIN_SIZE = "MIN_SIZE";
	public static final String HOUR_SIZE = "HOUR_SIZE";
	public static final String COLOR = "COLOR";
	private String min_size;
	private String hour_size;


    
    
	private void savePreferences() {
		int color = colorSpinner.getSelectedItemPosition();
		Editor editor = prefs.edit();
		editor.putString(MIN_SIZE, min_size);
		editor.putString(HOUR_SIZE, hour_size);
		editor.putInt(COLOR, color);
		editor.commit();
	}
    
    public void updateWidget(){
    	

      //  updateViews.setFloat(R.id.hour_textview, "setPaddingRelative", "0 a 0 0");
       // ComponentName thisWidget = new ComponentName(this, FuzzyClock.class);
        

      

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
          //fh=(fh==0)?12:fh;
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
        
        
		min_size = minSize.getText().toString();
		hour_size = hourSize.getText().toString();
		int color = colorSpinner.getSelectedItemPosition();

		
        
		RemoteViews updateViews = new RemoteViews(getPackageName(), R.layout.main);
        //updateViews.setTextViewText(R.id.min_textview, min);
        updateViews.setFloat(R.id.min_textview, "setTextSize", Float.valueOf(min_size));
        updateViews.setFloat(R.id.hour_textview, "setTextSize", Float.valueOf(hour_size));
        //layout changes.
        //RemoteViews updateViews = new RemoteViews(getPackageName(), R.layout.main);
        updateViews.setTextViewText(R.id.min_textview, min);
        updateViews.setTextViewText(R.id.hour_textview, hour);     
		String[] colors = getResources().getStringArray(R.array.color_values);
		updateViews.setTextColor(R.id.min_textview, Color.parseColor(colors[color]));
		updateViews.setTextColor(R.id.hour_textview, Color.parseColor(colors[color]));
		savePreferences();

       
        AppWidgetManager manager = AppWidgetManager.getInstance(this);
        manager.updateAppWidget(widgetID, updateViews);
        
      Intent resultValue = new Intent();
      resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID);
      setResult(RESULT_OK, resultValue);
      finish();
//                 		String text=fontSize.getText().toString();
    }

     
   
    @Override
	protected void onCreate(Bundle savedInstanceState) {
	       super.onCreate(savedInstanceState);
	       
			Context context = getApplicationContext();
			prefs=PreferenceManager.getDefaultSharedPreferences(context);
			int color = prefs.getInt(COLOR, 1);
			String hour_size = prefs.getString(HOUR_SIZE, getResources().getString(R.string.default_hour_size));
			String min_size = prefs.getString(MIN_SIZE, getResources().getString(R.string.default_min_size));
			
			
	       
	      // widgetID = AppWidgetManager.INVALID_APPWIDGET_ID;
	       Intent intent = getIntent();
	       Bundle extras = intent.getExtras();
	       if (extras != null)
	          {
	    	   Log.i("::updated::", "extras NOT null");
	          widgetID = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, 
	                                       AppWidgetManager.INVALID_APPWIDGET_ID);
	          }
	       //No valid ID, so bail out.
	       if (widgetID == AppWidgetManager.INVALID_APPWIDGET_ID)
	       {
	    	   
	    	   Log.i("::updated::", "no VALID WidgetID");

	          // finish();
	           
	       }



	       //If the user press BACK, do not add any widget.
	       setResult(RESULT_CANCELED);
	       
	       setContentView(R.layout.settings);
	        minSize=(EditText) findViewById(R.id.min_size);
	        hourSize=(EditText) findViewById(R.id.hour_size);
	        saveBtn=(Button) findViewById(R.id.save_button);
//	        enableShadow=(CheckBox) findViewById(R.id.chkShadow);
	        colorSpinner=(Spinner) findViewById(R.id.color_spnner);
	    
	        
		       ArrayAdapter<CharSequence> cAdapter;
				int spinner_dd_item=android.R.layout.simple_spinner_dropdown_item;
				cAdapter=ArrayAdapter.createFromResource(this, R.array.colors, android.R.layout.simple_spinner_item);
				cAdapter.setDropDownViewResource(spinner_dd_item);
				colorSpinner.setAdapter(cAdapter);
				minSize.setText(min_size);
				hourSize.setText(hour_size);
				colorSpinner.setSelection(color);

	        
	        saveBtn.setOnClickListener(new OnClickListener()
	        {
	        	public void onClick(View v){

	        			updateWidget();
	        	}
	        });
	        
	 	}
	        
 }
