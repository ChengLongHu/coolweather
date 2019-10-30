package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 我爱王金ge on 2019/10/28.
 */

public class Now {
    //温度
    @SerializedName("tmp")
    public String temperature;

    //天气
    @SerializedName("cond_txt")
    public String Condition;
}
