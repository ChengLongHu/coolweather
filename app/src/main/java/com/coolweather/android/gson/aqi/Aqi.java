package com.coolweather.android.gson.aqi;

import com.google.gson.annotations.SerializedName;

/**
 * Aqi值
 */

public class Aqi {
    //城市现在的空气质量
    @SerializedName("air_now_city")
    public Air_now_city airNowCity;

    //因为是一个组，所以用一个类包装起来
    public class Air_now_city{
        //aqi值
        @SerializedName("aqi")
        public String aqi;
        //pm2.5
        @SerializedName("pm25")
        public String pm25;
    }
}
