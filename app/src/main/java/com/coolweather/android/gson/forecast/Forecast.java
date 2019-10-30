package com.coolweather.android.gson.forecast;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 天气预报
 */

public class Forecast {
    //天气预报类  --一组值
    @SerializedName("daily_forecast")
    public List<Daily_forecast> daily_forecastList;

    public class Daily_forecast{
        //日期
        @SerializedName("date")
        public String date;
        //最高温度
        @SerializedName("tmp_max")
        public String tmp_max;
        //最低温度
        @SerializedName("tmp_min")
        public String tmp_min;
        //天气状况
        @SerializedName("cond_txt_d")
        public String cond;
    }
}
