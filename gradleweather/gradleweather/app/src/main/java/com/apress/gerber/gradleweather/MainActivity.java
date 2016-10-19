package com.apress.gerber.gradleweather;

import android.app.Dialog;
import android.app.ListActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.Calendar;
import java.util.Map;


public class MainActivity extends ListActivity implements Runnable{

    private Handler handler;
    private TemperatureAdapter temperatureAdapter;
    private TemperatureData temperatureData;
    private Dialog splashDialog;
    String [] weekdays = {
            "Sunday","Monday","Tuesday",
            "Wednesday","Thursday","Friday",
            "Saturday"
    };
    private ImageLoader imageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageLoaderConfiguration config = new ImageLoaderConfiguration
                .Builder(getApplicationContext())
                .build();
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(config);
        temperatureAdapter = new TemperatureAdapter(this,imageLoader);
        setListAdapter(temperatureAdapter);
        showSplashScreen();
        handler = new Handler();
        AsyncTask.execute(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        dismissSplashScreen();
    }

    private void showSplashScreen() {
        splashDialog = new Dialog(this, R.style.splash_screen);
        splashDialog.setContentView(R.layout.activity_splash);
        splashDialog.setCancelable(false);
        splashDialog.show();
    }

    private void dismissSplashScreen() {
        if (splashDialog!=null) {
            splashDialog.dismiss();
            splashDialog = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void run() {
        temperatureData = new NationalWeatherRequestData(this);
        // Set Runnable to remove splash screen just in case
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                temperatureAdapter.setTemperatureData(temperatureData);
                ((TextView) findViewById(R.id.city)).setText(temperatureData.getCity());
                ((TextView) findViewById(R.id.currentDayOfWeek)).setText(weekdays[Calendar.getInstance().get(Calendar.DAY_OF_WEEK)-1]);
                Map<String, String> currentConditions = temperatureData.getCurrentConditions();
                if (!currentConditions.isEmpty()) {
                    ((TextView) findViewById(R.id.currentTemperature)).setText(currentConditions.get(ExampleTemperatureData.CURRENT));
                    ((TextView) findViewById(R.id.currentDewPoint)).setText(currentConditions.get(ExampleTemperatureData.DEW_POINT));
                    ((TextView) findViewById(R.id.currentHigh)).setText(currentConditions.get(ExampleTemperatureData.HIGH));
                    ((TextView) findViewById(R.id.currentLow)).setText(currentConditions.get(ExampleTemperatureData.LOW));
                } else {
                    ((TextView) findViewById(R.id.currentTemperature)).setText("?");
                    ((TextView) findViewById(R.id.currentDewPoint)).setText("?");
                    ((TextView) findViewById(R.id.currentHigh)).setText("?");
                    ((TextView) findViewById(R.id.currentLow)).setText("?");
                }
                dismissSplashScreen();
            }
        }, 5000);
    }
}
