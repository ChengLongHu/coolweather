package com.coolweather.android;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.coolweather.android.gson.Weather;
import com.coolweather.android.gson.aqi.Aqi;
import com.coolweather.android.gson.forecast.Forecast;
import com.coolweather.android.gson.suggestion.Suggestions;
import com.coolweather.android.util.HttpUtil;
import com.coolweather.android.util.Utility;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    private static final String TAG = "WeatherActivity";
    //各个组件对象引用
    private ScrollView weatherLayout;
    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView degreeText;
    private TextView weatherInfoText;
    private LinearLayout forecastsLayout;
    private TextView aqiText;
    private TextView pm25Text;
    private TextView comfortText;
    private TextView carWashText;
    private TextView sportText;
    private ImageView bingPicImg;

    //数据对象
    private Weather weather;
    private List<Suggestions.Suggestion> suggestions;
    private List<Forecast.Daily_forecast> forecasts;
    private Aqi aqi;

    //异步处理值
    private static final int UPDATE_WEATHER = 1;
    private static final int UPDATE_SUGGESTION = 2;
    private static final int UPDATE_FORECAST = 3;
    private static final int UPDATE_AQI = 4;


    //异步处理器
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                //天气操作
                case UPDATE_WEATHER:
                    showWeatherInfo();
                    break;
                //建议操作
                case UPDATE_SUGGESTION:
                    showSuggestion();
                    break;
                //天气预报
                case UPDATE_FORECAST:
                    showForecast();
                    break;
                //AQI
                case UPDATE_AQI:
                    showAqi();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //窗体上部透明
        if(Build.VERSION.SDK_INT >= 21){
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);
        //初始化各控件
        bingPicImg = (ImageView)findViewById(R.id.bing_pic_img);
        weatherLayout = (ScrollView)findViewById(R.id.weather_layout);
        titleCity = (TextView)findViewById(R.id.title_city);
        titleUpdateTime = (TextView)findViewById(R.id.title_update);
        degreeText = (TextView)findViewById(R.id.degree_text);
        weatherInfoText = (TextView)findViewById(R.id.weather_info);
        forecastsLayout = (LinearLayout) findViewById(R.id.forecast_layout);
        aqiText = (TextView)findViewById(R.id.aqi_text);
        pm25Text = (TextView)findViewById(R.id.pm25_text);
        comfortText = (TextView)findViewById(R.id.comfort_text);
        carWashText = (TextView)findViewById(R.id.car_wash_text);
        sportText = (TextView)findViewById(R.id.sport_text);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather",null);
        if(weatherString != null && weather != null){
            //有缓存直接解析天气数据
            Weather weather = Utility.handleWeatherResponse(weatherString);
            Log.d("WeatherActivity", "数据库查的天气" + weather.toString());
            showWeatherInfo();
        } else {
            //无缓存时去服务器查询天气
            String weatherId = getIntent().getStringExtra("weather_id");
            Log.d("WeatherActivity", "网络查的天气 " + weatherId);
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(weatherId);
            requestSuggestion(weatherId);
            requestForecast(weatherId);
            requestAqi(weatherId);
        }
        //加载图片
        String bingPic = prefs.getString("bing_pic",null);
        if(bingPic != null){
            Glide.with(this).load(bingPic).into(bingPicImg);
        }else{
            loadBingPic();
        }

    }
    /**
     * 根据天气id请求城市天气信息
     */
    public void requestWeather(final String weatherId){
        String weatherUrl = "https://free-api.heweather.net/s6/weather/now?location=" + weatherId + "&key=bc0418b57b2d4918819d3974ac1285d9";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"获取天气信息失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                weather = Utility.handleWeatherResponse(responseText);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if(weather != null && "ok".equals(weather.status)){
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather",responseText);
                            editor.apply();
                            Message message = new Message();
                            message.what = UPDATE_WEATHER;
                            handler.sendMessage(message);
                        }
                    }
                }).start();

            }
        });
        loadBingPic();
    }

    private void requestSuggestion(final String weatherId){
        final String suggestUrl = "https://free-api.heweather.net/s6/weather/lifestyle?location=" + weatherId + "&key=bc0418b57b2d4918819d3974ac1285d9";
        HttpUtil.sendOkHttpRequest(suggestUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"获取天气建议信息失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final List<Suggestions.Suggestion> suggestionList = Utility.handleSuggestionResponse(responseText);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if(suggestionList != null){
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("suggestion",responseText);
                            editor.apply();
                            suggestions = suggestionList;
                            Message message = new Message();
                            message.what = UPDATE_SUGGESTION;
                            handler.sendMessage(message);
                        }
                    }
                }).start();
            }
        });
    }

    private void requestForecast(final String weatherId){
        final String suggestUrl = "https://free-api.heweather.net/s6/weather/forecast?location=" + weatherId + "&key=bc0418b57b2d4918819d3974ac1285d9";
        HttpUtil.sendOkHttpRequest(suggestUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(WeatherActivity.this,"获取预报信息失败",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final List<Forecast.Daily_forecast> forecastList = Utility.handleForecastResponse(responseText);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if(forecastList != null){
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("forecast",responseText);
                            editor.apply();
                            forecasts = forecastList;
                            Message message = new Message();
                            message.what = UPDATE_FORECAST;
                            handler.sendMessage(message);
                        }
                    }
                }).start();
            }
        });
    }

    private void requestAqi(final String weatherId){
        final String aqiUrl = "https://free-api.heweather.net/s6/air/now?location=" + weatherId + "&key=bc0418b57b2d4918819d3974ac1285d9";
        HttpUtil.sendOkHttpRequest(aqiUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(WeatherActivity.this,"获取空气预报信息失败",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                aqi = Utility.handleAqiResponse(responseText);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if(aqi != null){
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("aqi",responseText);
                            editor.apply();
                            Message message = new Message();
                            message.what = UPDATE_AQI;
                            handler.sendMessage(message);
                        }
                    }
                }).start();
            }
        });
    }
    /**
     * 处理并展示Weather实体类的数据
     */
    private void showWeatherInfo(){
        String cityName = weather.basic.cityName;
        String updateTime = weather.update.updateTime.split(" ")[1];
        String degree = weather.now.temperature + "℃";
        String weatherInfo = weather.now.Condition;
        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);

    }
    /**
     * 处理并展示Suggestion实体类的数据
     */
    private void showSuggestion(){
        String comfort = "舒适度: " + suggestions.get(0).info;
        String carWash = "洗车指数: " + suggestions.get(6).info;
        String sport = "运动建议: " + suggestions.get(3).info;
        comfortText.setText(comfort);
        carWashText.setText(carWash);
        sportText.setText(sport);
        weatherLayout.setVisibility(View.VISIBLE);
    }

    /**
     * 处理并展示Forecast实体类的数据
     */
    private void showForecast(){
        forecastsLayout.removeAllViews();
        for(Forecast.Daily_forecast forecast : forecasts){
            //动态加载布局
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item,forecastsLayout,false);
            TextView dataText = (TextView)view.findViewById(R.id.date_text);
            TextView infoText = (TextView)view.findViewById(R.id.info_text);
            TextView maxText = (TextView)view.findViewById(R.id.max_text);
            TextView minText = (TextView)view.findViewById(R.id.min_text);
            dataText.setText(forecast.date);
            infoText.setText(forecast.cond);
            maxText.setText(forecast.tmp_max);
            minText.setText(forecast.tmp_min);
            forecastsLayout.addView(view);
        }
        weatherLayout.setVisibility(View.VISIBLE);
    }

    private void showAqi(){
        if(aqi != null && aqi.airNowCity != null){
            String aqiData = aqi.airNowCity.aqi;
            String pm25 = aqi.airNowCity.pm25;
            aqiText.setText(aqiData);
            pm25Text.setText(pm25);
        }
    }

    /**
     * 加载必应每日一图
     */
    private void loadBingPic(){
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic",bingPic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                    }
                });
            }
        });
    }
}
