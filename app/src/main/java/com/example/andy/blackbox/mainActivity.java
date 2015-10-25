package com.example.andy.blackbox;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.Timer;
import java.util.TimerTask;
import android.app.Fragment;
import android.app.AlertDialog;
import android.content.DialogInterface;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLConnection;
import java.io.InputStream;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.common.api.*;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import java.math.BigInteger;
import java.util.List;

public class mainActivity extends AppCompatActivity implements SensorEventListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener
{

    final String numberToDial = "9787901132";

    protected boolean trackingState = false;
    protected boolean preparingToCall = false;

    private float mSensorX;
    private float mSensorY;
    private Display mDisplay;
    private SensorManager sm;
    private PowerManager mPowerManager;
    private WindowManager mWindowManager;

    double gravity[] = new double[3];
    double linear_acceleration[] = new double[3];

    GoogleApiClient locationClient;
    Location mLastLocation;
    String latitudeText;
    String longitudeText;

    long elapsed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ImageView button = (ImageView) findViewById(R.id.imageView);
        button.setOnClickListener(new View.OnClickListener(){public void onClick(View v){
            if (preparingToCall){
                preparingToCall = false;
                return;
            }
            onToggleButtonClick(button);
        }});

        // Get an instance of the SensorManager
        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        if(sm.getSensorList(Sensor.TYPE_ACCELEROMETER).size()!=0){
            Sensor s = sm.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);
            sm.registerListener(this,s, SensorManager.SENSOR_DELAY_NORMAL);
        }

        // Get an instance of the PowerManager
        mPowerManager = (PowerManager) getSystemService(POWER_SERVICE);

        // Get an instance of the WindowManager
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mDisplay = mWindowManager.getDefaultDisplay();


        buildGoogleApiClient();

        ((TextView)findViewById(R.id.detectionTextView)).setEnabled(false);
        ((TextView)findViewById(R.id.detectionTextView)).setVisibility(View.INVISIBLE);
    }

    protected synchronized void buildGoogleApiClient() {
        System.out.println("Building api client");
        locationClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onToggleButtonClick(ImageView button){
        if(preparingToCall){

        }
        this.trackingState = !this.trackingState;
        this.drawToggleButton(button);
        System.out.println(this.trackingState);
    }

    public void drawToggleButton(ImageView button) {
        if (trackingState == false) {
            ((TextView)findViewById(R.id.textView2)).setText("Disengaged");
            ((TextView)findViewById(R.id.textView2)).setTextColor(0xFFD0021B);
            button.setImageResource(R.drawable.toggle_image_button_disabled);
        } else {
            ((TextView)findViewById(R.id.textView2)).setText("Engaged");
            ((TextView)findViewById(R.id.textView2)).setTextColor(0xFF2B6DE5);
            button.setImageResource(R.drawable.toggle_image_button_enabled);
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    public void onSensorChanged(SensorEvent event)
    {
        if(trackingState==false)return;

        // alpha is calculated as t / (t + dT)
        // with t, the low-pass filter's time-constant
        // and dT, the event delivery rate
        final float alpha = (float) 0.8;

        gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
        gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
        gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

        linear_acceleration[0] = event.values[0] - gravity[0];
        linear_acceleration[1] = event.values[1] - gravity[1];
        linear_acceleration[2] = event.values[2] - gravity[2];

        double total = Math.sqrt(linear_acceleration[0]*linear_acceleration[0]+linear_acceleration[1]*linear_acceleration[1]+linear_acceleration[2]*linear_acceleration[2])/9.8;
        String t = ""+total;
        if(total>2.5){
            tryCall();
        }

    }

    public void tryCall() {
        elapsed = 0;
        final long INTERVAL = 1000;
        final long TIMEOUT = 10000;
        onToggleButtonClick((ImageView) findViewById(R.id.imageView)); // Change trackingState to false
        System.out.println("Collision Detected!");
        ((TextView) findViewById(R.id.detectionTextView)).setText("COLLISION DETECTED! CONTACTING EMERGENCY SERVICES IN: " + TIMEOUT / 1000 + " SECONDS!");
        ((TextView) findViewById(R.id.textView2)).setText("Detected");
        ((TextView) findViewById(R.id.textView2)).setTextColor(0xFFF5A623);
        preparingToCall = true;
        trackingState = false;
        ((ImageView)findViewById(R.id.imageView)).setImageResource(R.drawable.toggle_image_button_crash_cancel);
        ((TextView)findViewById(R.id.detectionTextView)).setEnabled(true);
        ((TextView)findViewById(R.id.detectionTextView)).setVisibility(View.VISIBLE);

        TimerTask task = new TimerTask(){
            @Override
            public void run(){
                if (preparingToCall == false){
                    this.cancel();
                    makeDetectInvis();
                    trackingState = true;
                    mainActivity.this.runOnUiThread(new Runnable(){
                        public void run() {drawToggleButton((ImageView) findViewById(R.id.imageView));}});
                }
                elapsed += INTERVAL;
                if (elapsed >= TIMEOUT){
                    this.cancel();
                    locationClient.connect();
                    System.out.println("Making the call");
                }
                changeDetectTest((int)(TIMEOUT-elapsed)/1000);
            }
        };
        elapsed = 0;
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(task,INTERVAL,INTERVAL);

    }

    public void makeDetectInvis(){
        mainActivity.this.runOnUiThread(new Runnable(){
            public void run() {
                ((TextView) findViewById(R.id.detectionTextView)).setEnabled(false);
                ((TextView) findViewById(R.id.detectionTextView)).setVisibility(View.INVISIBLE);
            }
        });
    }

    public void changeDetectTest(final int i) {
        mainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((TextView) findViewById(R.id.detectionTextView)).setText("COLLISION DETECTED! CONTACTING EMERGENCY SERVICES IN: " + i + " seconds!");
            }
        });
    }


    public void makeCall(){
        String myUrl = "http://api.tropo.com/1.0/sessions?action=create&token=0fd962523443224f87fee1f1be089f59a4fd62655f9a02d49aa74478f9b159ae18906978562fa27898c724a2&numberToDial="+ numberToDial + "&userLatitude=" + latitudeText + "&userLongitude=" + longitudeText;
        URLConnection connection;
        try{
            connection = (URLConnection) new URL(myUrl).openConnection();
        } catch(Exception e){
            System.out.println("Error sending request");
            return;
        }
        try {
            InputStream response = connection.getInputStream();
        }catch (IOException e){
            System.out.println("Error receiving response");
        }

    }


    public void onConnected(Bundle connectionHint) {

        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity.this);
        builder.setMessage("An alert has been sent! You should follow still follow up with another call!")
                .setTitle("Alert Sent");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                mainActivity.this.runOnUiThread(new Runnable(){
                    public void run() {
                        trackingState = false;
                        drawToggleButton((ImageView) findViewById(R.id.imageView));
                        ((TextView) findViewById(R.id.detectionTextView)).setEnabled(false);
                        ((TextView) findViewById(R.id.detectionTextView)).setVisibility(View.INVISIBLE);
                    }});
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                locationClient);
        android.os.SystemClock.sleep(2000);
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                locationClient);
        android.os.SystemClock.sleep(1000);
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                locationClient);
        if (mLastLocation != null) {
            latitudeText = String.valueOf(mLastLocation.getLatitude());
            longitudeText = String.valueOf(mLastLocation.getLongitude());
        }
        System.out.println(latitudeText);
        System.out.println(longitudeText);

        Runnable runnable = new Runnable(){
            public void run(){
                makeCall();
                return;
            }
        };
        Thread mythread = new Thread(runnable);
        mythread.start();

        locationClient.disconnect();
    }
    public void onConnectionSuspended(int i){
        System.out.println("Connection suspended to location services");
    }
    public void onConnectionFailed(ConnectionResult r){
        System.out.println("Connection failed to location services");
    }
}
