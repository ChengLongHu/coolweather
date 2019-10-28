package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 我爱王金ge on 2019/10/28.
 */

public class Basic {

    @SerializedName("city")
    public String cityName;

    @SerializedName("id")
    public String weatherId;

    public Update update;

    public class Update{
        @SerializedName("loc")
        public String updateTime;
    }
}
