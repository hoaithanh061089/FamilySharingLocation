package thanhnguyen.com.familysharinglocation;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.valdesekamdem.library.mdtoast.MDToast;

import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

/**
 * Created by THANHNGUYEN on 11/12/17.
 */

public class WeatherActivity extends AppCompatActivity {
    Typeface weatherFont;
    TextView cityField;
    TextView updatedField;
    TextView detailsField;
    TextView currentTemperatureField;
    TextView weatherIcon;
    String weatherlatitude;
    String weatherlongtitude;
    Handler handler;
    String weathercondition_unplash;
    String weathercondition_unplash_lastword;
    RelativeLayout weatherinfolayout;
    ProgressBar progressbar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FamilySharingApplicationClass.getDefaultTracker(this);
        Fresco.initialize(this);
        if (Build.VERSION.SDK_INT < 16)
        {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        else
        {
            View decorView = getWindow().getDecorView();
            // Hide the status bar.
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
        setContentView(R.layout.weatherlayout);

        handler = new Handler();
        weatherinfolayout = (RelativeLayout) findViewById(R.id.weatherinfolayout);
        cityField = (TextView) findViewById(R.id.city_field);
        updatedField = (TextView) findViewById(R.id.updated_field);
        detailsField = (TextView) findViewById(R.id.details_field);
        currentTemperatureField = (TextView) findViewById(R.id.current_temperature_field);
        weatherIcon = (TextView) findViewById(R.id.weather_icon);
        weatherFont = Typeface.createFromAsset(getAssets(), "weatherfont.ttf");
        weatherIcon.setTypeface(weatherFont);

        weatherlatitude =  getIntent().getStringExtra("weatherlatitude");
        weatherlongtitude = getIntent().getStringExtra("weatherlongtitude");
        progressbar = (ProgressBar) findViewById(R.id.processbar);


       // Toast.makeText(getBaseContext(), cityName, Toast.LENGTH_SHORT).show();
        updateWeatherData(weatherlatitude, weatherlongtitude);



    }

    private void updateWeatherData(final String lat, final String lon){
        new Thread(){
            public void run(){
                final JSONObject json = RemoteFetchWeather.getJSON(getBaseContext(), lat, lon );
                if(json == null){
                    handler.post(new Runnable(){
                        public void run(){

                            MDToast mdToast = MDToast.makeText(getBaseContext(), "No weather data found, please check your internet connection! .", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR);
                            mdToast.show();
                            finish();



                        }
                    });
                } else {
                    handler.post(new Runnable(){
                        public void run(){
                            renderWeather(json);
                        }
                    });
                }
            }
        }.start();
    }

    private void renderWeather(JSONObject json){
        try {
            cityField.setText(json.getString("name").toUpperCase(Locale.US) +
                    ", " +
                    json.getJSONObject("sys").getString("country"));

            JSONObject details = json.getJSONArray("weather").getJSONObject(0);
            JSONObject main = json.getJSONObject("main");

            weathercondition_unplash = details.getString("description");
            weathercondition_unplash_lastword = weathercondition_unplash.substring(weathercondition_unplash.lastIndexOf(" ")+1);
            Random r = new Random();
            int random = r.nextInt(1000);
            Uri uri = Uri.parse("https://source.unsplash.com/1080x1920/?"+weathercondition_unplash_lastword+"/" +  String.valueOf(random));
            SimpleDraweeView draweeView = (SimpleDraweeView) findViewById(R.id.backdrop);
           // draweeView.setImageURI(uri);


            cityField.setTextColor(Color.WHITE);
            updatedField.setTextColor(Color.WHITE);
            detailsField.setTextColor(Color.WHITE);
            currentTemperatureField.setTextColor(Color.WHITE);
            weatherIcon.setTextColor(Color.WHITE);

            DraweeController controller = Fresco.newDraweeControllerBuilder().setImageRequest(
                    ImageRequestBuilder.newBuilderWithSource(uri)
                            .build())
                    .setControllerListener(new BaseControllerListener<ImageInfo>() {
                        @Override
                        public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {

                            progressbar.setVisibility(View.INVISIBLE);

                        }
                    })
                    .build();
            draweeView.setController(controller);



            detailsField.setText(
                    details.getString("description").toUpperCase(Locale.US) +
                            "\n" + "Humidity: " + main.getString("humidity") + "%" +
                            "\n" + "Pressure: " + main.getString("pressure") + " hPa");

            currentTemperatureField.setText(
                    String.format("%.2f", main.getDouble("temp"))+ " ℃");

            DateFormat df = DateFormat.getDateTimeInstance();
            String updatedOn = df.format(new Date(json.getLong("dt")*1000));
            updatedField.setText("Last update: " + updatedOn);

            setWeatherIcon(details.getInt("id"),
                    json.getJSONObject("sys").getLong("sunrise") * 1000,
                    json.getJSONObject("sys").getLong("sunset") * 1000);

        }catch(Exception e){
            Log.e("SimpleWeather", "One or more fields not found in the JSON data");
        }
    }
    private void setWeatherIcon(int actualId, long sunrise, long sunset){
        int id = actualId / 100;
        String icon = "";
        if(actualId == 800){
            long currentTime = new Date().getTime();
            if(currentTime>=sunrise && currentTime<sunset) {
                icon =  getString(R.string.weather_sunny);
            } else {
                icon = getString(R.string.weather_clear_night);
            }
        } else {
            switch(id) {
                case 2 : icon = getString(R.string.weather_thunder);
                    break;
                case 3 : icon = getString(R.string.weather_drizzle);
                    break;
                case 7 : icon = getString(R.string.weather_foggy);
                    break;
                case 8 : icon = getString(R.string.weather_cloudy);
                    break;
                case 6 : icon = getString(R.string.weather_snowy);
                    break;
                case 5 : icon = getString(R.string.weather_rainy);
                    break;
            }
        }
        weatherIcon.setText(icon);
    }




}


